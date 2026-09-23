"""
Java code generator using mini-swe-agent.
Based on javabenchcodegen.py, but replaces opencode with mini-swe-agent.
"""
import json
import shutil
import os
import re
from pathlib import Path

SCRIPT_DIR = Path(__file__).parent
INPUT_BASE_DIR = SCRIPT_DIR.parent / "projects"  #

from minisweagent.agents.default import DefaultAgent
from minisweagent.models.litellm_model import LitellmModel
from minisweagent.environments.local import LocalEnvironment



#google/gemini-3.1-flash-lite
MODEL_NAME = os.getenv("SWE_MODEL", "openai/gpt-5.4-mini")
API_BASE = os.getenv("SWE_API_BASE", "https://openrouter.ai/api/v1")
API_KEY = os.getenv("SWE_API_KEY", "")
OUTPUT_MODEL_NAME = "" #deepseek-v4-flash-no-reason
# ========= Directory Configuration =========
BASE_DIRS = [
    Path("pa19-result"),
    # Path("pa20-result"),
    # Path("pa21-result"),
    # Path("pa22-result"),
]

SAMPLES = 5

REQUIREMENTS_FILE_NAME = "requirements.txt"
IMPLEMENTED_FILE_NAME = "implemented_classes.txt"
CLASS_DIAGRAM_FILE_NAME = None
PROMPT_SNAPSHOT_FILE = "prompt.txt"

TIMEOUT = 1200

# Whether to clean the sample directory before each run.
# Recommended to enable to avoid interference from previous .java files.
CLEAN_SAMPLE_DIR_BEFORE_RUN = True

FILE_START_RE = re.compile(r"^-+\s*(.P<name>[^-\s].*.\.java)\s+start\s*-*$", re.IGNORECASE)
FILE_END_RE = re.compile(r"^-+\s*(.P<name>[^-\s].*.\.java)\s+end\s*-*$", re.IGNORECASE)
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


def _parse_implemented(implemented: str) -> tuple[list[tuple[str, str]], list[tuple[str, str]]]:
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


SYSTEM_TEMPLATE = """You are a strict Java code generator.

## Hard constraints
- Every class MUST include a no-argument constructor (add an empty one if none is defined).
- Avoid using the `final` keyword unless absolutely necessary for compilation.
- Every field MUST provide public getter and setter methods.
- No package declarations - all classes in default package, no imports between project classes.
- Generate a .java file for EVERY class, interface, and enum in the class diagram. Omitting any is a critical failure.
- One top-level type per .java file, plain filename only (e.g. GameMap.java), no subdirectories.

## Output format (MUST follow exactly)
Write each .java file using one bash heredoc command:
```bash
cat > FileName.java << 'EOF'
<file content>
EOF
```
Write EXACTLY ONE action per response (one file OR submit).
After writing ALL files, submit:
```bash
echo COMPLETE_TASK_AND_SUBMIT_FINAL_OUTPUT
```
"""

INPUTS_TEMPLATE = """## Inputs

### 1) Class diagram (PlantUML)
{{task.class_diagram}}

### 2) Requirements
{{task.requirements}}

### 3) Reference Implementations (COPY VERBATIM)
{{task.implemented}}
"""

REFERENCE_CODE_RULES_TEMPLATE = """---
## Reference code rules
- FILE blocks: create the .java file and copy content verbatim.
- SNIPPET blocks: insert the snippet body verbatim into the correct class.
- Do not rewrite, summarize, or modify reference code.
- Reference files are NOT already in the working directory; you must create them.
"""

TASK_TEMPLATE = """---
## Your Task
Generate ALL files: both reference implementations and newly implemented classes. Do not skip any type.
"""

INSTANCE_TEMPLATE = "\n".join(
    [
        INPUTS_TEMPLATE,
        REFERENCE_CODE_RULES_TEMPLATE,
        TASK_TEMPLATE,
    ]
)

PROMPT_TEMPLATE_KEYS = {
    "{{task.class_diagram}}": "class_diagram",
    "{{task.requirements}}": "requirements",
    "{{task.implemented}}": "implemented",
}


