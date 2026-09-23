from __future__ import annotations

import argparse
import os
import sys
import subprocess
import textwrap
import math
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable
from xml.etree import ElementTree as ET
import csv
from collections import defaultdict


def safe_print(s: str) -> None:
    # Avoid UnicodeEncodeError on Windows consoles using GBK.
    try:
        print(s)
    except UnicodeEncodeError:
        sys.stdout.buffer.write(s.encode("utf-8", errors="replace") + b"\n")

# Select which PA projects to run by editing this list.
# Example: [19, 20, 22] 

PA_NUMBERS = [19,20,21,22]
MODELS = [
    # "gemini-3.1-flash-lite",
    # "minimax-m3",
    # "qwen3.6-flash",
    # "gpt-5.4-mini",
    "deepseek-v4-flash"
    "deepseek-v4-flash-norequire",
    "deepseek-v4-flash-nocompression",
    "deepseek-v4-flash-noreview",
    "deepseek-v4-flash-nofix",
    "deepseek-v4-flash-nocontext",

    "gpt-5.4-mini",
    "gpt-5.4-mini-norequire",
    "gpt-5.4-mini-nocontext",
    "gpt-5.4-mini-noreview",
    "gpt-5.4-mini-nocompression",
     "gpt-5.4-mini-nofix",

]


SAMPLES = [
    "sample1",
    "sample2",
    "sample3",
    "sample4",
    "sample5",
]

SCRIPT_DIR = Path(__file__).resolve().parent
REPO_ROOT = SCRIPT_DIR.parent
WORKSPACE_DIR = Path(os.environ.get("IECOREGEN_E_WORKSPACE", str(REPO_ROOT / "result-RQ2" / "E-Workspace")))
LIB_DIR = Path(os.environ.get("IECOREGEN_LIB_DIR", str(REPO_ROOT / "lib")))
ECLIPSE_DSL_PLUGINS_DIR = Path(os.environ.get("ECLIPSE_DSL_PLUGINS_DIR", r"E:\eclipse-dsl\plugins"))



JUNIT_CONSOLE_JAR = str(LIB_DIR / "junit-platform-console-standalone-1.13.3.jar")

# Extra runtime deps needed by tests / EMF etc. You can add more jars here.
EXTRA_CLASSPATH_JARS = [
    str(ECLIPSE_DSL_PLUGINS_DIR / "org.eclipse.emf.ecore_2.41.0.v20251025-0946.jar"),
    str(ECLIPSE_DSL_PLUGINS_DIR / "org.eclipse.emf.common_2.44.0.v20251025-0946.jar"),
    str(ECLIPSE_DSL_PLUGINS_DIR / "org.eclipse.emf.ecore.xmi-2.39.0.jar"),
    str(LIB_DIR / "mockito-core-4.11.0.jar"),
    str(LIB_DIR / "byte-buddy-1.12.13.jar"),
    str(LIB_DIR / "byte-buddy-agent-1.12.13.jar"),
    str(LIB_DIR / "objenesis-2.6.jar"),
]

# Lookup PAxx
def project_config(pa_number: int) -> dict[str, object]:
    pa = f"PA{pa_number}"
    return {
        "name": f"{pa}-emf",
        "test_project_dir": str(WORKSPACE_DIR / pa / "testcode"),
        "workspace_root": str(WORKSPACE_DIR / pa),
        "module_rel": f"pa{pa_number}",
        "junit_console_jar": JUNIT_CONSOLE_JAR,
        "extra_classpath_jars": list(EXTRA_CLASSPATH_JARS),
    }


@dataclass(frozen=True)
class SuiteStats:
    suite: str
    total: int
    passed: int
    skipped: int
    failures: int
    errors: int


@dataclass(frozen=True)
class CompileErrorStat:
    kind: str  # "compile_error"
    total: int
    passed: int
    skipped: int
    failures: int
    errors: int
    message: str


@dataclass(frozen=True)
class RunRow:
    pa: int
    project: str
    model: str
    sample: str
    passed: int
    total: int
    ratio: float
    skipped: int
    failures: int
    errors: int
    status: str  # ok / compile_error
    message: str


def iter_java_files(root: Path) -> Iterable[Path]:
    for p in root.rglob("*.java"):
        if p.is_file():
            yield p


def find_functional_code_dir(workspace_root: Path, model: str, sample: str, module_rel: str) -> Path | None:
    cand = workspace_root / model / sample / module_rel
    if not cand.is_dir():
        return None

    # Treat as valid only if it contains at least one .java file.
    try:
        next(iter_java_files(cand))
    except StopIteration:
        return None

    return cand
