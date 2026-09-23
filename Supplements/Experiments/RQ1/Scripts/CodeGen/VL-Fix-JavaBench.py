#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
LLM direct-call Java code generator - adapted for javabench input format

Based on the architecture of fjc_fix_gen_DD+FR+DeM.py, adapted for javabench inputs:
  - requirements.txt  -> Domain Description + Functional Requirements
  - *.plantuml        -> Design Model (class diagram)
  - implemented_classes.txt -> Reference Implementations

Supported phases:
  1. generate: LLM generates FJC.java (single file, all classes merged)
  2. compile:  Compile FJC.java
  3. fix:      When compilation fails, LLM fixes (up to 3 times)

Usage:
    python llm-codegen-javabench.py
    # Modify BASE_DIRS and MODEL_NAME below to control the run scope
"""
from __future__ import annotations

import itertools
import json
import locale
import os
import re
import subprocess
import sys
import threading
import time
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Tuple

from openai import OpenAI, APIStatusError

# ============================================================================
# API Configuration
# ============================================================================
BASE_URL = "" 
API_KEYS = [""]
MODEL_LIST = [""]
MODEL_NAME = ""  # Custom output directory name, leave empty to auto-use MODEL_LIST[0]
DEFAULT_MAX_TOKENS = 16384
MAX_MODEL_TOKENS = [16384] #

# ============================================================================
# Directory Configuration
# ============================================================================
SCRIPT_DIR = Path(__file__).parent
INPUT_BASE_DIR = SCRIPT_DIR.parent / "projects" 

BASE_DIRS = [
    Path("pa19-result"),
    Path("pa20-result"),
    Path("pa21-result"),
    Path("pa22-result"),
]
SAMPLES = 5

REQUIREMENTS_FILE_NAME = "requirements.txt"
IMPLEMENTED_FILE_NAME = "implemented_classes.txt"
PROMPT_SNAPSHOT_FILE = "prompt.txt"
TIMEOUT = 1200
CLEAN_SAMPLE_DIR_BEFORE_RUN = True
PHASES = [ 'compile', 'fix']  # Freely combinable: generate, compile, fix

# lib directory required for compilation (junit jars, etc.)
LIB_DIR = SCRIPT_DIR / "lib"

# ============================================================================
# Parallelism
# ============================================================================
MAX_SAMPLE_WORKERS = 5

# ============================================================================
# OpenAI Clients
# ============================================================================
if not API_KEYS:
    raise RuntimeError("API_KEYS is empty")

_CLIENTS = [OpenAI(api_key=k, base_url=BASE_URL) for k in API_KEYS]
_client_cycle = itertools.cycle(_CLIENTS)
_client_lock = threading.Lock()


def _next_client() -> OpenAI:
    with _client_lock:
        return next(_client_cycle)


def _call_openai(prompt: str, model: str, system_prompt: str = "",
                 max_retry: int | None = None, max_tokens: int | None = None) -> str:
    """Call OpenAI-compatible API with retry and backoff"""
    max_retry = max_retry or len(API_KEYS) * 2
    if max_tokens is None:
        try:
            idx = MODEL_LIST.index(model)
            if 0 <= idx < len(MAX_MODEL_TOKENS) and MAX_MODEL_TOKENS[idx] > 0:
                max_tokens = MAX_MODEL_TOKENS[idx]
            else:
                max_tokens = DEFAULT_MAX_TOKENS
        except ValueError:
            max_tokens = DEFAULT_MAX_TOKENS

    backoff = 1.0
    for attempt in range(max_retry):
        client = _next_client()
        try:
            messages = []
            if system_prompt:
                messages.append({"role": "system", "content": system_prompt})
            messages.append({"role": "user", "content": prompt})

            resp = client.chat.completions.create(
                model=model,
                messages=messages,
                temperature=0.7,
                max_tokens=max_tokens,
                extra_body={
                    "reasoning": {
                        "effort": "none"
                    }
                },
            )

            if hasattr(resp, 'usage') and resp.usage:
                print(f"  API OK | in={resp.usage.prompt_tokens} "
                      f"out={resp.usage.completion_tokens} total={resp.usage.total_tokens}")

            return resp.choices[0].message.content

        except APIStatusError as e:
            if e.status_code in (400, 429, 500, 502, 503):
                print(f"  API error {e.status_code}, retry in {backoff}s...")
                time.sleep(backoff)
                backoff = min(backoff * 2, 16)
                continue
            raise
        except Exception as e:
            print(f"  API error: {type(e).__name__}: {e}, retry in {backoff}s...")
            time.sleep(backoff)
            backoff = min(backoff * 2, 16)
            continue

    raise RuntimeError(f"API call failed after {max_retry} retries")


# ============================================================================
# Prompt Templates
# ============================================================================
SYSTEM_PROMPT = (
    "You are a strict Java code generator. "
    "Generate COMPLETE, compilable, and well-documented Java code "
    "based on domain descriptions, functional requirements, and design models."
)

GENERATE_PROMPT = """
# Task:
Generate Java code based on the Design Model, Domain Description, and Functional Requirements below.

