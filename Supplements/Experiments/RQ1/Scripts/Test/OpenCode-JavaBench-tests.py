"""
javabench_codetest.py
Batch compile and test generated code under each paXX-result / ModelName / sampleN directory:
  1. javac compile (generated code + testcode/src)
  2. JUnit ConsoleLauncher run tests
  3. Output sample-level CSV report and global summary CSV
"""

import csv
import re
import shutil
import subprocess
import sys
from datetime import datetime
from itertools import product
from pathlib import Path

# ============================================================
# Configuration
# ============================================================
MODEL_NAMES = [ "qwen3.6-flash","deepseek-v4-flash","MiniMax-M3",
 "gemini-3.1-flash-lite", "gpt-5.4-mini"] #,"deepseek-v4-flash","gemini-3.1-flash-lite","MiniMax-M3","deepseek-v4-flash", "qwen3.6-flash"
BASE_DIRS  = ["pa19-result", "pa20-result", "pa21-result", "pa22-result"]
SAMPLES    = ["sample1", "sample2", "sample3", "sample4", "sample5"] #
LIB_DIR    = Path("lib")
LIB_JAR    = LIB_DIR / "junit-platform-console-standalone-1.13.3.jar"
REPORT_DIR = Path("test_reports")



# ============================================================
# Utility functions
# ============================================================

def _classpath_sep() -> str:
    return ";" if sys.platform == "win32" else ":"


def _get_full_classpath() -> str:
    """Get full classpath of all jars under lib directory"""
    sep = _classpath_sep()
    jars = list(LIB_DIR.glob("*.jar"))
    return sep.join(str(j) for j in jars)


def _collect_java_files(*dirs: Path) -> list[str]:
    files = []
    for d in dirs:
        files.extend(str(f) for f in d.rglob("*.java") if f.is_file())
    return files


def _run(cmd: list[str], timeout: int = 120) -> tuple[str, str, int]:
    result = subprocess.run(cmd, capture_output=True, text=True, timeout=timeout)
    return result.stdout, result.stderr, result.returncode


def _parse_junit_output(output: str) -> dict:
    passed = failures = errors = skipped = 0

    m = re.search(r"\[\s*(\d+)\s*tests.\s+successful\s*\]", output, re.IGNORECASE)
    if m:
        passed = int(m.group(1))

    m = re.search(r"\[\s*(\d+)\s*tests.\s+failed\s*\]", output, re.IGNORECASE)
    if m:
        failures = int(m.group(1))

    m = re.search(r"\[\s*(\d+)\s*tests.\s+aborted\s*\]", output, re.IGNORECASE)
    if m:
        errors = int(m.group(1))

    m = re.search(r"\[\s*(\d+)\s*tests.\s+skipped\s*\]", output, re.IGNORECASE)
    if m:
        skipped = int(m.group(1))

    total = passed + failures + errors + skipped
    return {"passed": passed, "failures": failures, "errors": errors, "skipped": skipped, "total": total}


def _ratio_str(passed: int, total: int) -> str:
    if total == 0:
        return "0.0000"
    return f"{passed / total:.4f}"


# ============================================================
# Compile + test for a single sample
# ============================================================

CSV_FIELDS = ["system", "model", "sample", "compile_code", "compile_test",
              "total", "passed", "failed", "skipped", "pass_rate"]


