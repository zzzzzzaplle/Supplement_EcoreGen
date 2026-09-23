#!/usr/bin/env python3
"""
Automated test runner for 10 EMF-based systems across multiple LLM models.

Configuration:
  - SYSTEMS: list of system names to test
  - MODELS:  list of LLM model names to test
  - Edit these arrays freely to include/exclude items.

Output per project folder (e.g., OPMS/deepseek-v4-flash/project1/):
  - compile_true.txt   : compilation succeeded
  - compile_failed.txt : compilation failed (contains error info)
  - test_true.txt      : all tests passed
  - test_failed.txt    : tests failed (contains failure info)
"""
from __future__ import annotations

import os
import re
import math
import subprocess
import sys
import csv
import shutil
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path

# ============================================================
#  CONFIGURATION - freely edit these arrays
# ============================================================

# Which systems to test (comment out any you want to skip)
SYSTEMS = [
    "OLRS",
    "OPMS",
    "OPRS",

    "ORS",
    "R123_School",
    "R12_RentedCarGalleryManagementSystem",
    "R132_MunicipalLibrary",
    "R144_AirlineFlights",

    "R2_EmployeeManagementSystem",
    "R22_IPOApplication",
]

# Which LLM models to test (comment out any you want to skip)
MODELS = [
    "deepseek-v4-flash-norequire",
    "deepseek-v4-flash-nocompression",
    "deepseek-v4-flash-noreview",
    "deepseek-v4-flash-nofix",
    "deepseek-v4-flash",
    "deepseek-v4-flash-nocontext",
    # "gpt-5.4-mini",
    # "gemini-3.1-flash-lite",
    # "minimax-m3",
    # "qwen3.6-flash",
    "gpt-5.4-mini",
    "gpt-5.4-mini-nocontext",
    "gpt-5.4-mini-noreview",
    "gpt-5.4-mini-nocompression",
    "gpt-5.4-mini-nofix",
    "gpt-5.4-nocontext",
    # "gemini-3.1-flash-lite",
    # "minimax-m3",
    # "qwen3.6-flash",
    # "deepseek-v4-flash",
    # "gpt-5.4-mini"
]

# Max number of samples to test per model (set to None or a large number for all)
MAX_SAMPLES_PER_MODEL = 5

# Enable pass@k / compile@k calculation after test run
ENABLE_PASS_AT_K = True

# ============================================================
#  PATHS
# ============================================================

WORKSPACE = Path(os.environ.get(
    "IECOREGEN_E_WORKSPACE",
    r"C:\Users\Zeqing\OneDrive\2025-USTB\iecoregen\result-RQ2\E-Workspace",
))

LIB_DIR = Path(r"C:\Users\Zeqing\OneDrive\2025-USTB\iecoregen\lib")

JUNIT_CONSOLE_JAR = LIB_DIR / "junit-platform-console-standalone-1.13.3.jar"

EXTRA_CLASSPATH_JARS = [
    Path(r"E:\eclipse-dsl\plugins\org.eclipse.emf.ecore_2.41.0.v20251025-0946.jar"),
    Path(r"E:\eclipse-dsl\plugins\org.eclipse.emf.common_2.44.0.v20251025-0946.jar"),
    Path(r"E:\eclipse-dsl\plugins\org.eclipse.emf.ecore.xmi-2.39.0.jar"),
    LIB_DIR / "junit-4.13.2.jar",
    LIB_DIR / "hamcrest-core-1.3.jar",
    LIB_DIR / "mockito-core-4.11.0.jar",
    LIB_DIR / "byte-buddy-1.12.13.jar",
    LIB_DIR / "byte-buddy-agent-1.12.13.jar",
    LIB_DIR / "objenesis-2.6.jar",
]

# Regex to discover project/sample folders (must end with digits)
PROJECT_DIR_RE = re.compile(r".*\d+$")

# ============================================================
#  UTILITY
# ============================================================