# Hard constraints
- DO NOT include package declaration - start directly with import statements.
- Every field MUST provide public getter and setter methods.
- Every class MUST include a no-argument constructor .
- Avoid using the `final` keyword unless absolutely necessary for compilation.
- Generate code for EVERY class, interface, and enum in the class diagram. Omitting any is a critical failure.
- Use plain class names only (e.g. GameMap, not com.example.GameMap). No subdirectories or nested packages.
- ALL classes MUST be package-private (no `public` modifier on any top-level class, interface, or enum). Only ONE file (FJC.java) will be compiled - multiple public classes cause compilation failure.
- ALL necessary import statements MUST be included at the top of the file. Missing imports cause compilation failure.
- Pre-written code provided below MUST be copied VERBATIM into your output. Do NOT omit, skip, rewrite, or summarize any pre-written class - include every line exactly as given.

## Inputs

### 1) Class diagram (PlantUML)
```plantuml
{class_diagram}
```

### 2) Requirements
{requirements}

### 3) Pre-Written Code (MUST COPY VERBATIM into output)
{reference_implementations}

# Output
```java
<ALL Java classes in ONE file: copy ALL pre-written code above VERBATIM, then add your newly generated classes. Do NOT omit any pre-written class.>
```
"""

FIX_SYSTEM_PROMPT = (
    "You are a Java code debugging and fixing expert. "
    "Fix compilation errors while maintaining the original functionality."
)

FIX_PROMPT = """
# Task:
You are given Java code that failed to compile, along with the compiler error messages.
Your job is to fix ONLY the compilation errors.

## Rules
1. Only modify the code that causes compilation errors. Do NOT delete, rewrite, or modify any other working code.
2. Preserve all existing classes, methods, fields, and their signatures exactly as they are - unless a signature itself is the cause of the error.
3. Output the FULL, COMPLETE Java code including all imports, classes, and methods. Never truncate or abbreviate.
4. Keep all classes in the default package (no package declaration). Every class must have a no-argument constructor.

# Previous FJC.java Code (with compilation errors):
```java
{fjc_code}
```

# Compilation Error Messages:
```
{compile_errors}
```

# Domain Description & Functional Requirements:
{requirements}

# Output
```java
<Complete revised Java code. Must include all imports, classes, and methods.>
```
"""


# ============================================================================
# Helper Functions
# ============================================================================

def _strip_code_fence(text: str) -> str:
    """Extract code inside first ``` block or return text unchanged."""
    m = re.search(r"```(\w+).\n([\s\S]+.)```", text)
    if m:
        return m.group(2).strip()
    m_start = re.match(r"```\w*\n([\s\S]+)", text)
    if m_start:
        return m_start.group(1).strip()
    return text.strip()


def _remove_public_modifiers(code: str) -> str:
    """Remove `public` before top-level class/enum/interface declarations.
    Handles modifiers like final, abstract, strictfp between public and class.
    """
    lines = code.splitlines()
    cleaned = [re.sub(r"^\s*public\s+((.:final\s+|abstract\s+|strictfp\s+)*(.:class|interface|enum))", r"\1", l) for l in lines]
    return "\n".join(cleaned)


def _collect_and_move_imports(code: str) -> str:
    """Collect all import statements from anywhere in the file and move them to the top.
    Handles imports scattered in the middle of class bodies (common LLM mistake).
    """
    lines = code.splitlines()
    imports = []
    other_lines = []
    import_re = re.compile(r"^\s*import\s+")
    for line in lines:
        if import_re.match(line):
            imports.append(line.strip())
        else:
            other_lines.append(line)
    # Deduplicate while preserving order
    seen = set()
    unique_imports = []
    for imp in imports:
        if imp not in seen:
            seen.add(imp)
            unique_imports.append(imp)
    # Reconstruct: imports at top, then rest
    result_lines = unique_imports + other_lines
    return "\n".join(result_lines)


