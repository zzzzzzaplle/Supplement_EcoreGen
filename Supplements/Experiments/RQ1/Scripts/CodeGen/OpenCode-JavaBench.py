import json
import os
import re
import shutil
import subprocess
from pathlib import Path

SCRIPT_DIR = Path(__file__).parent
INPUT_BASE_DIR = SCRIPT_DIR.parent / "projects"  # 

# Usage Guide
# Manually set OPENCODE_ATTACH=1
# And start opencode serve --port 4096 in advance
# The script must run in attach/server mode.
# OpenCode Configuration

OPENCODE_MODEL = os.getenv("OPENCODE_MODEL", "openrouter/GPT-5.4-Mini")

# If you really want to attach to an already running opencode server, set:
#   OPENCODE_ATTACH=1
#   OPENCODE_SERVER_URL=http://127.0.0.1:4096
# Default: no attach, directly call opencode run.
OPENCODE_ATTACH = os.getenv("OPENCODE_ATTACH", "0") == "1"
SERVER_URL = os.getenv("OPENCODE_SERVER_URL", "http://127.0.0.1:4096")

print(__file__)
print("OPENCODE_MODEL =", OPENCODE_MODEL)

# ========= Configurable JavaBench Directory Structure =========
BASE_DIRS = [
]
MODEL_NAME = "gpt-5.4-mini" 
SAMPLES = 5

REQUIREMENTS_FILE_NAME = "requirement.txt"
IMPLEMENTED_FILE_NAME = "implemented_classes.txt"
CLASS_DIAGRAM_FILE_NAME = None
PROMPT_SNAPSHOT_FILE = "prompt.txt"

TIMEOUT = 1200

# Whether to clean the sample directory before each run.
# Avoid previously generated .java files affecting current statistics.
CLEAN_SAMPLE_DIR_BEFORE_RUN = True

# Whether to allow opencode to modify files.
# The goal is to let opencode use write/edit tools to actually create .java files.
ALLOW_OPENCODE_EDIT = True

FILE_START_RE = re.compile(
    r"^-+\s*(.P<name>[^-\s].*.\.java)\s+start\s*-*$",
    re.IGNORECASE,
)
FILE_END_RE = re.compile(
    r"^-+\s*(.P<name>[^-\s].*.\.java)\s+end\s*-*$",
    re.IGNORECASE,
)
LABEL_TOKEN_RE = re.compile(r"^[A-Za-z_]\w*(.:\.[A-Za-z_]\w*)*$")
JAVA_LABEL_RE = re.compile(r"^[A-Za-z_]\w*\.java$")


def _read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def _is_label_token(text: str) -> bool:
    return LABEL_TOKEN_RE.fullmatch(text) is not None


def _is_snippet_label(line: str) -> bool:
    stripped = line.strip()
    if not stripped:
        return False

    if FILE_START_RE.match(stripped) or FILE_END_RE.match(stripped):
        return False

    if stripped.endswith(":"):
        base = stripped[:-1].strip()
        return _is_label_token(base) or JAVA_LABEL_RE.fullmatch(base) is not None

    if stripped.endswith("{"):
        base = stripped[:-1].strip()
        return _is_label_token(base)

    if JAVA_LABEL_RE.fullmatch(stripped) is not None:
        return True

    return _is_label_token(stripped) and "." in stripped


def _normalize_label(line: str) -> str:
    stripped = line.strip()
    if stripped.endswith(":") or stripped.endswith("{"):
        return stripped[:-1].strip()
    return stripped


def _parse_snippets(lines: list[str]) -> list[tuple[str, str]]:
    snippets: list[tuple[str, str]] = []
    current_label: str | None = None
    current_body: list[str] = []

    for line in lines:
        if _is_snippet_label(line):
            if current_label and any(l.strip() for l in current_body):
                snippets.append(
                    (current_label, "\n".join(current_body).rstrip() + "\n")
                )
            current_label = _normalize_label(line)
            current_body = []
            continue

        if current_label is None:
            if line.strip():
                current_label = "UNLABELED"
                current_body = [line]
            continue

        current_body.append(line)

    if current_label and any(l.strip() for l in current_body):
        snippets.append((current_label, "\n".join(current_body).rstrip() + "\n"))

    return snippets