def safe_print(s: str) -> None:
    try:
        print(s)
    except UnicodeEncodeError:
        sys.stdout.buffer.write(s.encode("utf-8", errors="replace") + b"\n")


def pathsep_join(parts) -> str:
    return os.pathsep.join(str(p) for p in parts if p)


def iter_java_files(root: Path):
    for p in root.rglob("*.java"):
        if p.is_file():
            yield p


def run_cmd(cmd, cwd=None, timeout_s=600, show_cmd=False):
    if show_cmd:
        safe_print(f"[CMD] {' '.join(str(c) for c in cmd)}")
    return subprocess.run(
        cmd,
        cwd=str(cwd) if cwd else None,
        text=True,
        encoding="utf-8",
        errors="replace",
        capture_output=True,
        timeout=timeout_s,
    )


def write_result(filepath: Path, content: str = ""):
    filepath.write_text(content, encoding="utf-8", errors="replace")
    safe_print(f"  -> {filepath.name}")


def clean_result_files(project_dir: Path):
    """Remove old result files before a new run."""
    for name in ("compile_true.txt", "compile_failed.txt",
                 "test_true.txt", "test_failed.txt"):
        f = project_dir / name
        if f.exists():
            try:
                f.unlink()
            except OSError:
                pass

    reports_dir = project_dir / "test_reports"
    if reports_dir.exists():
        try:
            shutil.rmtree(reports_dir)
        except OSError:
            pass


# ============================================================
#  COMPILATION
# ============================================================


def compile_java(src_dir: Path, out_dir: Path, classpath: str) -> tuple[bool, str]:
    """Compile all .java files under src_dir into out_dir. Returns (success, detail)."""
    sources = [str(p) for p in iter_java_files(src_dir)]
    if not sources:
        return False, f"No .java sources found under {src_dir}"

    out_dir.mkdir(parents=True, exist_ok=True)

    # Use argfile to avoid command-line length limits on Windows
    argfile = out_dir / "_javac_sources.txt"
    argfile.write_text("\n".join(sources), encoding="utf-8")

    cmd = [
        "javac",
        "-J-Dstdout.encoding=UTF-8",
        "-J-Dstderr.encoding=UTF-8",
        "-encoding", "UTF-8",
        "-d", str(out_dir),
    ]
    if classpath:
        cmd += ["-cp", classpath]
    cmd.append("@" + str(argfile))

    cp = run_cmd(cmd, cwd=src_dir, show_cmd=False)
    output = (cp.stdout or "") + ("\n" + cp.stderr if cp.stderr else "")

    if cp.returncode != 0:
        return False, output

    return True, "Compilation successful"


# ============================================================
#  TEST DISCOVERY & EXECUTION
# ============================================================


def read_package(java_file: Path) -> str:
    """Read 'package xxx.yyy;' from a .java file."""
    try:
        text = java_file.read_text(encoding="utf-8", errors="replace")
        m = re.search(r"^\s*package\s+([\w.]+)\s*;", text, re.MULTILINE)
        if m:
            return m.group(1)
    except OSError:
        pass
    return ""


def discover_test_classes(testcode_dir: Path) -> list[str]:
    """Find all *Test.java files and return fully-qualified class names."""
    classes = []
    for jf in sorted(iter_java_files(testcode_dir)):
        if "Test" in jf.name and jf.name.endswith(".java"):
            pkg = read_package(jf)
            cls_name = jf.stem  # e.g., CR1Test
            fqn = f"{pkg}.{cls_name}" if pkg else cls_name
            classes.append(fqn)
    return classes