def _extract_ref_imports(impl_raw: str) -> List[str]:
    """Extract import statements from reference implementation files.
    These imports are needed because reference classes are copied verbatim.
    """
    imports = []
    import_re = re.compile(r"^\s*import\s+")
    for line in impl_raw.splitlines():
        if import_re.match(line):
            imports.append(line.strip())
    return imports


def _ensure_ref_imports(code: str, ref_imports: List[str]) -> str:
    """Ensure all reference implementation imports are present in the code."""
    if not ref_imports:
        return code
    lines = code.splitlines()
    existing = {l.strip() for l in lines if re.match(r"^\s*import\s+", l)}
    missing = [imp for imp in ref_imports if imp not in existing]
    if not missing:
        return code
    # Find the position after the last existing import, or beginning of file
    insert_pos = 0
    for i, line in enumerate(lines):
        if re.match(r"^\s*import\s+", line):
            insert_pos = i + 1
    for imp in reversed(missing):
        lines.insert(insert_pos, imp)
    return "\n".join(lines)


def _resolve_class_diagram_file(input_dir: Path) -> Path:
    """Auto-find .plantuml file in input_dir"""
    candidates = sorted(input_dir.glob("*.plantuml"))
    if len(candidates) == 1:
        return candidates[0]
    if len(candidates) == 0:
        raise FileNotFoundError(f"No .plantuml file in {input_dir}")
    raise FileNotFoundError(
        f"Multiple .plantuml files in {input_dir}: {', '.join(p.name for p in candidates)}"
    )


# --- Reference implementation parsing (consistent with javabench_codegen) ---

FILE_START_RE = re.compile(r"^-+\s*(.P<name>[^-\s].*.\.java)\s+start\s*-*$", re.IGNORECASE)
FILE_END_RE = re.compile(r"^-+\s*(.P<name>[^-\s].*.\.java)\s+end\s*-*$", re.IGNORECASE)


def _format_implemented_for_prompt(implemented: str) -> str:
    """Parse implemented_classes.txt and output FILE/SNIPPET formatted prompt text"""
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
                files.append((current_name, "\n".join(current_lines).rstrip() + "\n"))
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

    # Build output
    sections: list[str] = []
    sections.append("### Pre-written full files (MUST COPY VERBATIM into output)")
    if files:
        for name, content in files:
            sections.append(f"---FILE: {name}---")
            sections.append(content.rstrip("\n"))
            sections.append(f"---END FILE---")
    else:
        sections.append("(none)")

    sections.append("")
    sections.append("### Pre-written snippets (MUST COPY VERBATIM into output)")
    # snippet_lines is split by blank lines or labels (simplified: output as a whole)
    snippet_text = "\n".join(snippet_lines).strip()
    if snippet_text:
        sections.append(snippet_text)
    else:
        sections.append("(none)")

    return "\n".join(sections).rstrip() + "\n"


# ============================================================================
# Compilation
# ============================================================================

def _get_system_encoding() -> str:
    if sys.platform == 'win32':
        enc = locale.getpreferredencoding()
        if enc.lower() in ('cp936', 'gbk', 'gb2312'):
            return 'gbk'
    return 'utf-8'


def _collect_jars(lib_dir: Path) -> List[Path]:
    if not lib_dir or not lib_dir.exists():
        return []
    return [p for p in lib_dir.iterdir() if p.suffix == ".jar"]


def _build_classpath(jars: List[Path], classes_dir: Path) -> str:
    elements = [str(j) for j in jars] + [str(classes_dir)]
    return ";".join(elements) if sys.platform == 'win32' else ":".join(elements)


def compile_java(src_path: Path, classes_dir: Path, jars: List[Path]) -> Tuple[bool, str]:
    """Compile a single .java file"""
    try:
        classes_dir.mkdir(parents=True, exist_ok=True)
        classpath = _build_classpath(jars, classes_dir)

        cmd = [
            "javac", "-encoding", "UTF-8",
            "-cp", classpath,
            "-d", str(classes_dir),
            str(src_path),
        ]

        result = subprocess.run(
            cmd, capture_output=True, text=True,
            encoding=_get_system_encoding(), errors='replace', timeout=60,
        )

        success = result.returncode == 0
        output = (result.stdout or "") + (result.stderr or "")
        return success, output

    except subprocess.TimeoutExpired:
        return False, "Compilation timeout (60s)"
    except Exception as e:
        return False, f"Compilation error: {e}"