# (Intentionally no "functional sources under test project" mode)
# Functional sources are compiled from workspace:
#   <repo>\result-RQ2\E-Workspace\PAxx\<MODEL>\<SAMPLE>\paXX\**\*.java
# and emitted to:
#   <repo>\result-RQ2\E-Workspace\PAxx\<MODEL>\<SAMPLE>\classes


def scan_test_files(test_project_dir: Path) -> list[Path]:
    test_root = test_project_dir
    if not test_root.is_dir():
        return []

    out: list[Path] = []
    for p in test_root.rglob("*.java"):
        if p.is_file() and ("Test" in p.name) and p.name.endswith(".java"):
            out.append(p)
    return sorted(out)


def parse_junit_xml_results(results_dir: Path, *, clean: bool = False) -> list[SuiteStats]:
    if not results_dir.is_dir():
        return []

    if clean:
        for p in results_dir.glob("TEST-*.xml"):
            try:
                p.unlink()
            except OSError:
                pass

    suites: list[SuiteStats] = []
    for xml_path in sorted(results_dir.glob("TEST-*.xml")):
        try:
            root = ET.parse(xml_path).getroot()
        except ET.ParseError:
            continue

        if root.tag != "testsuite":
            continue

        suite = root.attrib.get("name") or xml_path.stem
        total = int(root.attrib.get("tests", "0"))
        skipped = int(root.attrib.get("skipped", "0"))
        failures = int(root.attrib.get("failures", "0"))
        errors = int(root.attrib.get("errors", "0"))
        passed = total - failures - errors - skipped
        suites.append(
            SuiteStats(
                suite=suite,
                total=total,
                passed=passed,
                skipped=skipped,
                failures=failures,
                errors=errors,
            )
        )

    return suites


def print_report(project_name: str, suites: list[SuiteStats], compile_error: CompileErrorStat | None = None) -> None:
    print(f"== {project_name} ==")

    overall_total = 0
    overall_passed = 0
    overall_skipped = 0
    overall_failures = 0
    overall_errors = 0

    for s in suites:
        overall_total += s.total
        overall_passed += s.passed
        overall_skipped += s.skipped
        overall_failures += s.failures
        overall_errors += s.errors

        ratio = (s.passed / s.total * 100.0) if s.total else 0.0
        print(
            f"{s.suite}: {s.passed}/{s.total} ({ratio:.2f}%) "
            f"[skipped={s.skipped} failures={s.failures} errors={s.errors}]"
        )

    if compile_error is not None:
        safe_print("-- COMPILE_ERROR --")
        safe_print(compile_error.message.rstrip())

    overall_ratio = (overall_passed / overall_total * 100.0) if overall_total else 0.0
    print(
        f"OVERALL: {overall_passed}/{overall_total} ({overall_ratio:.2f}%) "
        f"[skipped={overall_skipped} failures={overall_failures} errors={overall_errors}]"
    )


def run(
    cmd: list[str],
    cwd: Path | None = None,
    *,
    timeout_s: int | None = None,
    label: str | None = None,
) -> subprocess.CompletedProcess[str]:
    # Some tools (java/javac) may output UTF-8 while Windows default is GBK.
    # Be permissive to avoid crashing the runner on decode errors.
    if label:
        safe_print(f"[RUN] {label}")
    safe_print("[CMD] " + " ".join(cmd))
    if cwd:
        safe_print(f"[CWD] {cwd}")
    if timeout_s is not None:
        safe_print(f"[TIMEOUT] {timeout_s}s")

    return subprocess.run(
        cmd,
        cwd=str(cwd) if cwd else None,
        text=True,
        encoding="utf-8",
        errors="replace",
        capture_output=True,
        timeout=timeout_s,
    )


def pathsep_join(parts: Iterable[str]) -> str:
    out = []
    for p in parts:
        if p:
            out.append(p)
    return os.pathsep.join(out)