def run_junit_tests(
    classpath: str,
    test_classes: list[str],
    reports_dir: Path,
    timeout_s: int = 600,
) -> tuple[bool, str]:
    """Run JUnit tests via junit-platform-console-standalone. Returns (all_passed, detail)."""
    reports_dir.mkdir(parents=True, exist_ok=True)

    cmd = [
        "java",
        "-Djunit.platform.output.capture.stderr=true",
        "-Djunit.platform.output.capture.stdout=true",
        "-jar", str(JUNIT_CONSOLE_JAR),
        "execute",
        "--class-path", classpath,
        "--reports-dir", str(reports_dir),
        "--disable-ansi-colors",
        "--details=verbose",
    ]
    for tc in test_classes:
        cmd += ["--select-class", tc]

    try:
        cp = run_cmd(cmd, timeout_s=timeout_s, show_cmd=False)
    except subprocess.TimeoutExpired as e:
        out = (e.stdout or "") + ("\n" + (e.stderr or ""))
        return False, f"JUNIT TIMEOUT ({timeout_s}s)\n{out}"

    output = (cp.stdout or "") + ("\n" + cp.stderr if cp.stderr else "")

    # JUnit console returns non-zero on test failures
    if cp.returncode == 0:
        return True, output
    else:
        return False, output


def parse_junit_counts(reports_dir: Path) -> tuple[int, int]:
    """Return (passed, total) from the non-empty JUnit XML report with most tests."""
    best_total = 0
    best_passed = 0

    for report in reports_dir.glob("TEST-*.xml"):
        try:
            root = ET.parse(report).getroot()
        except (ET.ParseError, OSError):
            continue

        try:
            total = int(root.attrib.get("tests", "0"))
            failures = int(root.attrib.get("failures", "0"))
            errors = int(root.attrib.get("errors", "0"))
            skipped = int(root.attrib.get("skipped", "0"))
        except ValueError:
            continue

        passed = max(total - failures - errors - skipped, 0)
        if total > best_total:
            best_total = total
            best_passed = passed

    return best_passed, best_total


# ============================================================
#  PROJECT DISCOVERY
# ============================================================


def discover_project_dirs(model_dir: Path) -> list[Path]:
    """Discover project/sample sub-directories (names ending with digits)."""
    dirs = []
    for d in sorted(model_dir.iterdir()):
        if d.is_dir() and PROJECT_DIR_RE.match(d.name):
            # Must contain at least one .java file
            try:
                next(iter_java_files(d))
                dirs.append(d)
            except StopIteration:
                pass
    return dirs


# ============================================================
#  PASS@K / COMPILE@K CALCULATION
# ============================================================


def _pass_at_k(n: int, c: int, k: int) -> float | None:
    """
    Compute pass@k = 1 - C(n - c, k) / C(n, k).
    Returns None when n < k (insufficient samples).
    """
    if n < k:
        return None
    total = math.comb(n, k)
    if total == 0:
        return None
    return 1.0 - math.comb(n - c, k) / total


def _format_pass_at_k(value: float | None) -> str:
    """Format a metric value as percentage, or 'N/A' if unavailable."""
    if value is None:
        return "N/A"
    return f"{value * 100:.2f}%"


def _parse_ratio(row: dict[str, str]) -> float:
    """Parse a sample Ratio value from CSV-like row data."""
    try:
        return float(row.get("Ratio", "0") or 0)
    except ValueError:
        return 0.0


def calculate_and_write_model_average_report(
    rows: list[dict[str, str]],
    output_path: Path,
) -> None:
    """
    Write per-model average test pass rates.

    - AvgRatioAllSamples uses every sample row from this run as denominator.
    - AvgRatioCompiledSamples uses only rows whose compilation succeeded.
    """
    groups: dict[str, list[dict[str, str]]] = defaultdict(list)
    ordered_models: list[str] = []

    for row in rows:
        model = row["Model"]
        if model not in groups:
            ordered_models.append(model)
        groups[model].append(row)

    fieldnames = [
        "Model",
        "SampleRows",
        "CompilePassedSampleRows",
        "AvgRatioAllSamples",
        "AvgRatioCompiledSamples",
    ]

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()

        for model in ordered_models:
            group = groups[model]
            compiled_group = [
                r for r in group
                if r.get("Status", "").strip() != "compile_failed"
            ]

            avg_all = (
                sum(_parse_ratio(r) for r in group) / len(group)
                if group else 0.0
            )
            avg_compiled = (
                sum(_parse_ratio(r) for r in compiled_group) / len(compiled_group)
                if compiled_group else None
            )

            writer.writerow({
                "Model": model,
                "SampleRows": len(group),
                "CompilePassedSampleRows": len(compiled_group),
                "AvgRatioAllSamples": f"{avg_all:.4f}",
                "AvgRatioCompiledSamples": (
                    f"{avg_compiled:.4f}" if avg_compiled is not None else "N/A"
                ),
            })


