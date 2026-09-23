#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
OpenCode batch automated test script -- compile generated multi-file Java code and run JUnit tests

For each system, each model, each sample:
  1. Clear previous compilation artifacts (.class files)
  2. Compile all .java files in the sample directory (functional code)
  3. Compile test code (projects/{SYSTEM_DIR}/src/test/*.java)
  4. Run JUnit tests
  5. Save complete test output to {sample}/test/ directory
  6. Aggregate global test report

Usage:
    python3 opencode_test_REQ+CR+UML.py
    # Edit SYSTEM_DIRS, MODEL_NAMES, SAMPLES etc. below to control run scope
"""
from __future__ import annotations

import csv
import json
import locale
import re
import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Tuple

# ============================================================================
# Configuration
# ============================================================================
SCRIPT_DIR = Path(__file__).parent
PROJECTS_DIR = SCRIPT_DIR.parent / "projects" 
LIB_DIR = SCRIPT_DIR / "lib"                      # opencode-workspace/lib/ - jar files

# Systems to test
SYSTEM_DIRS: List[str] = [
    "OLRS",
    "OPMS",
    "OPRS",
    "ORS",
    "R2_EmployeeManagementSystem",
    "R123_School",
    "R12_RentedCarGalleryManagementSystem",
    "R132_MunicipalLibrary",
    "R144_AirlineFlights",
    "R22_IPOApplication",
]

# SYSTEM_DIRS: list[str] = ["OLRS", "OPMS", "OPRS", "ORS", "R123_School",
#                          "R12_RentedCarGalleryManagementSystem", "R132_MunicipalLibrary",
#                          "R144_AirlineFlights", "R22_IPOApplication", "R2_EmployeeManagementSystem"]
# Model names list
MODEL_NAMES: List[str] = [
    "minimax-m3",
    "deepseek-v4-flash",
    "gemini-3.1-flash-lite",
    "qwen3.6-flash",
    "gpt-5.4-mini"
]

# Max number of samples per system
SAMPLES: int = 5

# Output directory name (global test reports stored here)
OUTPUT_DIR_NAME: str = "test_reports"

# Per-sample test results subdirectory name
SAMPLE_TEST_DIR_NAME: str = "test"

# Compile and run timeout (seconds)
COMPILE_TIMEOUT: int = 60
TEST_TIMEOUT: int = 120

# ============================================================================
# Helper Functions
# ============================================================================

def _get_system_encoding() -> str:
    """Get system encoding; use GBK on Windows Chinese locale"""
    if sys.platform == 'win32':
        enc = locale.getpreferredencoding()
        if enc.lower() in ('cp936', 'gbk', 'gb2312'):
            return 'gbk'
    return 'utf-8'


def _collect_jars(lib_dir: Path) -> List[Path]:
    """Collect all jar files under lib directory"""
    if not lib_dir or not lib_dir.exists():
        return []
    return sorted(p for p in lib_dir.iterdir() if p.suffix == ".jar")


def _build_classpath(jars: List[Path], classes_dir: Path) -> str:
    """Build classpath string"""
    elements = [str(j) for j in jars] + [str(classes_dir)]
    return ";".join(elements) if sys.platform == 'win32' else ":".join(elements)


def _find_test_classes(test_dir: Path) -> List[str]:
    """Scan test class names from test directory (based on file names)"""
    if not test_dir.exists():
        return []
    classes = []
    for f in sorted(test_dir.glob("*.java")):
        name = f.stem
        if name.startswith("_"):
            continue
        classes.append(name)
    return classes


def _classify_failure(stacktrace: str) -> str:
    """Classify failure cause based on stacktrace"""
    if not stacktrace:
        return "unknown"
    if "AssertionError" in stacktrace or "AssertionFailedError" in stacktrace:
        return "assertion"
    if "NullPointerException" in stacktrace:
        return "NPE"
    if "ClassCastException" in stacktrace:
        return "ClassCast"
    if "IndexOutOfBounds" in stacktrace:
        return "IndexOutOfBounds"
    if "NoSuchMethod" in stacktrace:
        return "NoSuchMethod"
    if "StackOverflow" in stacktrace:
        return "StackOverflow"
    return "exception"


def _clean_class_files(sample_dir: Path):
    """Remove .class files under sample directory"""
    for cls_file in sample_dir.glob("**/*.class"):
        cls_file.unlink()


# ============================================================================
# Compile
# ============================================================================

def compile_java_files(
    java_files: List[Path],
    classes_dir: Path,
    jars: List[Path],
) -> Tuple[bool, str]:
    """Compile a set of Java files into classes_dir"""
    try:
        classes_dir.mkdir(parents=True, exist_ok=True)
        classpath = _build_classpath(jars, classes_dir)

        cmd = [
            "javac", "-encoding", "UTF-8",
            "-cp", classpath,
            "-d", str(classes_dir),
        ] + [str(f) for f in java_files]

        result = subprocess.run(
            cmd, capture_output=True, text=True,
            encoding=_get_system_encoding(), errors='replace',
            timeout=COMPILE_TIMEOUT,
        )

        output = (result.stdout or "") + (result.stderr or "")
        return result.returncode == 0, output

    except subprocess.TimeoutExpired:
        return False, f"Compilation timeout ({COMPILE_TIMEOUT}s)"
    except FileNotFoundError:
        return False, "javac not found. Ensure JDK is installed and on PATH."
    except Exception as e:
        return False, f"Compilation error: {e}"


# ============================================================================
# Run Tests
# ============================================================================

def run_junit_tests(
    test_classes: List[str],
    classes_dir: Path,
    jars: List[Path],
) -> Tuple[bool, str]:
    """Run tests using JUnit Platform Console Launcher"""
    try:
        classpath = _build_classpath(jars, classes_dir)

        cmd = [
            "java", "-cp", classpath,
            "org.junit.platform.console.ConsoleLauncher",
            "--disable-banner",
            "--disable-ansi-colors",
            "--details=tree",
        ]
        for tc in test_classes:
            cmd.extend(["-c", tc])

        result = subprocess.run(
            cmd, capture_output=True, text=True,
            encoding=_get_system_encoding(), errors='replace',
            timeout=TEST_TIMEOUT,
        )

        output = (result.stdout or "") + (result.stderr or "")
        # ConsoleLauncher: exit 0 = all passed, 1 = some failed, 2 = error
        return result.returncode == 0, output

    except subprocess.TimeoutExpired:
        return False, f"Test execution timeout ({TEST_TIMEOUT}s)"
    except Exception as e:
        return False, f"Test execution error: {e}"


def parse_test_results(output: str) -> Dict:
    """Parse test results from JUnit ConsoleLauncher tree output"""
    result = {
        "total": 0,
        "passed": 0,
        "failed": 0,
        "skipped": 0,
        "tests": [],
    }

    # Remove ANSI escape codes
    clean = re.sub(r'\x1b\[[0-9;]*m', '', output)

    # Unicode character sets for matching symbols
    PASS_SYMS = '\u2714\u2713'
    FAIL_SYMS = '\u2718\u2717\u2716\U0000274c'
    SKIP_SYMS = '\u25cc\u2298'
    ALL_SYMS = PASS_SYMS + FAIL_SYMS + SKIP_SYMS

    for line in clean.splitlines():
        stripped = line.strip()
        if not stripped:
            continue

        # tree format: +- testMethod [PASS]/[FAIL]  or  +- testMethod [PASS]/[FAIL]
        m = re.search(
            r'[++]-\s+(.+.)(.:\(\)).\s+([' + ALL_SYMS + r'])',
            stripped,
        )
        if m:
            test_name = m.group(1).strip()
            symbol = m.group(2)

            # Skip container nodes (JUnit prefix + test class name)
            if test_name.startswith('JUnit '):
                continue
            # Skip test class containers (name ends with Test/Tests, e.g. CR1_AddItemLineToOrderTest)
            if re.match(r'^[A-Z]\w*Test[s].$', test_name):
                continue

            entry = {"name": test_name}

            if symbol in PASS_SYMS:
                entry["status"] = "passed"
                entry["message"] = ""
                result["passed"] += 1
            elif symbol in FAIL_SYMS:
                entry["status"] = "failed"
                rest = stripped[m.end():].strip()
                entry["message"] = rest
                entry["category"] = _classify_failure(rest)
                result["failed"] += 1
            elif symbol in SKIP_SYMS:
                entry["status"] = "skipped"
                entry["message"] = ""
                result["skipped"] += 1
            else:
                continue

            result["tests"].append(entry)
            result["total"] += 1

    # Fallback: if tree parsing yields 0 results, try summary lines
    if result["total"] == 0:
        for pattern in [
            r'(\d+)\s+tests.\s+successful',
            r'tests found.*.(\d+)',
            r'(\d+)\s+tests.\s',
        ]:
            sm = re.search(pattern, clean, re.IGNORECASE)
            if sm:
                result["total"] = int(sm.group(1))
                break

    return result


# ============================================================================
# Per-Sample Test Output
# ============================================================================

def save_sample_test_output(
    sample_dir: Path,
    record: Dict,
):
    """Save single sample test results to {sample}/test/ directory"""
    test_out_dir = sample_dir / SAMPLE_TEST_DIR_NAME
    test_out_dir.mkdir(parents=True, exist_ok=True)

    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")

    # 1. Save complete raw JUnit output
    raw_path = test_out_dir / f"junit_output_{timestamp}.txt"
    raw_path.write_text(record.get("test_output", ""), encoding="utf-8")

    # 2. Save failed test list + stacktrace info
    failed_tests = [t for t in record.get("tests", []) if t["status"] == "failed"]
    fail_path = test_out_dir / f"failed_tests_{timestamp}.txt"
    with open(fail_path, 'w', encoding='utf-8') as f:
        f.write(f"Sample: {record['sample']}\n")
        f.write(f"Model:  {record['model']}\n")
        f.write(f"System: {record['system']}\n")
        f.write(f"Time:   {datetime.now():%Y-%m-%d %H:%M:%S}\n")
        f.write(f"Result: {record['passed']}/{record['total']} passed\n")
        f.write(f"Failed: {record['failed']}\n")
        f.write("=" * 60 + "\n\n")

        if not failed_tests:
            f.write("No failed tests.\n")
        else:
            for i, t in enumerate(failed_tests, 1):
                f.write(f"--- Failure #{i}: {t['name']} ---\n")
                f.write(f"Category: {t.get('category', 'unknown')}\n")
                f.write(f"Message:  {t.get('message', '')}\n\n")

    # 3. Save structured JSON results
    json_path = test_out_dir / f"result_{timestamp}.json"
    with open(json_path, 'w', encoding='utf-8') as f:
        json.dump(record, f, indent=2, ensure_ascii=False)

    return test_out_dir


# ============================================================================
# Single Sample Pipeline
# ============================================================================

def run_single_sample(
    system_dir: str,
    model_name: str,
    sample_name: str,
    jars: List[Path],
) -> Dict:
    """Execute for a single system/model/sample: clean -> compile -> test -> save results"""
    # Path definitions
    sample_dir = SCRIPT_DIR / system_dir / model_name / sample_name
    test_dir = PROJECTS_DIR / system_dir / "src" / "test"
    classes_dir = sample_dir / "classes"

    record = {
        "system": system_dir,
        "model": model_name,
        "sample": sample_name,
        "compile_code": "N/A",
        "compile_test": "N/A",
        "test_pass_rate": 0.0,
        "total": 0,
        "passed": 0,
        "failed": 0,
        "skipped": 0,
        "tests": [],
        "compile_errors": "",
        "test_output": "",
    }

    # -- Step 1: Clear compilation artifacts --
    if classes_dir.exists():
        shutil.rmtree(classes_dir)
    _clean_class_files(sample_dir)
    classes_dir.mkdir(parents=True)

    # -- Step 2: Collect functional code .java files --
    java_files = sorted(sample_dir.glob("*.java"))
    if not java_files:
        record["compile_code"] = "MISSING"
        record["compile_errors"] = f"No .java files found in {sample_dir}"
        print(f"      [FAIL] No .java files in sample dir")
        # Save failure record
        save_sample_test_output(sample_dir, record)
        return record

    # -- Step 3: Check test directory --
    if not test_dir.exists():
        record["compile_test"] = "MISSING"
        record["compile_errors"] = f"Test directory not found: {test_dir}"
        print(f"      [FAIL] Test dir missing")
        save_sample_test_output(sample_dir, record)
        return record

    test_classes = _find_test_classes(test_dir)
    if not test_classes:
        record["compile_test"] = "NO_TESTS"
        print(f"      [FAIL] No test files found")
        save_sample_test_output(sample_dir, record)
        return record

    # -- Step 4: Compile functional code --
    ok_code, out_code = compile_java_files(java_files, classes_dir, jars)
    record["compile_code"] = "OK" if ok_code else "FAIL"
    if not ok_code:
        record["compile_errors"] = out_code
        print(f"      [FAIL] Compile code FAIL ({len(java_files)} files)")
        save_sample_test_output(sample_dir, record)
        return record

    # -- Step 5: Compile test code --
    test_files = sorted(test_dir.glob("*.java"))
    ok_test, out_test = compile_java_files(test_files, classes_dir, jars)
    record["compile_test"] = "OK" if ok_test else "FAIL"
    if not ok_test:
        record["compile_errors"] = out_test
        print(f"      [FAIL] Compile tests FAIL")
        save_sample_test_output(sample_dir, record)
        return record

    # -- Step 6: Run JUnit tests --
    test_ok, test_output = run_junit_tests(test_classes, classes_dir, jars)
    record["test_output"] = test_output

    parsed = parse_test_results(test_output)
    record["total"] = parsed["total"]
    record["passed"] = parsed["passed"]
    record["failed"] = parsed["failed"]
    record["skipped"] = parsed["skipped"]
    record["tests"] = parsed["tests"]
    record["test_pass_rate"] = (
        parsed["passed"] / parsed["total"] if parsed["total"] > 0 else 0.0
    )

    # -- Step 7: Save per-sample test output --
    test_out_dir = save_sample_test_output(sample_dir, record)

    status_icon = "[PASS]" if test_ok else "[FAIL]"
    print(f"      {status_icon} {parsed['passed']}/{parsed['total']} passed"
          f"  -> {test_out_dir.relative_to(SCRIPT_DIR)}")
    return record


# ============================================================================
# Global Report
# ============================================================================

def print_report(results: List[Dict]):
    """Print test summary report to console"""
    print("\n" + "=" * 72)
    print("  OPENCODE BATCH TEST REPORT")
    print("=" * 72)

    current_system = None
    current_model = None
    sys_total = sys_passed = sys_failed = 0

    for r in results:
        # System separator
        if r["system"] != current_system:
            if current_system is not None:
                rate = sys_passed / sys_total * 100 if sys_total else 0
                print(f"  -- {current_system} subtotal: "
                      f"{sys_passed}/{sys_total} ({rate:.1f}%)")
                print()
            current_system = r["system"]
            current_model = None
            sys_total = sys_passed = sys_failed = 0

        # Model separator
        if r["model"] != current_model:
            current_model = r["model"]
            print(f"\n  [{r['system']}] model={r['model']}")

        # Sample results
        rate = r["test_pass_rate"] * 100
        status = "[PASS]" if r["compile_code"] == "OK" and rate == 100 else "[FAIL]"

        comp_info = f"compile_code={r['compile_code']}"
        if r["compile_test"] != "N/A":
            comp_info += f", compile_test={r['compile_test']}"

        if r["total"] > 0:
            print(f"    {status} {r['sample']}: "
                  f"{r['passed']}/{r['total']} ({rate:.0f}%) "
                  f"[{comp_info}]")
        else:
            err = r.get("compile_errors", "")[:60].replace("\n", " ")
            print(f"    {status} {r['sample']}: "
                  f"[{comp_info}] {err}")

        sys_total += r["total"]
        sys_passed += r["passed"]
        sys_failed += r["failed"]

    # Last system subtotal
    if current_system:
        rate = sys_passed / sys_total * 100 if sys_total else 0
        print(f"  -- {current_system} subtotal: "
              f"{sys_passed}/{sys_total} ({rate:.1f}%)")

    # Grand total
    total_all = sum(r["total"] for r in results)
    passed_all = sum(r["passed"] for r in results)
    failed_all = sum(r["failed"] for r in results)
    compile_fail = sum(
        1 for r in results
        if r["compile_code"] == "FAIL" or r["compile_test"] == "FAIL"
    )
    missing = sum(1 for r in results if r["compile_code"] == "MISSING")

    print("\n" + "-" * 72)
    if total_all:
        print(f"  TOTAL: {passed_all}/{total_all} passed "
              f"({passed_all / total_all * 100:.1f}%)")
    else:
        print("  TOTAL: 0 tests")
    if failed_all:
        print(f"  Failed tests: {failed_all}")
    if compile_fail:
        print(f"  Compile failures: {compile_fail}")
    if missing:
        print(f"  Missing code: {missing}")

    # Failure category breakdown
    categories: Dict[str, int] = {}
    for r in results:
        for t in r.get("tests", []):
            if t["status"] == "failed":
                cat = t.get("category", "unknown")
                categories[cat] = categories.get(cat, 0) + 1
    if categories:
        print("\n  Failure breakdown:")
        for cat, cnt in sorted(categories.items(), key=lambda x: -x[1]):
            print(f"    {cat}: {cnt}")

    print("=" * 72)


def save_json_report(results: List[Dict], output_path: Path):
    """Save detailed report in JSON format"""
    report: Dict = {}
    for r in results:
        sys_key = r["system"]
        mod_key = r["model"]
        if sys_key not in report:
            report[sys_key] = {}
        if mod_key not in report[sys_key]:
            report[sys_key][mod_key] = {
                "samples": [],
                "summary": {"total": 0, "passed": 0, "failed": 0},
            }
        summary = report[sys_key][mod_key]["summary"]
        summary["total"] += r["total"]
        summary["passed"] += r["passed"]
        summary["failed"] += r["failed"]
        if summary["total"] > 0:
            summary["pass_rate"] = round(
                summary["passed"] / summary["total"], 4
            )
        report[sys_key][mod_key]["samples"].append(r)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(report, f, indent=2, ensure_ascii=False)
    print(f"\nJSON report -> {output_path}")


def save_csv_report(results: List[Dict], output_path: Path):
    """Save CSV summary report (one row per sample)"""
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, 'w', encoding='utf-8-sig', newline='') as f:
        writer = csv.writer(f)
        writer.writerow([
            "system", "model", "sample",
            "compile_code", "compile_test",
            "total", "passed", "failed", "skipped",
            "pass_rate",
        ])
        for r in results:
            writer.writerow([
                r["system"], r["model"], r["sample"],
                r["compile_code"], r["compile_test"],
                r["total"], r["passed"], r["failed"], r["skipped"],
                f"{r['test_pass_rate']:.4f}",
            ])
    print(f"CSV report  -> {output_path}")