def compile_sources(src_dir: Path, out_dir: Path, classpath: str) -> None:
    sources = [str(p) for p in iter_java_files(src_dir)]
    if not sources:
        raise RuntimeError(f"No .java sources under {src_dir}")

    out_dir.mkdir(parents=True, exist_ok=True)

    # Use an argfile to avoid command line length limits on Windows.
    argfile = out_dir / "javac_sources.txt"
    argfile.write_text("\n".join(sources), encoding="utf-8")

    cmd = ["javac",
           "-J-Dstdout.encoding=UTF-8",  # Force javac to use UTF-8 output
           "-J-Dstderr.encoding=UTF-8",  # Force javac error messages to also use UTF-8
           "-encoding", "UTF-8",  # Declare source file encoding as UTF-8
           "-d", str(out_dir)]
    if classpath:
        cmd += ["-cp", classpath]
    cmd += ["@" + str(argfile)]

    cp = run(cmd, label=f"javac: {src_dir} -> {out_dir}")
    if cp.returncode != 0:
        raise RuntimeError(f"javac failed:\n{cp.stdout}\n{cp.stderr}")


def compile_tests_if_needed(test_project_dir: Path, out_dir: Path | None = None) -> Path:
    if out_dir is None:
        out_dir = test_project_dir / "build" / "junit-classes"

    # If already compiled, reuse.
    if out_dir.is_dir():
        return out_dir

    src_dir = test_project_dir / "src" / "test" / "java"
    if not src_dir.is_dir():
        raise RuntimeError(f"Missing test sources dir: {src_dir}")

    out_dir.mkdir(parents=True, exist_ok=True)
    return out_dir


def run_junit_console(
    junit_console_jar: Path,
    classpath: str,
    scan_classpath: str,
    reports_dir: Path,
    *,
    timeout_s: int = 600,
) -> str:
    reports_dir.mkdir(parents=True, exist_ok=True)

    # We request legacy XML report output to align with build/test-results style.
    # JUnit console supports --reports-dir and --reporting-dir depending on version; 1.13.x uses --reports-dir.
    cmd = [
        "java",
        # Avoid console trying to use terminal features.
        "-Djunit.platform.output.capture.stderr=true",
        "-Djunit.platform.output.capture.stdout=true",
        "-jar",
        str(junit_console_jar),
        "execute",
        "--scan-classpath",
        "--class-path",
        scan_classpath,
        "--reports-dir",
        str(reports_dir),
        "--disable-ansi-colors",
        "--details=verbose"
    ]

    # The console's own classpath for launching (java -jar ignores -cp);
    # we rely on --class-path for test discovery/execution.
    try:
        cp = run(cmd, timeout_s=timeout_s, label="junit-console: execute")
    except subprocess.TimeoutExpired as e:
        out = (e.stdout or "") + ("\n" + (e.stderr or "") if (e.stderr or "") else "")
        return "-- JUNIT_TIMEOUT --\n" + out

    # JUnit returns non-zero when tests fail; that's not a compile error.
    # We still want XML reports for stats.
    return (cp.stdout or "") + ("\n" + cp.stderr if cp.stderr else "")


