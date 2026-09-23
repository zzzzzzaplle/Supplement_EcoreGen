"""
Mini-SWE-Agent Java code generator - REQ + CR + UML input format.

Combines:
  - opencode_gen_REQ+CR+UML.py  -> input format (REQ.md + CR.md + UML), system dir scanning
  - swejavabench_codegen.py      -> agent framework (mini-swe-agent), model config, heredoc output

Usage:
    python3 miniswe_gen_REQ+CR+UML.py
    # Adjust SYSTEM_DIRS, MODEL_NAME, SAMPLES below to control scope.
"""
import json
import os
import re
import shutil
from pathlib import Path
from jinja2 import Template

SCRIPT_DIR = Path(__file__).parent
INPUT_BASE_DIR = SCRIPT_DIR.parent / "projects"  # 

from minisweagent.agents.default import DefaultAgent
from minisweagent.models.litellm_model import LitellmModel
from minisweagent.environments.local import LocalEnvironment

# ============================================================================
# Model Configuration (from swejavabench)
# ============================================================================
MODEL_NAME = os.getenv("SWE_MODEL", "")
API_BASE = os.getenv("SWE_API_BASE", "")
API_KEY = os.getenv("SWE_API_KEY", "")
MODEL_DIR_NAME = "llama-3.3-70b-instruct"   # output  sub-directory name (independent of MODEL_NAME)

# ============================================================================
# Directory Configuration (from opencode_gen)
# ============================================================================
# Systems to process (empty -> auto-scan INPUT_BASE_DIR)
SYSTEM_DIRS: list[str] = ["OLRS", "OPMS", "OPRS", "ORS", "R123_School",
                         "R12_RentedCarGalleryManagementSystem", "R132_MunicipalLibrary",
                         "R144_AirlineFlights", "R22_IPOApplication", "R2_EmployeeManagementSystem"]

SAMPLES = 5

# Input file names (from opencode_gen)
REQ_FILE_NAME = "REQ.md"
CR_FILE_NAME = "CR.md"
UML_FILE_NAME = "UML_DESIGNED_level3.md"

PROMPT_SNAPSHOT_FILE = "prompt.txt"
TIMEOUT = 1200

# Clean sample directory before each run
CLEAN_SAMPLE_DIR_BEFORE_RUN = True


# ============================================================================
# Prompt Templates
# ============================================================================

SYSTEM_TEMPLATE = """You are a strict Java code generator.

## Hard constraints
- Every class MUST include a no-argument constructor (add an empty one if none is defined).
- Avoid using the `final` keyword unless absolutely necessary for compilation.
- Every field MUST provide public getter and setter methods.
- No package declarations - all classes in default package, no imports between project classes.
- Generate a .java file for EVERY class, interface, and enum in the class diagram.
- One top-level type per .java file, plain filename only (e.g. GameMap.java), no subdirectories.

## Output format
Use bash commands to create the required .java files in the current working directory.
Write ALL .java files directly in the current working directory. Do NOT create subdirectories or use /tmp.
For file creation, bash heredocs are recommended:
```bash
cat > FileName.java << 'EOF'
<file content>
EOF
```
After writing ALL files and compiling successfully, submit:
```bash
echo COMPLETE_TASK_AND_SUBMIT_FINAL_OUTPUT
```

## Compilation & Fixing
- After generating all .java files, use bash to compile them with `javac`.
- If compilation errors occur, fix them.
"""

INSTANCE_TEMPLATE = """## Inputs

### 1) Domain Description (Requirements)
{{ req_text }}

### 2) Functional Requirements (Computational Requirements)
{{ cr_text }}

### 3) Design Model (Class Diagram)
```plantuml
{{ uml_text }}
```
"""


# ============================================================================
# Helper Functions (from opencode_gen)
# ============================================================================

def _extract_version_block(md_path: Path) -> str:
    """Return text between `// ==version1==` and `// ==end==`."""
    if not md_path.exists():
        return ""
    text = md_path.read_text(encoding="utf-8", errors="ignore")
    m = re.search(r"//\s*==version1==([\s\S]*.)//\s*==end==", text)
    return m.group(1).strip() if m else text.strip()