# ============================================================================
# Core Phases
# ============================================================================

def run_generate(input_dir: Path, sample_src: Path, model: str) -> Tuple[bool, Path]:
    """Phase 1: Call LLM to generate FJC.java"""
    try:
        req_text = (input_dir / REQUIREMENTS_FILE_NAME).read_text(encoding="utf-8")
        uml_text = _resolve_class_diagram_file(input_dir).read_text(encoding="utf-8")
        impl_raw = (input_dir / IMPLEMENTED_FILE_NAME).read_text(encoding="utf-8")
        ref_impl = _format_implemented_for_prompt(impl_raw)

        prompt = GENERATE_PROMPT.format(
            requirements=req_text,
            class_diagram=uml_text,
            reference_implementations=ref_impl,
        )

        print(f"  [generate] calling LLM ({model})...")
        code = _call_openai(prompt, model, system_prompt=SYSTEM_PROMPT)
        code = _strip_code_fence(code)
        code = _remove_public_modifiers(code)
        code = _collect_and_move_imports(code)
        ref_imports = _extract_ref_imports(impl_raw)
        code = _ensure_ref_imports(code, ref_imports)

        sample_src.mkdir(parents=True, exist_ok=True)
        out_path = sample_src / "FJC.java"
        out_path.write_text(code, encoding="utf-8")
        print(f"  [generate] saved -> {out_path}")
        return True, out_path

    except Exception as e:
        print(f"  [generate] FAILED: {e}")
        return False, Path()


def run_compile(fjc_path: Path, classes_dir: Path, jars: List[Path]) -> Tuple[bool, str]:
    """Phase 2: Compile FJC.java"""
    if not fjc_path.exists():
        return False, "FJC.java not found"
    success, output = compile_java(fjc_path, classes_dir, jars)
    status = "OK" if success else "FAIL"
    print(f"  [compile] {status}")
    return success, output


def run_fix(fjc_path: Path, classes_dir: Path, jars: List[Path],
            input_dir: Path, model: str, compile_error: str,
            max_attempts: int = 3) -> Tuple[bool, int, str]:
    """Phase 3: Compilation failure -> LLM fix -> Recompile (loop up to max_attempts times)"""
    req_text = (input_dir / REQUIREMENTS_FILE_NAME).read_text(encoding="utf-8")
    impl_raw = (input_dir / IMPLEMENTED_FILE_NAME).read_text(encoding="utf-8")
    ref_imports = _extract_ref_imports(impl_raw)
    fix_info_path = fjc_path.parent.parent.parent / "fix_info.txt"
    last_error = compile_error

    for attempt in range(1, max_attempts + 1):
        print(f"  [fix] attempt {attempt}/{max_attempts}")

        # Record error
        with open(fix_info_path, 'w', encoding='utf-8') as f:
            f.write(f"=== Fix Attempt {attempt} ===\n")
            f.write(f"Time: {datetime.now():%Y-%m-%d %H:%M:%S}\n\n")
            f.write(last_error + "\n")

        current_code = fjc_path.read_text(encoding="utf-8")
        prompt = FIX_PROMPT.format(
            fjc_code=current_code,
            compile_errors=last_error,
            requirements=req_text,
        )

        try:
            fixed = _call_openai(prompt, model, system_prompt=FIX_SYSTEM_PROMPT)
            fixed = _strip_code_fence(fixed)
            fixed = _remove_public_modifiers(fixed)
            fixed = _collect_and_move_imports(fixed)
            fixed = _ensure_ref_imports(fixed, ref_imports)
            fjc_path.write_text(fixed, encoding="utf-8")

            success, output = compile_java(fjc_path, classes_dir, jars)
            last_error = output

            if success:
                print(f"  [fix] compile OK after {attempt} attempt(s)")
                with open(fix_info_path, 'a', encoding='utf-8') as f:
                    f.write(f"\nFix succeeded at attempt {attempt}\n")
                return True, attempt, output

            # Consider warnings-only as success
            if "error:" not in output.lower():
                print(f"  [fix] warnings only, treating as success")
                return True, attempt, output

        except Exception as e:
            print(f"  [fix] error: {e}")
            last_error = str(e)

    print(f"  [fix] FAILED after {max_attempts} attempts")
    with open(fix_info_path, 'a', encoding='utf-8') as f:
        f.write(f"\nFix failed after {max_attempts} attempts\n")
    return False, max_attempts, last_error