def run_one_project_one_sample(
    proj: dict[str, object],
    model: str,
    sample: str,
    report_only: bool,
) -> tuple[list[SuiteStats], CompileErrorStat | None]:
    test_project_dir = Path(str(proj["test_project_dir"]))
    workspace_root = Path(str(proj["workspace_root"]))
    module_rel = str(proj["module_rel"])  # paXX

    compile_error: CompileErrorStat | None = None

    functional_src_dir = find_functional_code_dir(workspace_root, model, sample, module_rel)
    if functional_src_dir is None:
        compile_error = CompileErrorStat(
            kind="compile_error",
            total=0,
            passed=0,
            skipped=0,
            failures=0,
            errors=0,
            message=(
                "Functional source dir not found:\n"
                f"  src_dir={workspace_root / model / sample / module_rel}"
            ),
        )
        sample_dir = workspace_root / model / sample
        append_compile_error(sample_dir, f"[{proj['name']} | {model} | {sample}]\n", compile_error.message)
        return ([], compile_error)

    functional_classes_dir = workspace_root / model / sample / "classes"

    junit_console_jar = Path(str(proj["junit_console_jar"]))
    extra_jars = [Path(p) for p in proj.get("extra_classpath_jars", [])]

    if not junit_console_jar.is_file():
        compile_error = CompileErrorStat(
            kind="compile_error",
            total=0,
            passed=0,
            skipped=0,
            failures=0,
            errors=0,
            message=f"Missing junit console jar: {junit_console_jar}",
        )
        sample_dir = workspace_root / model / sample
        append_compile_error(sample_dir, f"[{proj['name']} | {model} | {sample}]\n", compile_error.message)
        return ([], compile_error)

    sample_dir = workspace_root / model / sample
    reports_dir = sample_dir / "test"

    # Ensure we don't reuse stale XML from a previous run.
    parse_junit_xml_results(reports_dir, clean=True)

    if not report_only:
        try:
            # 1) compile functional code (workspace paXX -> workspace classes)
            safe_print(f"[STEP] compile functional")
            safe_print(f"[INFO] functional_src_dir={functional_src_dir}")
            safe_print(f"[INFO] functional_classes_dir={functional_classes_dir}")
            compile_cp = pathsep_join([str(j) for j in extra_jars if j.is_file()])
            safe_print(f"[INFO] functional_compile_cp={compile_cp}")
            compile_sources(functional_src_dir, functional_classes_dir, compile_cp)

            # 2) compile tests (PAxx-emf/src/test/java)
            safe_print(f"[STEP] compile tests")
            test_src_dir = test_project_dir
            if not test_src_dir.is_dir():
                raise RuntimeError(f"Missing test src dir: {test_src_dir}")

            test_classes_dir = test_project_dir / "build" / "junit-test-classes"

            test_compile_cp = pathsep_join(
                [
                    str(functional_classes_dir),
                    str(junit_console_jar),
                    *[str(j) for j in extra_jars if j.is_file()],
                ]
            )
            safe_print(f"[INFO] test_src_dir={test_src_dir}")
            safe_print(f"[INFO] test_classes_dir={test_classes_dir}")
            safe_print(f"[INFO] test_compile_cp={test_compile_cp}")
            compile_sources(test_src_dir, test_classes_dir, test_compile_cp)

            # 3) run junit console using test-classes + functional-classes + deps
            safe_print(f"[STEP] run junit")
            scan_cp = pathsep_join(
                [
                    str(test_classes_dir),
                    str(functional_classes_dir),
                    *[str(j) for j in extra_jars if j.is_file()],
                ]
            )

            safe_print(f"[INFO] scan_cp={scan_cp}")
            safe_print(f"[INFO] reports_dir={reports_dir}")
            junit_out = run_junit_console(
                junit_console_jar,
                classpath=scan_cp,
                scan_classpath=scan_cp,
                reports_dir=reports_dir,
                timeout_s=600,
            )
            out_path = reports_dir / "junit-console.out.txt"
            out_path.write_text(junit_out, encoding="utf-8", errors="replace")
            safe_print(f"[INFO] junit console output saved: {out_path}")
            if "-- JUNIT_TIMEOUT --" in junit_out:
                raise RuntimeError("JUnit console timed out (600s). Likely a test is blocked (e.g., waiting for System.in via ConsolePlayer).\n" + junit_out)
        except Exception as e:
            compile_error = CompileErrorStat(
                kind="compile_error",
                total=0,
                passed=0,
                skipped=0,
                failures=0,
                errors=0,
                message=str(e),
            )
            sample_dir = workspace_root / model / sample
            append_compile_error(sample_dir, f"[{proj['name']} | {model} | {sample}]\n", compile_error.message)

    suites = parse_junit_xml_results(reports_dir)
    return (suites, compile_error)


def reset_compile_error_logs(workspace_root: Path, models: list[str], samples: list[str]) -> None:
    # At the start of each run: clear (or create empty) compile_error.txt under each sample
    for model in models:
        for sample in samples:
            sample_dir = workspace_root / model / sample
            try:
                sample_dir.mkdir(parents=True, exist_ok=True)
                (sample_dir / "compile_error.txt").write_text("", encoding="utf-8")
            except OSError:
                # Log write failure should not affect test flow
                pass


def append_compile_error(sample_dir: Path, header: str, message: str) -> None:
    try:
        sample_dir.mkdir(parents=True, exist_ok=True)
        out_path = sample_dir / "compile_error.txt"
        with out_path.open("a", encoding="utf-8", newline="\n") as f:
            f.write(header)
            if not header.endswith("\n"):
                f.write("\n")
            f.write(message.rstrip("\n"))
            f.write("\n\n")
    except OSError:
        pass


def write_summary_table(output_path: Path, rows: list[RunRow], *, delimiter: str) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", newline="", encoding="utf-8") as f:
        w = csv.writer(f, delimiter=delimiter)
        w.writerow(
            [
                "pa",
                "project",
                "model",
                "sample",
                "passed",
                "total",
                "ratio",
                "skipped",
                "failures",
                "errors",
                "status",
            ]
        )
        for r in rows:
            w.writerow(
                [
                    r.pa,
                    r.project,
                    r.model,
                    r.sample,
                    r.passed,
                    r.total,
                    f"{r.ratio:.6f}",
                    r.skipped,
                    r.failures,
                    r.errors,
                    r.status,
                ]
            )