def _scan_system_dirs(base_dir: Path) -> list[str]:
    """Auto-scan base_dir for system folders containing REQ.md (skip paXX-result)."""
    if not base_dir.exists():
        return []
    result = []
    for d in sorted(base_dir.iterdir()):
        if not d.is_dir():
            continue
        if re.match(r"^pa\d+-result$", d.name):
            continue
        if (d / REQ_FILE_NAME).exists():
            result.append(d.name)
    return result


def _write_prompt_snapshot(base_dir: Path, prompt: str) -> None:
    (base_dir / PROMPT_SNAPSHOT_FILE).write_text(prompt, encoding="utf-8")


def _render_prompt_snapshot(task_data: dict) -> str:
    instance = Template(INSTANCE_TEMPLATE).render(**task_data)
    return f"SYSTEM:\n{SYSTEM_TEMPLATE}\n\nINSTANCE:\n{instance}"


def _prepare_sample_dir(sample_dir: Path) -> None:
    if CLEAN_SAMPLE_DIR_BEFORE_RUN and sample_dir.exists():
        shutil.rmtree(sample_dir)
    sample_dir.mkdir(parents=True, exist_ok=True)


def _count_java_files(directory: Path) -> int:
    if not directory.exists():
        return 0
    return len(list(directory.glob("*.java")))


def _list_generated_java_files(sample_dir: Path) -> list[Path]:
    """Only count .java files in the sample_dir root."""
    if not sample_dir.exists():
        return []
    return sorted(p for p in sample_dir.glob("*.java") if p.is_file())


def _list_nested_java_files(sample_dir: Path) -> list[Path]:
    """Diagnostic: find .java files erroneously placed in sub-directories."""
    if not sample_dir.exists():
        return []
    root_files = set(_list_generated_java_files(sample_dir))
    all_files = set(p for p in sample_dir.rglob("*.java") if p.is_file())
    return sorted(all_files - root_files)


# ============================================================================
# Agent (from swejavabench)
# ============================================================================

def _run_agent(
    work_dir: Path,
    task_data: dict,
    timeout: int = TIMEOUT,
) -> dict:
    """Run mini-swe-agent to generate Java files."""
    print(
        f"[mini-swe] creating agent: model={MODEL_NAME}, api_base={API_BASE}, "
        f"work_dir={work_dir}",
        flush=True,
    )

    agent = DefaultAgent(
        LitellmModel(
            model_name=f"openai/{MODEL_NAME}",
            model_kwargs={
                "api_base": API_BASE,
                "api_key": API_KEY,
                "extra_body": {
                    "reasoning": {
                        "effort": "none"
                    }
                },
            },
            cost_tracking="ignore_errors",
        ),
        LocalEnvironment(cwd=str(work_dir), timeout=timeout),
        system_template=SYSTEM_TEMPLATE,
        instance_template=INSTANCE_TEMPLATE,
        step_limit=100,
        cost_limit=0.0,
    )

    print("[mini-swe] agent created; starting agent.run()", flush=True)
    result = agent.run(**task_data)
    print(
        f"[mini-swe] agent.run() returned: exit_status={result.get('exit_status')}",
        flush=True,
    )

    agent.save(
        work_dir / "mini_swe_agent_final_state.json",
        {
            "info": {
                "run_result": result,
                "sample_dir": str(work_dir.resolve()),
                "model": MODEL_NAME,
            }
        },
    )

    return result