# ============================================================================
# Sample Processing
# ============================================================================

def process_sample(input_dir: Path, sample_dir: Path, model: str,
                   jars: List[Path], phases: List[str]) -> Dict:
    """Process a single sample through the full pipeline"""
    src_root = sample_dir / "src" / "main"
    classes_root = sample_dir / "classes"

    result = {
        'compile_success': False,
        'compile_output': '',
        'fix_attempts': 0,
    }

    # Phase 1: Generate
    if 'generate' in phases:
        ok, fjc_path = run_generate(input_dir, src_root, model)
        if not ok:
            return result
    else:
        fjc_path = src_root / "FJC.java"

    # Phase 2: Compile
    if 'compile' in phases:
        success, output = run_compile(fjc_path, classes_root, jars)
        result['compile_success'] = success
        result['compile_output'] = output

        # Phase 3: Fix (if compile failed)
        if not success and 'fix' in phases:
            has_error = "error:" in output.lower()
            if has_error:
                fix_ok, fix_count, fix_output = run_fix(
                    fjc_path, classes_root, jars,
                    input_dir, model, output,
                )
                result['fix_attempts'] = fix_count
                result['compile_success'] = fix_ok
                result['compile_output'] = fix_output

    return result


def process_fix_sample(input_dir: Path, src_sample_dir: Path,
                       fix_sample_dir: Path, model: str,
                       jars: List[Path]) -> Dict:
    """Process a single sample's fix pipeline, outputting to a separate fix directory.

    - Read FJC.java from src_sample_dir
    - Compilation succeeds -> copy as-is to fix_sample_dir
    - Compilation fails -> LLM fixes and saves to fix_sample_dir
    """
    import shutil

    src_fjc = src_sample_dir / "src" / "main" / "FJC.java"
    fix_src_root = fix_sample_dir / "src" / "main"
    fix_classes_root = fix_sample_dir / "classes"
    fix_src_root.mkdir(parents=True, exist_ok=True)
    fix_classes_root.mkdir(parents=True, exist_ok=True)

    result = {
        'compile_success': False,
        'compile_output': '',
        'fix_attempts': 0,
        'source_sample': str(src_sample_dir),
    }

    if not src_fjc.exists():
        print(f"  [fix] FJC.java not found in {src_sample_dir}, skipping")
        return result

    # Copy FJC.java to fix directory
    fix_fjc = fix_src_root / "FJC.java"
    shutil.copy2(src_fjc, fix_fjc)

    # Compile
    success, output = compile_java(fix_fjc, fix_classes_root, jars)
    result['compile_success'] = success
    result['compile_output'] = output

    if success:
        print(f"  [fix] compile OK, copied as-is")
        return result

    # Compilation failed -> LLM fix
    has_error = "error:" in output.lower()
    if has_error:
        fix_ok, fix_count, fix_output = run_fix(
            fix_fjc, fix_classes_root, jars,
            input_dir, model, output,
        )
        result['fix_attempts'] = fix_count
        result['compile_success'] = fix_ok
        result['compile_output'] = fix_output

    return result


# ============================================================================
# Main
# ============================================================================