def write_summary_csv(csv_path: Path, rows: list[RunRow]) -> None:
    write_summary_table(csv_path, rows, delimiter=",")


def write_summary_tsv(tsv_path: Path, rows: list[RunRow]) -> None:
    write_summary_table(tsv_path, rows, delimiter="\t")


def write_model_average_report(output_path: Path, rows: list[RunRow]) -> None:
    """
    Write per-model average test pass rates.

    - AvgRatioAllSamples uses every sample row from this run as denominator.
    - AvgRatioCompiledSamples uses only rows whose compilation succeeded.
    """
    groups: dict[str, list[RunRow]] = defaultdict(list)
    ordered_models: list[str] = []

    for row in rows:
        if row.model not in groups:
            ordered_models.append(row.model)
        groups[row.model].append(row)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow([
            "Model",
            "SampleRows",
            "CompilePassedSampleRows",
            "AvgRatioAllSamples",
            "AvgRatioCompiledSamples",
        ])

        for model in ordered_models:
            group = groups[model]
            compiled_group = [r for r in group if r.status != "compile_error"]

            avg_all = (
                sum(r.ratio for r in group) / len(group)
                if group else 0.0
            )
            avg_compiled = (
                sum(r.ratio for r in compiled_group) / len(compiled_group)
                if compiled_group else None
            )

            writer.writerow([
                model,
                len(group),
                len(compiled_group),
                f"{avg_all:.6f}",
                f"{avg_compiled:.6f}" if avg_compiled is not None else "N/A",
            ])


def pass_at_k(n: int, c: int, k: int) -> float | None:
    """
    Compute pass@k = 1 - C(n - c, k) / C(n, k).
    Returns None when n < k.
    """
    if n < k:
        return None
    total = math.comb(n, k)
    if total == 0:
        return None
    return 1.0 - math.comb(n - c, k) / total


def format_percent(value: float | None) -> str:
    if value is None:
        return "N/A"
    return f"{value * 100:.2f}%"


def write_pass_at_k_report(
    output_path: Path,
    rows: list[RunRow],
    ks: tuple[int, ...] = (1, 3),
) -> None:
    """Write compile@k and pass@k metrics grouped by PA project and model."""
    groups: dict[tuple[str, str], list[RunRow]] = defaultdict(list)
    ordered_keys: list[tuple[str, str]] = []

    for row in rows:
        key = (row.project, row.model)
        if key not in groups:
            ordered_keys.append(key)
        groups[key].append(row)

    results: list[dict] = []
    for project, model in ordered_keys:
        group = groups[(project, model)]
        n = len(group)
        compile_count = sum(1 for r in group if r.status != "compile_error")
        pass_count = sum(1 for r in group if r.ratio == 1.0)
        results.append({
            "System": project,
            "Model": model,
            "N": n,
            "compile_at": {k: pass_at_k(n, compile_count, k) for k in ks},
            "pass_at": {k: pass_at_k(n, pass_count, k) for k in ks},
        })

    model_metrics: dict[str, list[dict]] = defaultdict(list)
    for r in results:
        model_metrics[r["Model"]].append(r)

    avg_rows: list[dict] = []
    for model in dict.fromkeys(r["Model"] for r in results):
        metrics_list = model_metrics[model]
        avg_compile = {}
        avg_pass = {}
        for k in ks:
            compile_vals = [
                m["compile_at"][k] for m in metrics_list
                if m["compile_at"][k] is not None
            ]
            pass_vals = [
                m["pass_at"][k] for m in metrics_list
                if m["pass_at"][k] is not None
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
            "N": sum(m["N"] for m in metrics_list),
            "compile_at": avg_compile,
            "pass_at": avg_pass,
        })

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

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow([
            "System",
            "Model",
            *(f"compile@{k}" for k in ks),
            *(f"pass@{k}" for k in ks),
            "N",
        ])

        for r in results:
            row = [r["System"], r["Model"]]
            row.extend(format_percent(r["compile_at"][k]) for k in ks)
            row.extend(format_percent(r["pass_at"][k]) for k in ks)
            row.append(r["N"])
            writer.writerow(row)

        for avg in avg_rows:
            row = [avg["System"], avg["Model"]]
            row.extend(format_percent(avg["compile_at"][k]) for k in ks)
            row.extend(format_percent(avg["pass_at"][k]) for k in ks)
            row.append(avg["N"])
            writer.writerow(row)

        if overall_avg:
            row = [overall_avg["System"], overall_avg["Model"]]
            row.extend(format_percent(overall_avg["compile_at"][k]) for k in ks)
            row.extend(format_percent(overall_avg["pass_at"][k]) for k in ks)
            row.append(overall_avg["N"])
            writer.writerow(row)


