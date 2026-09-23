import json
import os
import re
import shutil
import subprocess
from pathlib import Path

SCRIPT_DIR = Path(__file__).parent
INPUT_BASE_DIR = SCRIPT_DIR.parent / "projects"  # 

# ============================================================================
# Usage Guide
# Manually set OPENCODE_ATTACH=1
# And start opencode serve --port 4096 in advance
# The script must run in attach/server mode.
# ============================================================================

# OpenCode Configuration; GPT-5.4-Mini
OPENCODE_MODEL = os.getenv("OPENCODE_MODEL", "")
# If you really need to attach to an already running opencode server, set:
#   OPENCODE_ATTACH=1
#   OPENCODE_SERVER_URL=http://127.0.0.1:4096
OPENCODE_ATTACH = os.getenv("OPENCODE_ATTACH", "0") == "1"
SERVER_URL = os.getenv("OPENCODE_SERVER_URL", "http://127.0.0.1:4096")

print(__file__)
print("OPENCODE_MODEL =", OPENCODE_MODEL)

# ============================================================================
# Configurable Parameters
# ============================================================================
# System directories to process (leave empty to auto-scan all non paXX-result directories under INPUT_BASE_DIR)
SYSTEM_DIRS: list[str] = []

MODEL_NAME = ""  # Output directory name, unrelated to OPENCODE_MODEL
SAMPLES = 5

# Input file names
REQ_FILE_NAME = "REQ.md"
CR_FILE_NAME = "CR.md"
UML_FILE_NAME = "UML_DESIGNED_level3.md"

PROMPT_SNAPSHOT_FILE = "prompt.txt"
TIMEOUT = 1200

# Whether to clean the sample directory before each run
CLEAN_SAMPLE_DIR_BEFORE_RUN = True

# Whether to allow opencode to modify files
ALLOW_OPENCODE_EDIT = True


# ============================================================================
# Helper Functions
# ============================================================================

def _extract_version_block(md_path: Path) -> str:
    """Return text between `// ==version1==` and `// ==end==`."""
    if not md_path.exists():
        return ""
    text = md_path.read_text(encoding="utf-8", errors="ignore")
    m = re.search(r"//\s*==version1==([\s\S]*.)//\s*==end==", text)
    return m.group(1).strip() if m else text.strip()


def _scan_system_dirs(base_dir: Path) -> list[str]:
    """Auto-scan base_dir for system folders containing REQ.md (excluding paXX-result)"""
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


def _make_prompt(
    req_text: str,
    cr_text: str,
    uml_text: str,
) -> str:
    return f"""You are a strict Java code generator.

You MUST use opencode's file writing/editing tools to create .java files in the current working directory.
Do NOT output file blocks, markdown code fences, or merely print code in the response.

## Hard constraints
- Every class MUST include a no-argument constructor (add an empty one if none is defined).
- Avoid using the `final` keyword unless absolutely necessary for compilation.
- Every field MUST provide public getter and setter methods.
- No package declarations - all classes in default package, no imports between project classes.
- Generate a .java file for EVERY class, interface, and enum in the class diagram. 
- One top-level type per .java file, plain filename only (e.g. GameMap.java), no subdirectories.

## Compilation & Fixing
- After generating all .java files, use bash to compile them with `javac`.
- If compilation errors occur, fix them.

## Inputs

### 1) Domain Description (Requirements)
{req_text}

### 2) Functional Requirements (Computational Requirements)
{cr_text}

### 3) Design Model (Class Diagram)
```plantuml
{uml_text}
```
"""