def _parse_implemented(
    implemented: str,
) -> tuple[list[tuple[str, str]], list[tuple[str, str]]]:
    files: list[tuple[str, str]] = []
    snippet_lines: list[str] = []

    in_file = False
    current_name: str | None = None
    current_lines: list[str] = []

    for line in implemented.splitlines():
        stripped = line.strip()

        if not in_file:
            m = FILE_START_RE.match(stripped)
            if m:
                in_file = True
                current_name = m.group("name")
                current_lines = []
                continue
        else:
            m = FILE_END_RE.match(stripped)
            if m and current_name and m.group("name").lower() == current_name.lower():
                files.append(
                    (current_name, "\n".join(current_lines).rstrip() + "\n")
                )
                in_file = False
                current_name = None
                current_lines = []
                continue

        if in_file:
            current_lines.append(line)
        else:
            snippet_lines.append(line)

    if in_file and current_name:
        files.append((current_name, "\n".join(current_lines).rstrip() + "\n"))

    return files, _parse_snippets(snippet_lines)


def _format_implemented_for_prompt(implemented: str) -> str:
    """
    Keep the FILE/SNIPPET input format here so the model understands which code must be copied.
    Note: this is only the reference implementation format inside the prompt; the model is no longer required to output FILE blocks.
    """
    files, snippets = _parse_implemented(implemented)
    sections: list[str] = []

    sections.append("### Implemented full files (copy verbatim)")
    if files:
        for name, content in files:
            sections.append(f"---FILE: {name}---")
            sections.append(content.rstrip("\n"))
            sections.append("---END FILE---")
    else:
        sections.append("(none)")

    sections.append("")
    sections.append("### Implemented snippets (insert verbatim)")
    if snippets:
        for label, body in snippets:
            sections.append(f"---SNIPPET: {label}---")
            sections.append(body.rstrip("\n"))
            sections.append("---END SNIPPET---")
    else:
        sections.append("(none)")

    return "\n".join(sections).rstrip() + "\n"


def _write_prompt_snapshot(base_dir: Path, prompt: str) -> None:
    (base_dir / PROMPT_SNAPSHOT_FILE).write_text(prompt, encoding="utf-8")


def _make_prompt(
    sample_idx: int,
    requirements: str,
    class_diagram: str,
    implemented: str,
) -> str:
    return f"""You are a strict Java code generator.

You MUST use opencode's file writing/editing tools to create .java files in the current working directory.
Do NOT output file blocks, markdown code fences, or merely print code in the response.

## Hard constraints
- Every class MUST include a no-argument constructor (add an empty one if none is defined).
- Avoid using the `final` keyword unless absolutely necessary for compilation.
- Every field MUST provide public getter and setter methods.
- No package declarations - all classes in default package, no imports between project classes.
- Generate a .java file for EVERY class, interface, and enum in the class diagram. Omitting any is a critical failure.
- One top-level type per .java file, plain filename only (e.g. GameMap.java), no subdirectories.

## Reference code rules
- FILE blocks: create the .java file and copy content verbatim.
- SNIPPET blocks: insert the snippet body verbatim into the correct class.
- Do not rewrite, summarize, or modify reference code.
- Reference files are NOT already in the working directory; you must create them.

## Inputs

### 1) Class diagram (PlantUML)
{class_diagram}

### 2) Requirements
{requirements}

### 3) Reference Implementations (COPY VERBATIM)
{implemented}
"""


def _derive_class_diagram_file_name(base_dir: Path) -> str | None:
    match = re.fullmatch(r"pa(\d+)-result", base_dir.name, re.IGNORECASE)
    if not match:
        return None
    return f"PA{match.group(1)}.plantuml"


def _resolve_class_diagram_file(base_dir: Path, override: str | None) -> Path:
    if override:
        return base_dir / override

    candidates = sorted(base_dir.glob("*.plantuml"))
    if len(candidates) == 1:
        return candidates[0]

    if len(candidates) == 0:
        derived_name = _derive_class_diagram_file_name(base_dir)
        if derived_name:
            derived_path = base_dir / derived_name
            if derived_path.exists():
                return derived_path

        raise FileNotFoundError(
            f"No .plantuml file found in {base_dir.resolve()}"
        )

    names = ", ".join(p.name for p in candidates)
    raise FileNotFoundError(
        f"Multiple .plantuml files found in {base_dir.resolve()}: {names}. "
        "Set CLASS_DIAGRAM_FILE_NAME to select one."
    )