def main() -> int:
    # No CLI args by design (you run it from PyCharm). Edit arrays/constants on top instead.
    report_only = False

    junit_console_jar = Path(JUNIT_CONSOLE_JAR)
    if not junit_console_jar.is_file():
        safe_print(f"Missing JUNIT_CONSOLE_JAR: {junit_console_jar}")
        return 2

    overall_total = 0
    overall_passed = 0
    overall_skipped = 0
    overall_failures = 0
    overall_errors = 0

    rows: list[RunRow] = []

    for pa_number in PA_NUMBERS:
        proj = project_config(pa_number)
        project_name = str(proj["name"])

        # At the start of each run: clear compile_error.txt for all (model, sample) under this PA
        reset_compile_error_logs(Path(str(proj["workspace_root"])), list(MODELS), list(SAMPLES))

        project_total = 0
        project_passed = 0
        project_skipped = 0
        project_failures = 0
        project_errors = 0

        for model in MODELS:
            for sample in SAMPLES:
                safe_print(f"\n## Running {project_name} | {model} | {sample}")

                suites, compile_error = run_one_project_one_sample(
                    proj=proj, model=model, sample=sample, report_only=report_only
                )

                # Report for this (project, model, sample)
                print_report(f"{project_name} | {model} | {sample}", suites, compile_error=compile_error)

                # Compute aggregated stats for this run (across all suites)
                run_total = sum(s.total for s in suites)
                run_passed = sum(s.passed for s in suites)
                run_skipped = sum(s.skipped for s in suites)
                run_failures = sum(s.failures for s in suites)
                run_errors = sum(s.errors for s in suites)
                run_ratio = (run_passed / run_total) if run_total else 0.0

                status = "ok" if compile_error is None else "compile_error"
                message = "" if compile_error is None else compile_error.message.replace("\r\n", "\n").replace("\r", "\n")

                rows.append(
                    RunRow(
                        pa=int(pa_number),
                        project=project_name,
                        model=model,
                        sample=sample,
                        passed=run_passed,
                        total=run_total,
                        ratio=run_ratio,
                        skipped=run_skipped,
                        failures=run_failures,
                        errors=run_errors,
                        status=status,
                        message=message,
                    )
                )

                # Accumulate overall across everything we ran.
                overall_total += run_total
                overall_passed += run_passed
                overall_skipped += run_skipped
                overall_failures += run_failures
                overall_errors += run_errors

                project_total += run_total
                project_passed += run_passed
                project_skipped += run_skipped
                project_failures += run_failures
                project_errors += run_errors

        # Per-project summary (aggregated across all models/samples for this PA)
        project_ratio = (project_passed / project_total * 100.0) if project_total else 0.0
        safe_print(f"\n== PROJECT SUMMARY: {project_name} ==")
        safe_print(
            f"OVERALL: {project_passed}/{project_total} ({project_ratio:.2f}%) "
            f"[skipped={project_skipped} failures={project_failures} errors={project_errors}]"
        )

    overall_ratio = (overall_passed / overall_total * 100.0) if overall_total else 0.0
    safe_print("\n== ALL PROJECTS OVERALL ==")
    safe_print(
        f"OVERALL: {overall_passed}/{overall_total} ({overall_ratio:.2f}%) "
        f"[skipped={overall_skipped} failures={overall_failures} errors={overall_errors}]"
    )

    # Write summary reports next to this script: ./reports/summary.csv and ./reports/summary.tsv
    script_dir = Path(__file__).resolve().parent
    reports_dir = script_dir / "reports"
    csv_path = reports_dir / "summary.csv"
    tsv_path = reports_dir / "summary.tsv"
    model_avg_path = reports_dir / "summary_model_avg.csv"
    pass_at_k_path = reports_dir / "summary@passk.csv"
    write_summary_csv(csv_path, rows)
    write_summary_tsv(tsv_path, rows)
    write_model_average_report(model_avg_path, rows)
    write_pass_at_k_report(pass_at_k_path, rows)
    safe_print(f"\nCSV written: {csv_path}")
    safe_print(f"TSV written: {tsv_path}")
    safe_print(f"Model average CSV written: {model_avg_path}")
    safe_print(f"Pass@k CSV written: {pass_at_k_path}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
