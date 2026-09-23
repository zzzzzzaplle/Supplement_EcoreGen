#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
FJC Code Generator - Adapted for REQ+CR+UML Input Format

Supported phases:
  1. generate: LLM generates FJC.java (single file, all classes merged)
  2. compile:  Compile FJC.java
  3. fix:      When compilation fails, LLM fixes (up to 3 times)

Usage:
    python fjc_fix_gen_REQ+CR+UML.py
    # Modify SYSTEM_DIRS, MODEL_NAME, SAMPLES, etc. below to control the run scope
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
BASE_URL = "https://openrouter.ai/api/v1"
API_KEYS = ["your api key"]
MODEL_LIST = [""]
DEFAULT_MAX_TOKENS = 16384
MAX_MODEL_TOKENS = [16384]

# ============================================================================
# Directory Configuration
# ============================================================================
SCRIPT_DIR = Path(__file__).parent
INPUT_BASE_DIR = SCRIPT_DIR.parent / "projects"  
SYSTEM_DIRS: List[str] = [ ]
# SYSTEM_DIRS: List[str] = ["R123_School", "R12_RentedCarGalleryManagementSystem", "R132_MunicipalLibrary", "R144_AirlineFlights", "R22_IPOApplication", "R2_EmployeeManagementSystem"]
#"OPMS", "OLRS", "ORS", "OPRS","R123_School", "R12_RentedCarGalleryManagementSystem", "R132_MunicipalLibrary", "R144_AirlineFlights", "R22_IPOApplication", "R2_EmployeeManagementSystem"

MODEL_NAME = "gemini-3.1-flash-lite"  # Custom output directory name, leave empty to auto-use MODEL_LIST[0]
SAMPLES = 5

# Input file names
REQ_FILE_NAME = ""
CR_FILE_NAME = ""
UML_FILE_NAME = "" # Can be modified later to use a class diagram without getters/setters
PROMPT_SNAPSHOT_FILE = ""
TIMEOUT = 1200
CLEAN_SAMPLE_DIR_BEFORE_RUN = True # Whether to clean sample directory before running
# Phase combination
PHASES = ["compile", "fix"]  # Freely combinable: generate, compile, fix

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


class NoneContentError(RuntimeError):
    """API returned content=None - should not retry, fail directly."""
    pass


def _call_openai(prompt: str, model: str, system_prompt: str = "",
                 max_retry: int | None = None, max_tokens: int | None = None,
                 dump_dir: Path | None = None) -> str:
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

            content = resp.choices[0].message.content
            finish_reason = resp.choices[0].finish_reason

            if content is None:
                out_tokens = resp.usage.completion_tokens if hasattr(resp, 'usage') and resp.usage else '.'
                print(f"  API returned None content (finish_reason={finish_reason}, "
                      f"out_tokens={out_tokens}, max_tokens={max_tokens})")
                # Dump full response to sample directory for post-analysis
                if dump_dir:
                    dump_dir.mkdir(parents=True, exist_ok=True)
                    dp = dump_dir / "none_content_response.json"
                    try:
                        data = resp.model_dump()
                    except Exception:
                        data = {"raw": str(resp)}
                    dp.write_text(json.dumps(data, ensure_ascii=False, indent=2, default=str), encoding="utf-8")
                    print(f"  Dumped raw response -> {dp}")
                raise NoneContentError(
                    f"API returned None content. finish_reason={finish_reason}, "
                    f"out_tokens={out_tokens}."
                )

            if finish_reason == "length":
                print(f"  WARNING: output truncated at max_tokens={max_tokens} "
                      f"(finish_reason=length)")

            if hasattr(resp, 'usage') and resp.usage:
                print(f"  API OK | in={resp.usage.prompt_tokens} "
                      f"out={resp.usage.completion_tokens} total={resp.usage.total_tokens}")

            return content

        except NoneContentError:
            raise  # Do not retry - same parameters will fail again
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
    "You are a Java code generation expert. "
    "Generate COMPLETE, compilable, and well-documented Java code "
    "based on domain descriptions, functional requirements, and design models. "
    "Output ONLY a single ```java code block. No explanations, no analysis, no commentary before or after the code block."
)

GENERATE_PROMPT = """
# Task:
Generate Java code based on the Domain Description, Functional Requirements, and Design Model below.
You must generate unparameterized constructors for each class.

# Hard constraints:
1. DO NOT include package declaration - start directly with import statements.
2. Every field MUST provide public getter and setter methods.
3. You must generate one unparameterized constructor for each class.

# Domain Description
{req_text}

# Functional Requirements
{cr_text}

# Design Model (Class Diagram)
{uml_text}

# Output
Output ONLY the code block below. Do NOT write any explanation, analysis, or commentary before or after it.
```java
<ALL generated Java classes in one file.>
```
"""

FIX_SYSTEM_PROMPT = (
    "You are a Java code debugging and fixing expert. "
    "Fix compilation errors while maintaining the original functionality."
)