def calculate_and_write_pass_at_k(
    csv_path: Path, output_path: Path, ks: list[int] = (1, 3)
) -> None:
    """
    Read OurBench.csv, compute pass@k and compile@k metrics,
    and write results to output_path as CSV.

    - pass@k  : a sample "passes" if Ratio == 1.0 (all tests passed)
    - compile@k: a sample "passes" if Status != "compile_failed"
    """
    with csv_path.open("r", encoding="utf-8-sig", newline="") as f:
        rows = list(csv.DictReader(f))

    # Group by (System, Model), preserving insertion order
    groups: dict[tuple[str, str], list[dict[str, str]]] = defaultdict(list)
    ordered_keys: list[tuple[str, str]] = []

    for row in rows:
        key = (row["System"], row["Model"])
        if key not in groups:
            ordered_keys.append(key)
        groups[key].append(row)

    # Compute metrics per (System, Model) group
    results: list[dict] = []
    for system, model in ordered_keys:
        group = groups[(system, model)]
        n = len(group)
        compile_count = sum(
            1 for r in group if r.get("Status", "").strip() != "compile_failed"
        )
        pass_count = sum(
            1 for r in group
            if float(r.get("Ratio", "0")) == 1.0
        )
        results.append({
            "System": system,
            "Model": model,
            "N": n,
            "compile_at": {k: _pass_at_k(n, compile_count, k) for k in ks},
            "pass_at": {k: _pass_at_k(n, pass_count, k) for k in ks},
        })

    # Compute per-model averages
    model_metrics: dict[str, list[dict]] = defaultdict(list)
    for r in results:
        model_metrics[r["Model"]].append(r)

    avg_rows: list[dict] = []
    for model in dict.fromkeys(r["Model"] for r in results):
        metrics_list = model_metrics[model]
        valid_n = sum(m["N"] for m in metrics_list if m["N"] > 0)
        avg_compile = {}
        avg_pass = {}
        for k in ks:
            compile_vals = [
                m["compile_at"][k] for m in metrics_list
                if m["N"] > 0 and m["compile_at"][k] is not None
            ]
            pass_vals = [
                m["pass_at"][k] for m in metrics_list
                if m["N"] > 0 and m["pass_at"][k] is not None
            ]
            avg_compile[k] = (
                sum(compile_vals) / len(compile_vals) if compile_vals else None
            )
            avg_pass[k] = (
                sum(pass_vals) / len(pass_vals) if pass_vals else None
            )
        avg_rows.append({
            "System": f"AVG({model})",
            "Model": model,
            "N": valid_n,
            "compile_at": avg_compile,
            "pass_at": avg_pass,
        })

    # Compute an overall average across model-average rows.
    overall_avg: dict | None = None
    if avg_rows:
        overall_compile = {}
        overall_pass = {}
        for k in ks:
            compile_vals = [
                r["compile_at"][k] for r in avg_rows
                if r["compile_at"][k] is not None
            ]
            pass_vals = [
                r["pass_at"][k] for r in avg_rows
                if r["pass_at"][k] is not None
            ]
            overall_compile[k] = (
                sum(compile_vals) / len(compile_vals) if compile_vals else None
            )
            overall_pass[k] = (
                sum(pass_vals) / len(pass_vals) if pass_vals else None
            )

        overall_avg = {
            "System": "AVG(ALL_MODELS)",
            "Model": "ALL_MODELS",
            "N": sum(r["N"] for r in avg_rows),
            "compile_at": overall_compile,
            "pass_at": overall_pass,
        }

    # Write output CSV
    fieldnames = [
        "System", "Model",
        *(f"compile@{k}" for k in ks),
        *(f"pass@{k}" for k in ks),
        "N",
    ]
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(fieldnames)
        for r in results:
            row = [r["System"], r["Model"]]
            row.extend(_format_pass_at_k(r["compile_at"][k]) for k in ks)
            row.extend(_format_pass_at_k(r["pass_at"][k]) for k in ks)
            row.append(r["N"])
            writer.writerow(row)
        for avg in avg_rows:
            row = [avg["System"], avg["Model"]]
            row.extend(_format_pass_at_k(avg["compile_at"][k]) for k in ks)
            row.extend(_format_pass_at_k(avg["pass_at"][k]) for k in ks)
            row.append(avg["N"])
            writer.writerow(row)
        if overall_avg:
            row = [overall_avg["System"], overall_avg["Model"]]
            row.extend(_format_pass_at_k(overall_avg["compile_at"][k]) for k in ks)
            row.extend(_format_pass_at_k(overall_avg["pass_at"][k]) for k in ks)
            row.append(overall_avg["N"])
            writer.writerow(row)


