import os
import re
import sys
import json

# 匹配中文字符、全角标点符号以及常用中文标点
CHINESE_CHARACTER_PATTERN = re.compile(r'[\u4e00-\u9fff]')
FULL_WIDTH_PUNCTUATION_PATTERN = re.compile(r'[\uff01-\uff5e]')
CJK_PUNCTUATION_PATTERN = re.compile(r'[\u3000-\u303f]')

# 忽略扫描的目录与文件名称
IGNORED_DIRECTORIES = {'.git', '.idea', '.vscode', '__pycache__', 'bin', '.settings'}
IGNORED_FILES = {'task.md', '检查中文回复建议'}


def is_ignored_directory(directory_name):
    # 过滤版本控制与构建生成的临时目录，避免无意义的扫描开销
    if directory_name in IGNORED_DIRECTORIES:
        return True
    return False


def is_ignored_file(file_name):
    # 过滤任务说明文档与历史参考文档自身
    if file_name in IGNORED_FILES:
        return True
    return False


def is_binary_file(file_path):
    # 针对可能存在的非文本文件进行后缀判断
    binary_extensions = {'.jar', '.class', '.png', '.jpg', '.jpeg', '.gif', '.zip', '.tar', '.gz'}
    _, extension = os.path.splitext(file_path)
    if extension.lower() in binary_extensions:
        return True
    return False


def find_chinese_in_line(line_content):
    # 查找一行中所有中文字符或全角标点
    characters = []
    
    chinese_matches = CHINESE_CHARACTER_PATTERN.findall(line_content)
    if chinese_matches:
        characters.extend(chinese_matches)
        
    full_width_matches = FULL_WIDTH_PUNCTUATION_PATTERN.findall(line_content)
    if full_width_matches:
        characters.extend(full_width_matches)
        
    cjk_matches = CJK_PUNCTUATION_PATTERN.findall(line_content)
    if cjk_matches:
        characters.extend(cjk_matches)
        
    return characters


def scan_single_file(file_path):
    # 读取单个文件并按行检索中文
    if is_binary_file(file_path):
        return []

    matched_lines = []
    try:
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as file_pointer:
            for line_number, line_content in enumerate(file_pointer, start=1):
                found_characters = find_chinese_in_line(line_content)
                if not found_characters:
                    continue

                matched_lines.append({
                    'line_number': line_number,
                    'preview': line_content.strip(),
                    'characters': list(set(found_characters))
                })
    except Exception as read_error:
        print(f"[警告] 无法读取文件: {file_path}, 原因: {read_error}")

    return matched_lines


def scan_target_directory(root_directory):
    # 遍历目标目录并汇总所有命中文件
    scan_results = {}

    for current_root, directories, files in os.walk(root_directory):
        # 扁平化过滤忽略目录
        directories[:] = [d for d in directories if not is_ignored_directory(d)]

        for file_name in files:
            if is_ignored_file(file_name):
                continue

            absolute_file_path = os.path.join(current_root, file_name)
            relative_file_path = os.path.relpath(absolute_file_path, root_directory)

            matched_lines = scan_single_file(absolute_file_path)
            if not matched_lines:
                continue

            scan_results[relative_file_path] = matched_lines

    return scan_results


def classify_files_by_module(scan_results):
    # 将命中文件归类到不同的业务模块，便于统计和分步排查
    categorized_results = {
        'Benchmarks': {},
        'Scripts': {},
        'Raw_Results': {},
        'Showcase': {},
        'Other': {}
    }

    for file_path, matches in scan_results.items():
        if 'Benchmarks' in file_path:
            categorized_results['Benchmarks'][file_path] = matches
            continue
        if 'Scripts' in file_path:
            categorized_results['Scripts'][file_path] = matches
            continue
        if 'Raw Results' in file_path:
            categorized_results['Raw_Results'][file_path] = matches
            continue
        if 'EcoreGenTLShowcase' in file_path:
            categorized_results['Showcase'][file_path] = matches
            continue
        categorized_results['Other'][file_path] = matches

    return categorized_results


def output_summary_report(categorized_results, output_json_path=None):
    # 打印终端汇总信息
    total_files = sum(len(module_files) for module_files in categorized_results.values())
    total_lines = sum(
        len(matches)
        for module_files in categorized_results.values()
        for matches in module_files.values()
    )

    print("=" * 60)
    print("【全库中文与全角标点检测结果汇总】")
    print(f"总计命中文件数: {total_files}")
    print(f"总计命中行数:   {total_lines}")
    print("-" * 60)

    for module_name, module_files in categorized_results.items():
        module_line_count = sum(len(matches) for matches in module_files.values())
        print(f"模块 [{module_name}]: {len(module_files)} 个文件, 共 {module_line_count} 行")

    print("=" * 60)

    # 如指定输出路径，则保存完整 JSON 数据
    if not output_json_path:
        return

    summary_data = {
        'total_files': total_files,
        'total_lines': total_lines,
        'modules': {
            module_name: {
                'file_count': len(module_files),
                'line_count': sum(len(matches) for matches in module_files.values()),
                'files': module_files
            }
            for module_name, module_files in categorized_results.items()
        }
    }

    with open(output_json_path, 'w', encoding='utf-8') as json_file:
        json.dump(summary_data, json_file, ensure_ascii=False, indent=2)
    print(f"完整报告已保存至: {output_json_path}")


def main():
    target_directory = "Supplements"
    if len(sys.argv) > 1:
        target_directory = sys.argv[1]

    output_json_path = "scripts/baseline_chinese_report.json"
    if len(sys.argv) > 2:
        output_json_path = sys.argv[2]

    if not os.path.exists(target_directory):
        print(f"[错误] 目标目录不存在: {target_directory}")
        sys.exit(1)

    print(f"开始扫描目录: {target_directory} ...")
    raw_results = scan_target_directory(target_directory)
    categorized = classify_files_by_module(raw_results)
    output_summary_report(categorized, output_json_path)


if __name__ == '__main__':
    main()