FIX_PROMPT = """
# Task:
Based on the Compilation Error Messages, revise the Previous FJC.java Code.
Output the FULL, COMPLETE Java code. Do not omit any code, imports, or classes.
Do not use placeholders like `// ... existing code ...`.

# Previous FJC.java Code (with compilation errors):
```java
{fjc_code}
```

# Compilation Error Messages:
```
{compile_errors}
```

# Domain Description:
{req_text}

# Functional Requirements:
{cr_text}

# Output
```java
<Complete revised Java code. Must include all imports, classes, and methods.>
```
"""


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
    """Remove `public` before top-level class/enum/interface declarations."""
    lines = code.splitlines()
    cleaned = [re.sub(r"^\s*public(\s+(class|interface|enum))", r"\1", l) for l in lines]
    return "\n".join(cleaned)


def _scan_system_dirs(base_dir: Path) -> List[str]:
    """Auto-scan system directories under base_dir that contain REQ.md (excluding paXX-result)"""
    if not base_dir.exists():
        return []
    result = []
    for d in sorted(base_dir.iterdir()):
        if not d.is_dir():
            continue
        # Exclude paXX-result format directories
        if re.match(r"^pa\d+-result$", d.name):
            continue
        # Only process directories containing REQ.md
        if (d / REQ_FILE_NAME).exists():
            result.append(d.name)
    return result


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
        req_text = _extract_version_block(input_dir / REQ_FILE_NAME)
        cr_text = _extract_version_block(input_dir / CR_FILE_NAME)
        uml_text = _extract_version_block(input_dir / UML_FILE_NAME)

        prompt = GENERATE_PROMPT.format(
            req_text=req_text,
            cr_text=cr_text,
            uml_text=uml_text,
        )

        print(f"  [generate] calling LLM ({model})...")
        code = _call_openai(prompt, model, system_prompt=SYSTEM_PROMPT,
                            dump_dir=sample_src)

        # Dump raw response for diagnostics
        sample_src.mkdir(parents=True, exist_ok=True)
        raw_path = sample_src / "raw_response.txt"
        raw_path.write_text(code, encoding="utf-8")
        print(f"  [generate] raw response: {len(code)} chars, first 200: {code[:200]!r}")

        code = _strip_code_fence(code)
        code = _remove_public_modifiers(code)

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
    """Phase 3: Compilation failure -> LLM fix -> Recompile (loop up to max_attempts times)
    
    Fixed code is written to fjc_path, fix log is written to fix_info.txt at the same level as fjc_path.
    """
    req_text = _extract_version_block(input_dir / REQ_FILE_NAME)
    cr_text = _extract_version_block(input_dir / CR_FILE_NAME)
    fix_info_path = fjc_path.parent / "fix_info.txt"
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
            req_text=req_text,
            cr_text=cr_text,
        )

        try:
            fixed = _call_openai(prompt, model, system_prompt=FIX_SYSTEM_PROMPT)
            fixed = _strip_code_fence(fixed)
            fixed = _remove_public_modifiers(fixed)
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

    # Determine the list of systems to process
    if SYSTEM_DIRS:
        system_names = SYSTEM_DIRS
    else:
        system_names = _scan_system_dirs(INPUT_BASE_DIR)

    print(f"{'='*70}")
    print(f"  FJC-CodeGen REQ+CR+UML")
    print(f"{'='*70}")
    print(f"  Model:        {model}")
    print(f"  Input base:   {INPUT_BASE_DIR}")
    print(f"  Output base:  {SCRIPT_DIR}")
    print(f"  Systems:      {system_names}")
    print(f"  Samples:      {SAMPLES}")
    print(f"  JARs:         {len(jars)} found")
    print(f"  Phases:       {', '.join(PHASES)}")
    print(f"{'='*70}\n")

    if not system_names:
        print("[WARN] No system directories found. Exiting.")
        return

    # Save prompt snapshot
    snapshot = GENERATE_PROMPT.format(
        req_text="<will be filled from REQ.md>",
        cr_text="<will be filled from CR.md>",
        uml_text="<will be filled from UML_DESIGNED_level3.md>",
    )

    for system_name in system_names:
        input_dir = INPUT_BASE_DIR / system_name
        out_root = SCRIPT_DIR / system_name / output_name

        if not input_dir.exists():
            print(f"[SKIP] {system_name}: input dir not found ({input_dir})")
            continue

        print(f"\n{'#'*60}")
        print(f"Processing: {system_name}")
        print(f"  Input:  {input_dir}")
        print(f"  Output: {out_root}")
        print(f"{'#'*60}")

        # Save prompt snapshot to input directory
        (input_dir / PROMPT_SNAPSHOT_FILE).write_text(snapshot, encoding="utf-8")
        out_root.mkdir(parents=True, exist_ok=True)

        for i in range(1, SAMPLES + 1):
            sample_dir = out_root / f"sample{i}"

            # Only clean directory when generate phase is included
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

        for system_name in system_names:
            input_dir = INPUT_BASE_DIR / system_name
            src_root = SCRIPT_DIR / system_name / output_name
            fix_root = SCRIPT_DIR / system_name / fix_output_name

            if not src_root.exists():
                print(f"[SKIP] {system_name}: source dir not found ({src_root})")
                continue

            print(f"\n{'#'*60}")
            print(f"Fix Processing: {system_name}")
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