def main():
    model = MODEL_LIST[0] if MODEL_LIST else "unknown"
    output_name = MODEL_NAME if MODEL_NAME else model
    jars = _collect_jars(LIB_DIR)

    print(f"{'='*70}")
    print(f"  LLM-CodeGen-JavaBench")
    print(f"{'='*70}")
    print(f"  Model:        {model}")
    print(f"  Input base:   {INPUT_BASE_DIR}")
    print(f"  Output base:  {SCRIPT_DIR}")
    print(f"  Dirs:         {[str(d) for d in BASE_DIRS]}")
    print(f"  Samples:      {SAMPLES}")
    print(f"  JARs:         {len(jars)} found")
    print(f"  Phases:       {', '.join(PHASES)}")
    print(f"{'='*70}\n")

    # Save prompt snapshot
    req_placeholder = "<will be filled from requirements.txt>"
    uml_placeholder = "<will be filled from *.plantuml>"
    ref_placeholder = "<will be filled from implemented_classes.txt>"
    snapshot = GENERATE_PROMPT.format(
        requirements=req_placeholder,
        class_diagram=uml_placeholder,
        reference_implementations=ref_placeholder,
    )

    for base_dir in BASE_DIRS:
        input_dir = INPUT_BASE_DIR / base_dir.name
        out_root = SCRIPT_DIR / base_dir.name / output_name

        if not input_dir.exists():
            print(f"[SKIP] {base_dir.name}: input dir not found ({input_dir})")
            continue

        print(f"\n{'#'*60}")
        print(f"Processing: {base_dir.name}")
        print(f"  Input:  {input_dir}")
        print(f"  Output: {out_root}")
        print(f"{'#'*60}")

        # Save prompt snapshot to input directory
        (input_dir / PROMPT_SNAPSHOT_FILE).write_text(snapshot, encoding="utf-8")
        out_root.mkdir(parents=True, exist_ok=True)

        for i in range(1, SAMPLES + 1):
            sample_dir = out_root / f"sample{i}"

            # Only clean directory when generate phase is included; preserve existing files for step-by-step execution (e.g. standalone compile+fix)
            should_clean = CLEAN_SAMPLE_DIR_BEFORE_RUN and 'generate' in PHASES
            if should_clean and sample_dir.exists():
                import shutil
                shutil.rmtree(sample_dir)
            sample_dir.mkdir(parents=True, exist_ok=True)

            print(f"\n--- sample {i}/{SAMPLES} ---")

            # When fix outputs independently, first round only does generate+compile, not in-place fix
            first_phases = [p for p in PHASES if p != 'fix'] if 'fix' in PHASES and 'generate' not in PHASES else PHASES
            result = process_sample(
                input_dir=input_dir,
                sample_dir=sample_dir,
                model=model,
                jars=jars,
                phases=first_phases,
            )

            # Save result
            meta = {
                "model": model,
                "sample_dir": str(sample_dir.resolve()),
                "compile_success": result['compile_success'],
                "fix_attempts": result['fix_attempts'],
            }
            (sample_dir / "run_metadata.json").write_text(
                json.dumps(meta, ensure_ascii=False, indent=2), encoding="utf-8",
            )

            status = "OK" if result['compile_success'] else "FAIL"
            fix_info = f" (fix: {result['fix_attempts']})" if result['fix_attempts'] else ""
            print(f"  Result: {status}{fix_info}")

    # Fix phase: output independently to {output_name}-fix directory (only triggered when generate is not included)
    if 'fix' in PHASES and 'generate' not in PHASES:
        fix_output_name = f"{output_name}-fix"
        print(f"\n{'='*70}")
        print(f"  Fix Phase: {output_name} -> {fix_output_name}")
        print(f"{'='*70}")

        for base_dir in BASE_DIRS:
            input_dir = INPUT_BASE_DIR / base_dir.name
            src_root = SCRIPT_DIR / base_dir.name / output_name
            fix_root = SCRIPT_DIR / base_dir.name / fix_output_name

            if not src_root.exists():
                print(f"[SKIP] {base_dir.name}: source dir not found ({src_root})")
                continue

            print(f"\n{'#'*60}")
            print(f"Fix Processing: {base_dir.name}")
            print(f"  Source:  {src_root}")
            print(f"  Output:  {fix_root}")
            print(f"{'#'*60}")

            fix_root.mkdir(parents=True, exist_ok=True)

            for i in range(1, SAMPLES + 1):
                src_sample = src_root / f"sample{i}"
                fix_sample = fix_root / f"sample{i}"

                if not src_sample.exists():
                    print(f"\n--- sample {i}/{SAMPLES} --- SKIP (source not found)")
                    continue

                # Clean fix directory
                if fix_sample.exists():
                    import shutil
                    shutil.rmtree(fix_sample)
                fix_sample.mkdir(parents=True, exist_ok=True)

                print(f"\n--- sample {i}/{SAMPLES} ---")

                result = process_fix_sample(
                    input_dir=input_dir,
                    src_sample_dir=src_sample,
                    fix_sample_dir=fix_sample,
                    model=model,
                    jars=jars,
                )

                # Save result
                meta = {
                    "model": model,
                    "sample_dir": str(fix_sample.resolve()),
                    "source_sample": str(src_sample.resolve()),
                    "compile_success": result['compile_success'],
                    "fix_attempts": result['fix_attempts'],
                }
                (fix_sample / "run_metadata.json").write_text(
                    json.dumps(meta, ensure_ascii=False, indent=2), encoding="utf-8",
                )

                status = "OK" if result['compile_success'] else "FAIL"
                fix_info = f" (fix: {result['fix_attempts']})" if result['fix_attempts'] else ""
                print(f"  Result: {status}{fix_info}")

    print(f"\n{'='*70}")
    print("Done.")


if __name__ == "__main__":
    main()