def _save_run_logs(
    sample_dir: Path,
    result: dict,
    prompt_snapshot: str,
) -> None:
    """Save prompt, metadata and agent state to sample_dir."""
    sample_dir.mkdir(parents=True, exist_ok=True)

    # 1. Prompt used for this run
    (sample_dir / "mini_prompt.txt").write_text(prompt_snapshot, encoding="utf-8")

    # 2. Run metadata
    metadata = {
        "model": MODEL_NAME,
        "api_base": API_BASE,
        "sample_dir": str(sample_dir.resolve()),
        "exit_status": result.get("exit_status"),
        "submission": result.get("submission"),
    }
    (sample_dir / "mini_run_metadata.json").write_text(
        json.dumps(metadata, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )


# ============================================================================
# Core Processing
# ============================================================================

def _process_system(system_name: str) -> None:
    """Process a single system's code generation pipeline."""
    input_dir = INPUT_BASE_DIR / system_name
    out_root = SCRIPT_DIR / system_name / MODEL_DIR_NAME

    if not input_dir.exists():
        raise FileNotFoundError(f"Missing input dir: {input_dir.resolve()}")

    # Read input files (extract version blocks)
    req_text = _extract_version_block(input_dir / REQ_FILE_NAME)
    cr_text = _extract_version_block(input_dir / CR_FILE_NAME)
    uml_text = _extract_version_block(input_dir / UML_FILE_NAME)

    for name, text in [("REQ", req_text), ("CR", cr_text), ("UML", uml_text)]:
        if not text:
            raise FileNotFoundError(f"Empty or missing input: {name}")

    # Build task data for mini-swe-agent
    task_data = {
        "req_text": req_text,
        "cr_text": cr_text,
        "uml_text": uml_text,
    }

    # Save prompt snapshot (resolved instance template)
    prompt_snapshot = _render_prompt_snapshot(task_data)
    _write_prompt_snapshot(input_dir, prompt_snapshot)

    out_root.mkdir(parents=True, exist_ok=True)

    for i in range(1, SAMPLES + 1):
        sample_dir = out_root / f"sample{i}"
        _prepare_sample_dir(sample_dir)

        print(f"Running sample {i}/{SAMPLES}...", flush=True)

        try:
            result = _run_agent(sample_dir, task_data)

            java_files = _list_generated_java_files(sample_dir)
            nested_java_files = _list_nested_java_files(sample_dir)

            _save_run_logs(sample_dir, result, prompt_snapshot)

            if nested_java_files:
                nested_report = "\n".join(
                    str(p.relative_to(sample_dir)) for p in nested_java_files
                )
                (sample_dir / "nested_java_files_warning.txt").write_text(
                    nested_report + "\n", encoding="utf-8",
                )
                print(
                    f"sample{i}: WARNING: {len(nested_java_files)} nested .java files "
                    f"found; not counted as valid root outputs"
                )

            if result.get("exit_status") == "Submitted":
                names = ", ".join(p.name for p in java_files) if java_files else "(none)"
                print(
                    f"sample{i}: completed, generated {len(java_files)} .java files: {names}"
                )
            else:
                print(
                    f"sample{i}: exit_status={result.get('exit_status')}, "
                    f"generated {len(java_files)} .java files"
                )

        except Exception as e:
            (sample_dir / "error.txt").write_text(str(e), encoding="utf-8")
            print(f"sample{i}: failed with error - {e}")


# ============================================================================
# Main
# ============================================================================

def main() -> None:
    system_names = SYSTEM_DIRS if SYSTEM_DIRS else _scan_system_dirs(INPUT_BASE_DIR)

    print(f"{'=' * 70}")
    print(f"  MiniSWE-Gen REQ+CR+UML")
    print(f"{'=' * 70}")
    print(f"  Model:        {MODEL_NAME}")
    print(f"  API Base:     {API_BASE}")
    print(f"  Input base:   {INPUT_BASE_DIR}")
    print(f"  Output base:  {SCRIPT_DIR}")
    print(f"  Model dir:    {MODEL_DIR_NAME}")
    print(f"  Systems:      {system_names}")
    print(f"  Samples:      {SAMPLES}")
    print(f"{'=' * 70}\n")

    if not system_names:
        print("[WARN] No system directories found. Exiting.")
        return

    for system_name in system_names:
        try:
            print(f"\n{'#' * 60}")
            print(f"Processing: {system_name}")
            print(f"{'#' * 60}")
            _process_system(system_name)
        except FileNotFoundError as exc:
            print(f"[SKIP] {system_name}: {exc}")


if __name__ == "__main__":
    main()