# ============================================================
#  MAIN ORCHESTRATION
# ============================================================


def process_one_project(
    system: str,
    model: str,
    project_dir: Path,
    testcode_dir: Path,
):
    """Compile + test one project, write result files."""
    safe_print(f"\n  === {system}/{model}/{project_dir.name} ===")

    clean_result_files(project_dir)

    # Build classpath from extra jars
    extra_jars_cp = pathsep_join(
        str(j) for j in EXTRA_CLASSPATH_JARS if j.is_file()
    )

    # --- Step 1: compile functional code ---
    safe_print("  [STEP] Compile functional code")
    func_classes_dir = project_dir / "classes"
    ok, detail = compile_java(project_dir, func_classes_dir, extra_jars_cp)

    if not ok:
        write_result(project_dir / "compile_failed.txt", detail)
        return  # Skip tests if compilation fails

    write_result(project_dir / "compile_true.txt", "Compilation successful\n")

    # --- Step 2: compile test code ---
    safe_print("  [STEP] Compile test code")
    test_classes_dir = project_dir / "test_classes"

    # Test compilation classpath includes functional classes + extra jars
    test_compile_cp = pathsep_join([
        str(func_classes_dir),
        extra_jars_cp,
    ])
    ok, detail = compile_java(testcode_dir, test_classes_dir, test_compile_cp)
    if not ok:
        write_result(project_dir / "test_failed.txt",
                     f"Test compilation failed:\n{detail}")
        return

    # --- Step 3: discover & run tests ---
    safe_print("  [STEP] Run JUnit tests")
    test_class_names = discover_test_classes(testcode_dir)
    if not test_class_names:
        write_result(project_dir / "test_failed.txt",
                     "No test classes found in testcode")
        return

    safe_print(f"  [INFO] Test classes: {test_class_names}")

    # Runtime classpath: test classes + functional classes + jars
    run_cp = pathsep_join([
        str(test_classes_dir),
        str(func_classes_dir),
        extra_jars_cp,
    ])
    reports_dir = project_dir / "test_reports"

    ok, detail = run_junit_tests(run_cp, test_class_names, reports_dir)

    if ok:
        write_result(project_dir / "test_true.txt",
                     f"All tests passed\n\n{detail}")
    else:
        write_result(project_dir / "test_failed.txt", detail)


