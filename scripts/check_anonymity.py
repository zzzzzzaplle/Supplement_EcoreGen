import os
import re
import sys
import json

# 敏感词模式定义
AUTHOR_NAME_PATTERN = re.compile(r'zeqing', re.IGNORECASE)
INSTITUTION_NAME_PATTERN = re.compile(r'ustb', re.IGNORECASE)
# 准确匹配 Windows 盘符绝对路径 (如 C:\Users\...)，排除 access:\n 等非路径情况
HARDCODED_WINDOWS_PATH_PATTERN = re.compile(r'(?<![a-zA-Z])[a-zA-Z]:\\[a-zA-Z0-9_\\\.\-]+')
# 匹配 Unix 风格的用户主目录绝对路径 (如 /Users/xxx 或 /home/xxx)
HARDCODED_UNIX_PATH_PATTERN = re.compile(r'/(?:Users|home)/[a-zA-Z0-9_\.\-]+')
ONEDRIVE_PATTERN = re.compile(r'onedrive', re.IGNORECASE)

# 忽略扫描的目录与文件名称
IGNORED_DIRECTORIES = {'.git', '.idea', '.vscode', '__pycache__', 'bin', '.settings', 'scripts'}
IGNORED_FILES = {'task.md', '检查中文回复建议', 'baseline_chinese_report.json', 'baseline_anonymity_report.json'}


def is_ignored_directory(directory_name):
    # 忽略版本控制与辅助工具脚本自身目录
    if directory_name in IGNORED_DIRECTORIES:
        return True
    return False


def is_ignored_file(file_name):
    # 忽略任务看板与排查记录文档
    if file_name in IGNORED_FILES:
        return True
    return False


def is_binary_file(file_path):
    # 针对二进制文件跳过文本扫描
    binary_extensions = {'.jar', '.class', '.png', '.jpg', '.jpeg', '.gif', '.zip'}
    _, extension = os.path.splitext(file_path)
    if extension.lower() in binary_extensions:
        return True
    return False


def check_line_for_anonymity_risks(line_content):
    # 逐行检查是否存在双盲评审违规敏感信息
    risk_findings = []

    # 1. 检查作者姓名拼音 (极高危双盲违规)
    if AUTHOR_NAME_PATTERN.search(line_content):
        risk_findings.append('AUTHOR_NAME_ZEQING')

    # 2. 检查本地 OneDrive 路径特征
    if ONEDRIVE_PATTERN.search(line_content):
        risk_findings.append('LOCAL_ONEDRIVE_PATH')

    # 3. 检查 Windows 盘符或 Unix 用户目录绝对路径
    if HARDCODED_WINDOWS_PATH_PATTERN.search(line_content) or HARDCODED_UNIX_PATH_PATTERN.search(line_content):
        risk_findings.append('HARDCODED_ABSOLUTE_PATH')

    # 4. 检查机构全称或域名标识 (如 ustb.edu.cn / edu.ustb)
    if INSTITUTION_NAME_PATTERN.search(line_content):
        risk_findings.append('INSTITUTION_USTB')

    return risk_findings


def scan_file_for_anonymity(file_path):
    # 扫描单个文件的匿名性风险
    if is_binary_file(file_path):
        return []

    matched_lines = []
    try:
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as file_pointer:
            for line_number, line_content in enumerate(file_pointer, start=1):
                findings = check_line_for_anonymity_risks(line_content)
                if not findings:
                    continue

                matched_lines.append({
                    'line_number': line_number,
                    'risks': findings,
                    'preview': line_content.strip()[:160]
                })
    except Exception as read_error:
        print(f"[警告] 读取文件失败: {file_path}, 原因: {read_error}")

    return matched_lines


def scan_target_directory(root_directory):
    # 遍历目标目录并汇总匿名性风险
    scan_results = {}

    for current_root, directories, files in os.walk(root_directory):
        directories[:] = [d for d in directories if not is_ignored_directory(d)]

        for file_name in files:
            if is_ignored_file(file_name):
                continue

            absolute_file_path = os.path.join(current_root, file_name)
            relative_file_path = os.path.relpath(absolute_file_path, root_directory)

            matched_lines = scan_file_for_anonymity(absolute_file_path)
            if not matched_lines:
                continue

            scan_results[relative_file_path] = matched_lines

    return scan_results


def classify_anonymity_results(scan_results):
    # 将风险结果按严重程度与类型分类
    author_name_files = {}
    path_leak_files = {}
    institution_annotation_files = {}

    for file_path, matches in scan_results.items():
        has_author = any('AUTHOR_NAME_ZEQING' in item['risks'] for item in matches)
        has_path = any(('HARDCODED_ABSOLUTE_PATH' in item['risks'] or 'LOCAL_ONEDRIVE_PATH' in item['risks']) for item in matches)
        has_institution = any('INSTITUTION_USTB' in item['risks'] for item in matches)

        if has_author:
            author_name_files[file_path] = matches

        if has_path:
            path_leak_files[file_path] = matches

        if has_institution:
            institution_annotation_files[file_path] = matches

    return {
        'author_name_files': author_name_files,
        'path_leak_files': path_leak_files,
        'institution_annotation_files': institution_annotation_files
    }


def output_anonymity_report(classified_results, output_json_path=None):
    # 打印双盲排查概要
    author_files = classified_results['author_name_files']
    path_files = classified_results['path_leak_files']
    inst_files = classified_results['institution_annotation_files']

    print("=" * 60)
    print("【双盲评审（Double-Blind）匿名性检测结果】")
    print(f"1. 作者姓名 (Zeqing) 泄密文件数:       {len(author_files)} 个")
    print(f"2. 本地硬编码绝对路径泄密文件数:         {len(path_files)} 个")
    print(f"3. 机构标识 (USTB/URI 注解) 文件数:     {len(inst_files)} 个")
    print("-" * 60)

    if author_files:
        print("高危作者泄密文件清单:")
        for file_path in author_files:
            print(f"  - {file_path}")

    if path_files:
        print("高危绝对路径泄密文件清单:")
        for file_path in path_files:
            print(f"  - {file_path}")

    print("=" * 60)

    if not output_json_path:
        return

    summary_data = {
        'author_name_files_count': len(author_files),
        'path_leak_files_count': len(path_files),
        'institution_files_count': len(inst_files),
        'author_files': author_files,
        'path_files': path_files
    }

    with open(output_json_path, 'w', encoding='utf-8') as json_file:
        json.dump(summary_data, json_file, ensure_ascii=False, indent=2)
    print(f"匿名性报告已保存至: {output_json_path}")


def main():
    target_directory = "Supplements"
    if len(sys.argv) > 1:
        target_directory = sys.argv[1]

    output_json_path = "scripts/baseline_anonymity_report.json"
    if len(sys.argv) > 2:
        output_json_path = sys.argv[2]

    if not os.path.exists(target_directory):
        print(f"[错误] 目标目录不存在: {target_directory}")
        sys.exit(1)

    print(f"开始扫描匿名性风险: {target_directory} ...")
    raw_results = scan_target_directory(target_directory)
    classified = classify_anonymity_results(raw_results)
    output_anonymity_report(classified, output_json_path)


if __name__ == '__main__':
    main()