def _create_opencode_config_dir(base_dir: Path) -> Path:
    """Create opencode config directory, allowing direct file writing in non-interactive batch mode."""
    config_dir = base_dir / ".opencode-codegen-config"
    config_dir.mkdir(parents=True, exist_ok=True)

    config = {
        "$schema": "https://opencode.ai/config.json",
        "permission": {
            "edit": "allow" if ALLOW_OPENCODE_EDIT else "ask",
            "read": "allow",
            "glob": "allow",
            "grep": "allow",
            "bash": "allow",
        },
    }

    (config_dir / "opencode.json").write_text(
        json.dumps(config, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    return config_dir


def _run_opencode(
    prompt: str,
    sample_dir: Path,
    config_dir: Path,
    model: str,
    timeout: int = TIMEOUT,
) -> tuple[str, str, int, list[str]]:
    """
    Use opencode run to execute code generation in the sample directory.
    The opencode agent uses write/edit tools to create .java files.
    """
    cmd = [
        "opencode",
        "--print-logs",
        "--log-level",
        "DEBUG",
        "run",
        "--model",
        model,
        "--dir",
        str(sample_dir.resolve()),
        "--format",
        "json",
    ]

    if OPENCODE_ATTACH:
        cmd.extend(["--attach", SERVER_URL])

    cmd.append(prompt)

    env = os.environ.copy()
    env["OPENCODE_CONFIG_DIR"] = str(config_dir.resolve())

    print(f"Running OpenCode, model={model}, dir={sample_dir} ...")

    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True,
        timeout=timeout,
        env=env,
    )

    stdout = result.stdout or ""
    stderr = result.stderr or ""

    print(f"[DEBUG] returncode={result.returncode}")
    print(f"[DEBUG] stdout[:300]={stdout[:300]!r}")
    print(f"[DEBUG] stderr[:300]={stderr[:300]!r}")

    return stdout, stderr, result.returncode, cmd


def _save_run_logs(
    sample_dir: Path,
    stdout: str,
    stderr: str,
    prompt: str,
    cmd: list[str],
    returncode: int,
    model: str,
) -> None:
    sample_dir.mkdir(parents=True, exist_ok=True)

    # 1. OpenCode JSON event stream
    (sample_dir / "opencode_events.jsonl").write_text(stdout, encoding="utf-8")

    # 2. stdout raw backup
    # 3. stderr / debug log
    (sample_dir / "opencode_stderr.txt").write_text(stderr or "", encoding="utf-8")

    # 4. The actual prompt used for this sample
    (sample_dir / "opencode_prompt.txt").write_text(prompt, encoding="utf-8")

    # 5. Run metadata for this sample
    metadata = {
        "model": model,
        "sample_dir": str(sample_dir.resolve()),
        "returncode": returncode,
        "command": cmd,
        "opencode_attach": OPENCODE_ATTACH,
        "server_url": SERVER_URL if OPENCODE_ATTACH else None,
    }

    (sample_dir / "opencode_run_metadata.json").write_text(
        json.dumps(metadata, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )


def _list_generated_java_files(sample_dir: Path) -> list[Path]:
    """Only count .java files in the sample_dir root directory."""
    if not sample_dir.exists():
        return []
    return sorted(p for p in sample_dir.glob("*.java") if p.is_file())


def _list_nested_java_files(sample_dir: Path) -> list[Path]:
    """Diagnostic: find .java files erroneously created in sub-directories by the model."""
    if not sample_dir.exists():
        return []
    root_files = set(_list_generated_java_files(sample_dir))
    all_files = set(p for p in sample_dir.rglob("*.java") if p.is_file())
    return sorted(all_files - root_files)


def _prepare_sample_dir(sample_dir: Path) -> None:
    if CLEAN_SAMPLE_DIR_BEFORE_RUN and sample_dir.exists():
        shutil.rmtree(sample_dir)
    sample_dir.mkdir(parents=True, exist_ok=True)


# ============================================================================
# Core Processing
# ============================================================================

def _process_system(system_name: str) -> None:
    """Process a single system's code generation pipeline"""
    input_dir = INPUT_BASE_DIR / system_name
    out_root = SCRIPT_DIR / system_name / MODEL_NAME

    if not input_dir.exists():
        raise FileNotFoundError(f"Missing input dir: {input_dir.resolve()}")

    # Read input files (extract version block content)
    req_text = _extract_version_block(input_dir / REQ_FILE_NAME)
    cr_text = _extract_version_block(input_dir / CR_FILE_NAME)
    uml_text = _extract_version_block(input_dir / UML_FILE_NAME)

    for name, text in [("REQ", req_text), ("CR", cr_text), ("UML", uml_text)]:
        if not text:
            raise FileNotFoundError(f"Empty or missing input: {name}")

    # Build prompt and save snapshot
    prompt_snapshot = _make_prompt(
        req_text=req_text,
        cr_text=cr_text,
        uml_text=uml_text,
    )
    _write_prompt_snapshot(input_dir, prompt_snapshot)

    out_root.mkdir(parents=True, exist_ok=True)

    # Create opencode config directory
    config_dir = _create_opencode_config_dir(out_root)

    for i in range(1, SAMPLES + 1):
        sample_dir = out_root / f"sample{i}"
        _prepare_sample_dir(sample_dir)

        prompt = _make_prompt(
            req_text=req_text,
            cr_text=cr_text,
            uml_text=uml_text,
        )

        stdout, stderr, returncode, cmd = _run_opencode(
            prompt=prompt,
            sample_dir=sample_dir,
            config_dir=config_dir,
            model=OPENCODE_MODEL,
        )

        _save_run_logs(
            sample_dir=sample_dir,
            stdout=stdout,
            stderr=stderr,
            prompt=prompt,
            cmd=cmd,
            returncode=returncode,
            model=OPENCODE_MODEL,
        )

        java_files = _list_generated_java_files(sample_dir)
        nested_java_files = _list_nested_java_files(sample_dir)

        if returncode != 0:
            err = (
                f"Failed, exit code {returncode}\n"
                f"stdout:\n{stdout}\n\n"
                f"stderr:\n{stderr}\n"
            )
            (sample_dir / "error.txt").write_text(err, encoding="utf-8")
            print(f"sample{i}: failed; wrote {sample_dir / 'error.txt'}")
            continue

        if nested_java_files:
            nested_report = "\n".join(str(p.relative_to(sample_dir)) for p in nested_java_files)
            (sample_dir / "nested_java_files_warning.txt").write_text(
                nested_report + "\n",
                encoding="utf-8",
            )
            print(
                f"sample{i}: WARNING: found {len(nested_java_files)} nested .java files; "
                f"they are not counted as valid root outputs"
            )

        if not java_files:
            print(
                f"sample{i}: no root-level .java files generated; "
                f"check opencode_events.jsonl and opencode_stderr.txt"
            )
        else:
            names = ", ".join(p.name for p in java_files)
            print(f"sample{i}: generated {len(java_files)} .java files: {names}")


# ============================================================================
# Main
# ============================================================================

def main() -> None:
    # Determine the list of systems to process
    if SYSTEM_DIRS:
        system_names = SYSTEM_DIRS
    else:
        system_names = _scan_system_dirs(INPUT_BASE_DIR)

    print(f"{'='*70}")
    print(f"  OpenCode-Gen REQ+CR+UML")
    print(f"{'='*70}")
    print(f"  Model:        {OPENCODE_MODEL}")
    print(f"  Input base:   {INPUT_BASE_DIR}")
    print(f"  Output base:  {SCRIPT_DIR}")
    print(f"  Systems:      {system_names}")
    print(f"  Samples:      {SAMPLES}")
    print(f"{'='*70}\n")

    if not system_names:
        print("[WARN] No system directories found. Exiting.")
        return

    for system_name in system_names:
        try:
            print(f"\n{'#'*60}")
            print(f"Processing: {system_name}")
            print(f"{'#'*60}")
            _process_system(system_name)
        except FileNotFoundError as exc:
            print(f"[SKIP] {system_name}: {exc}")


if __name__ == "__main__":
    main()