def main():
    # Validate essential paths
    if not JUNIT_CONSOLE_JAR.is_file():
        safe_print(f"ERROR: Missing JUNIT_CONSOLE_JAR: {JUNIT_CONSOLE_JAR}")
        return 2

    safe_print("=" * 60)
    safe_print("  Automated System Test Runner")
    safe_print(f"  Systems: {SYSTEMS}")
    safe_print(f"  Models:  {MODELS}")
    safe_print("=" * 60)

    total_projects = 0
    compile_ok = 0
    compile_fail = 0
    test_ok = 0
    test_fail = 0
    skipped = 0

    # Store results for CSV
    results_data = []

    for system in SYSTEMS:
        testcode_dir = WORKSPACE / system / "testcode"
        if not testcode_dir.is_dir():
            safe_print(f"\n[SKIP] {system}: no testcode directory")
            skipped += 1
            continue

        for model in MODELS:
            model_dir = WORKSPACE / system / model
            if not model_dir.is_dir():
                safe_print(f"\n[SKIP] {system}/{model}: model directory not found")
                continue

            project_dirs = discover_project_dirs(model_dir)
            if not project_dirs:
                safe_print(f"\n[SKIP] {system}/{model}: no project directories found")
                continue

            if MAX_SAMPLES_PER_MODEL is not None:
                project_dirs = project_dirs[:MAX_SAMPLES_PER_MODEL]

            safe_print(f"\n>>> Discovered {len(project_dirs)} samples for {system}/{model}")

            for project_dir in project_dirs:
                total_projects += 1

                process_one_project(system, model, project_dir, testcode_dir)

                # Collect stats from result files and JUnit XML reports.
                status = "compile_failed"
                passed = 0
                total = 0

                if (project_dir / "compile_true.txt").exists():
                    compile_ok += 1
                    if (project_dir / "test_true.txt").exists():
                        test_ok += 1
                        status = "test_success"
                    else:
                        test_fail += 1
                        status = "test_failed"

                    passed, total = parse_junit_counts(project_dir / "test_reports")
                else:
                    compile_fail += 1

                ratio = passed / total if total else 0.0

                results_data.append({
                    "System": system,
                    "Model": model,
                    "Passed": passed,
                    "Total": total,
                    "Ratio": f"{ratio:.4f}",
                    "Status": status,
                    "sample": project_dir.name,
                })

    # Write CSV report
    report_dir = Path(r"C:\Users\Zeqing\OneDrive\2025-USTB\iecoregen\Codebase_Python\reports")
    report_dir.mkdir(parents=True, exist_ok=True)
    csv_path = report_dir / "OurBench.csv"
    try:
        with open(csv_path, 'w', newline='', encoding='utf-8') as f:
            writer = csv.DictWriter(
                f,
                fieldnames=[
                    "System",
                    "Model",
                    "Passed",
                    "Total",
                    "Ratio",
                    "Status",
                    "sample",
                ],
            )
            writer.writeheader()
            writer.writerows(results_data)
        safe_print(f"\n[INFO] Wrote CSV report to {csv_path}")
    except Exception as e:
        safe_print(f"\n[ERROR] Failed to write CSV report: {e}")

    model_avg_path = report_dir / "OurBench_model_avg.csv"
    try:
        calculate_and_write_model_average_report(results_data, model_avg_path)
        safe_print(f"[INFO] Wrote model average report to {model_avg_path}")
    except Exception as e:
        safe_print(f"[ERROR] Failed to write model average report: {e}")

    # --- Pass@k / Compile@k calculation ---
    if ENABLE_PASS_AT_K:
        pass_at_k_output = report_dir / "OurBench_pass@k.csv"
        try:
            calculate_and_write_pass_at_k(csv_path, pass_at_k_output)
            safe_print(f"[INFO] Wrote pass@k report to {pass_at_k_output}")
        except Exception as e:
            safe_print(f"[ERROR] Failed to calculate pass@k: {e}")
    else:
        safe_print("[INFO] Pass@k calculation is disabled (set ENABLE_PASS_AT_K = True to enable)")

    # Summary
    safe_print("\n" + "=" * 60)
    safe_print("  SUMMARY")
    safe_print("=" * 60)
    safe_print(f"  Total projects tested: {total_projects}")
    safe_print(f"  Compilation:  {compile_ok} passed / {compile_fail} failed")
    safe_print(f"  Tests:        {test_ok} passed / {test_fail} failed")
    safe_print(f"  Skipped:      {skipped} systems")
    safe_print("=" * 60)

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