def _create_opencode_config_dir(base_dir: Path) -> Path:
    """
    Create a Python-managed opencode configuration directory.
    Purpose: let opencode run write files directly in non-interactive batch mode without blocking on permission confirmation.
    """
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
    Key changes:
    - Use --dir sample_dir to let opencode run in the sample directory.
    - No longer require stdout to output file blocks.
    - After run completes, main() directly checks .java files under sample_dir.
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

    # 1. OpenCode JSON event stream:
    (sample_dir / "opencode_events.jsonl").write_text(stdout, encoding="utf-8")

    # 2. stdout raw backup
    # 3. stderr / debug log
    (sample_dir / "opencode_stderr.txt").write_text(stderr or "", encoding="utf-8")

    # 4. The actual prompt used for this sample
    (sample_dir / "opencode_prompt.txt").write_text(prompt, encoding="utf-8")

    # 5. Run metadata for this run
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
    """
    Only count .java files in the root of sample_dir.
    This forces the model to not create subdirectories.
    """
    if not sample_dir.exists():
        return []

    return sorted(p for p in sample_dir.glob("*.java") if p.is_file())


def _list_nested_java_files(sample_dir: Path) -> list[Path]:
    """
    For diagnostics: if the model incorrectly creates subdirectories, this can detect them.
    But we do not count them as successful output.
    """
    if not sample_dir.exists():
        return []

    root_files = set(_list_generated_java_files(sample_dir))
    all_files = set(p for p in sample_dir.rglob("*.java") if p.is_file())
    return sorted(all_files - root_files)


def _prepare_sample_dir(sample_dir: Path) -> None:
    if CLEAN_SAMPLE_DIR_BEFORE_RUN and sample_dir.exists():
        shutil.rmtree(sample_dir)

    sample_dir.mkdir(parents=True, exist_ok=True)


def _process_base_dir(base_dir: Path) -> None:
    print(f"\n=== Processing {base_dir} ===")

    # Input directory: projects/paXX-result/ (centralized input source)
    input_dir = INPUT_BASE_DIR / base_dir.name
    # Output directory: workspace/paXX-result/MODEL_NAME/ (each workspace outputs independently)
    out_root = base_dir / MODEL_NAME

    if not input_dir.exists():
        raise FileNotFoundError(
            f"Missing input dir: {input_dir.resolve()}"
        )

    requirements_file = input_dir / REQUIREMENTS_FILE_NAME
    implemented_file = input_dir / IMPLEMENTED_FILE_NAME
    class_diagram_file = _resolve_class_diagram_file(
        input_dir,
        CLASS_DIAGRAM_FILE_NAME,
    )

    for p in [requirements_file, class_diagram_file, implemented_file]:
        if not p.exists():
            raise FileNotFoundError(f"Missing input file: {p.resolve()}")

    requirements = _read_text(requirements_file)
    class_diagram = _read_text(class_diagram_file)
    implemented_raw = _read_text(implemented_file)
    implemented = _format_implemented_for_prompt(implemented_raw)

    prompt_snapshot = _make_prompt(
        sample_idx=1,
        requirements=requirements,
        class_diagram=class_diagram,
        implemented=implemented,
    )
    _write_prompt_snapshot(input_dir, prompt_snapshot)

    out_root.mkdir(parents=True, exist_ok=True)

    config_dir = _create_opencode_config_dir(base_dir)

    for i in range(1, SAMPLES + 1):
        sample_dir = out_root / f"sample{i}"
        _prepare_sample_dir(sample_dir)

        prompt = _make_prompt(
            sample_idx=i,
            requirements=requirements,
            class_diagram=class_diagram,
            implemented=implemented,
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


def main() -> None:
    for base_dir in BASE_DIRS:
        try:
            _process_base_dir(base_dir)
        except FileNotFoundError as exc:
            print(f"[SKIP] {base_dir}: {exc}")


if __name__ == "__main__":
    main()