# ============================================================================
# Main
# ============================================================================

def main():
    print("=" * 60)
    print("  OpenCode Batch Test Runner")
    print(f"  Time: {datetime.now():%Y-%m-%d %H:%M:%S}")
    print(f"  Systems: {SYSTEM_DIRS}")
    print(f"  Models:  {MODEL_NAMES}")
    print(f"  Samples: {SAMPLES}")
    print("=" * 60)

    jars = _collect_jars(LIB_DIR)
    if not jars:
        print(f"ERROR: No jars found in {LIB_DIR}")
        sys.exit(1)
    print(f"Libs: {len(jars)} jars loaded from {LIB_DIR}")

    results: List[Dict] = []

    for system_dir in SYSTEM_DIRS:
        for model_name in MODEL_NAMES:
            model_base = SCRIPT_DIR / system_dir / model_name

            # Auto-scan sample directories
            sample_dirs = sorted(
                d for d in model_base.iterdir()
                if d.is_dir() and re.match(r"^sample\d+$", d.name)
            ) if model_base.exists() else []

            if not sample_dirs:
                print(f"\n[{system_dir}/{model_name}] No sample dirs, skipping")
                continue

            sample_dirs = sample_dirs[:SAMPLES]
            sample_names = [d.name for d in sample_dirs]

            print(f"\n[{system_dir}/{model_name}] "
                  f"samples: {', '.join(sample_names)}")

            for sample_name in sample_names:
                r = run_single_sample(
                    system_dir, model_name, sample_name, jars,
                )
                results.append(r)

    # -- Generate global report --
    if results:
        print_report(results)

        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        report_dir = SCRIPT_DIR / OUTPUT_DIR_NAME
        save_json_report(results, report_dir / f"test_report_{timestamp}.json")
        save_csv_report(results, report_dir / f"test_report_{timestamp}.csv")
    else:
        print("\nNo samples found to test. Check SYSTEM_DIRS and MODEL_NAMES.")


if __name__ == "__main__":
    main()