def _render_prompt_template(template: str, task_data: dict) -> str:
    rendered = template
    for placeholder, key in PROMPT_TEMPLATE_KEYS.items():
        rendered = rendered.replace(placeholder, task_data[key])
    return rendered


def _build_prompt_snapshot(task_data: dict) -> str:
    rendered_instance = _render_prompt_template(INSTANCE_TEMPLATE, task_data)
    return f"SYSTEM:\n{SYSTEM_TEMPLATE}\n\nINSTANCE:\n{rendered_instance}"


def _make_task_data(requirements: str, class_diagram: str, implemented: str) -> dict:
    return {
        "requirements": requirements,
        "class_diagram": class_diagram,
        "implemented": implemented,
    }


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
    result = agent.run(task_data)
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


def _count_java_files(directory: Path) -> int:
    """Count .java files in directory."""
    if not directory.exists():
        return 0
    return len(list(directory.glob("*.java")))


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

def _prepare_sample_dir(sample_dir: Path) -> None:
    if CLEAN_SAMPLE_DIR_BEFORE_RUN and sample_dir.exists():
        shutil.rmtree(sample_dir)

    sample_dir.mkdir(parents=True, exist_ok=True)

def _process_base_dir(base_dir: Path) -> None:
    print(f"\n=== Processing {base_dir} ===", flush=True)

    # Input directory: projects/paXX-result/ (centralized input source)
    input_dir = INPUT_BASE_DIR / base_dir.name
    # Output directory: workspace/paXX-result/MODEL_NAME/ (each workspace outputs independently)
    out_root = base_dir / OUTPUT_MODEL_NAME

    if not input_dir.exists():
        raise FileNotFoundError(f"Missing input dir: {input_dir.resolve()}")

    requirements_file = input_dir / REQUIREMENTS_FILE_NAME
    implemented_file = input_dir / IMPLEMENTED_FILE_NAME
    class_diagram_file = _resolve_class_diagram_file(input_dir, CLASS_DIAGRAM_FILE_NAME)

    for p in [requirements_file, class_diagram_file, implemented_file]:
        if not p.exists():
            raise FileNotFoundError(f"Missing input file: {p.resolve()}")

    requirements = _read_text(requirements_file)
    class_diagram = _read_text(class_diagram_file)
    implemented_raw = _read_text(implemented_file)
    implemented = _format_implemented_for_prompt(implemented_raw)

    task_data = _make_task_data(requirements, class_diagram, implemented)
    
    prompt_snapshot = _build_prompt_snapshot(task_data)
    _write_prompt_snapshot(input_dir, prompt_snapshot)
    out_root.mkdir(parents=True, exist_ok=True)

    for i in range(1, SAMPLES + 1):
        sample_dir = out_root / f"sample{i}"
        _prepare_sample_dir(sample_dir)

        print(f"Running sample {i}/{SAMPLES}...", flush=True)
        
        try:
            result = _run_agent(sample_dir, task_data)
            java_count = _count_java_files(sample_dir)
            _save_mini_run_logs(
                sample_dir=sample_dir,
                result=result,
                prompt_snapshot=prompt_snapshot,
            )
            if result.get("exit_status") == "Submitted":
                print(f"sample{i}: completed, generated {java_count} .java files")
            else:
                print(
                    f"sample{i}: exit_status={result.get('exit_status')}, "
                    f"generated {java_count} .java files"
                )         
        except Exception as e:
            (sample_dir / "error.txt").write_text(str(e), encoding="utf-8")
            print(f"sample{i}: failed with error - {e}")


def main() -> None:
    for base_dir in BASE_DIRS:
        try:
            _process_base_dir(base_dir)
        except FileNotFoundError as exc:
            print(f"[SKIP] {base_dir}: {exc}")


def _save_mini_run_logs(
    sample_dir: Path,
    result: dict,
    prompt_snapshot: str,
) -> None:
    sample_dir.mkdir(parents=True, exist_ok=True)

    (sample_dir / "mini_prompt.txt").write_text(
        prompt_snapshot,
        encoding="utf-8",
    )

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

if __name__ == "__main__":
    main()