def run_sample(base_dir: Path, model: str, sample: str, lib_jar: Path) -> dict:
    sample_dir   = base_dir / model / sample
    test_src_dir = Path(__file__).parent.parent / "projects" / base_dir.name / "src"
    classes_dir  = sample_dir / "classes"
    project      = base_dir.name

    row = {
        "system": project, "model": model, "sample": sample,
        "compile_code": "FAIL", "compile_test": "FAIL",
        "total": 0, "passed": 0, "failed": 0, "skipped": 0,
        "pass_rate": "0.0000",
    }

    if not sample_dir.exists():
        sample_dir.mkdir(parents=True, exist_ok=True)
        msg = "sample dir not found"
        (sample_dir / "compile_result.txt").write_text(msg + "\n", encoding="utf-8")
        return row

    if classes_dir.exists():
        shutil.rmtree(classes_dir)
    classes_dir.mkdir(parents=True, exist_ok=True)

    # -- Step 1: Compile functional code only --
    code_files = _collect_java_files(sample_dir)
    if not code_files:
        msg = "no .java files found in sample dir"
        (sample_dir / "compile_result.txt").write_text(msg + "\n", encoding="utf-8")
        return row

    compile_cp = _get_full_classpath()
    code_cmd = [
        "javac", "-encoding", "UTF-8",
        "-cp", compile_cp,
        "-d", str(classes_dir),
    ] + code_files

    stdout, stderr, rc = _run(code_cmd)
    compile_log = f"=== compile_code ===\nEXIT CODE: {rc}\n\n--- STDOUT ---\n{stdout}\n--- STDERR ---\n{stderr}\n"

    if rc != 0:
        # Functional code compilation failed
        (sample_dir / "compile_result.txt").write_text(compile_log, encoding="utf-8")
        return row

    row["compile_code"] = "OK"

    # -- Step 2: Compile functional code + test code --
    test_files = _collect_java_files(test_src_dir)
    all_files = code_files + test_files

    if not test_files:
        msg = "no test .java files found"
        compile_log += f"\n=== compile_test ===\n{msg}\n"
        (sample_dir / "compile_result.txt").write_text(compile_log, encoding="utf-8")
        return row

    test_compile_cmd = [
        "javac", "-encoding", "UTF-8",
        "-cp", compile_cp,
        "-d", str(classes_dir),
    ] + all_files

    stdout2, stderr2, rc2 = _run(test_compile_cmd)
    compile_log += f"\n=== compile_test ===\nEXIT CODE: {rc2}\n\n--- STDOUT ---\n{stdout2}\n--- STDERR ---\n{stderr2}\n"
    (sample_dir / "compile_result.txt").write_text(compile_log, encoding="utf-8")

    if rc2 != 0:
        # Test code compilation failed, functional code OK
        return row

    row["compile_test"] = "OK"

    sep = _classpath_sep()
    test_cp = f"{_get_full_classpath()}{sep}{classes_dir}"

    reports_dir = sample_dir / "test"
    reports_dir.mkdir(exist_ok=True)

    junit_cmd = [
        "java", "-cp", test_cp,
        "org.junit.platform.console.ConsoleLauncher",
        "--scan-classpath",
        f"--classpath={classes_dir}",
        "--reports-dir", str(reports_dir),
    ]

    stdout, stderr, rc = _run(junit_cmd, timeout=180)
    test_log = f"EXIT CODE: {rc}\n\n--- STDOUT ---\n{stdout}\n--- STDERR ---\n{stderr}\n"
    (sample_dir / "test_output.txt").write_text(test_log, encoding="utf-8")

    junit_out = (stdout or "") + ("\n" + stderr if stderr else "")
    (reports_dir / "junit-console.out.txt").write_text(junit_out, encoding="utf-8", errors="replace")

    stats    = _parse_junit_output(junit_out)
    passed   = stats["passed"]
    total    = stats["total"]
    skipped  = stats["skipped"]
    failures = stats["failures"]
    errors   = stats["errors"]

    row.update({
        "total": total,
        "passed": passed,
        "failed": failures,
        "skipped": skipped,
        "pass_rate": _ratio_str(passed, total),
    })
    return row


# ============================================================
# Main flow
# ============================================================

def _write_csv(path: Path, rows: list[dict], fields: list[str]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with open(path, "w", newline="", encoding="utf-8") as f:
        w = csv.DictWriter(f, fieldnames=fields, extrasaction="ignore")
        w.writeheader()
        w.writerows(rows)


def main() -> None:
    if not LIB_JAR.exists():
        raise FileNotFoundError(f"JUnit jar not found: {LIB_JAR.resolve()}")

    REPORT_DIR.mkdir(parents=True, exist_ok=True)
    global_rows: list[dict] = []
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")

    for base_str, model in product(BASE_DIRS, MODEL_NAMES):
        base_dir = Path(base_str)
        if not (base_dir / model).exists():
            print(f"[SKIP] {base_dir}/{model} not found")
            continue

        print(f"\n{'='*60}\n  Project: {base_str}  Model: {model}\n{'='*60}")
        sample_rows: list[dict] = []

        for sample in SAMPLES:
            print(f"  [{sample}] compiling & testing ...", end=" ", flush=True)
            row = run_sample(base_dir, model, sample, LIB_JAR)
            print(f"compile={row['compile_code']}  test={row['compile_test']}  passed={row['passed']}/{row['total']}")
            sample_rows.append(row)
            global_rows.append(row)  # Append per-sample detail rows to global report

            sample_csv = base_dir / model / sample / "test" / "sample_report.csv"
            _write_csv(sample_csv, [row], CSV_FIELDS)

        model_csv = base_dir / model / "model_report.csv"
        _write_csv(model_csv, sample_rows, CSV_FIELDS)
        print(f"  >> Model report: {model_csv}")


    global_csv = REPORT_DIR / f"test_report_{timestamp}.csv"
    _write_csv(global_csv, global_rows, CSV_FIELDS)
    print(f"\n>> Global report: {global_csv}")


if __name__ == "__main__":
    main()