# CQ4 task-level Pass@1 CI -- SciPy textbook method (t-distribution for n<30)
# Input : CQ4_task-level-CI.csv (12 cells, per-task Pass@1 in percent)
# Method: scipy.stats.t.interval(confidence=0.95, df=n-1, loc=mean, scale=sem)
#         sem = std(ddof=1)/sqrt(n)   (unbiased std -> unbiased SE)
#         n=10 (TLBench) and n=4 (JavaBench) are both < 30 -> t-distribution
# Output: console + markdown report (CQ4_task-level-CI_report.md)
# NOTE: Pass@1 in [0,1]; negative CI lower bounds are clipped to 0 in the "reported" column.
import csv, os
import numpy as np
from scipy import stats

CONF = 0.95
HERE = os.path.dirname(os.path.abspath(__file__))
CSV_IN  = os.path.join(HERE, "CQ4_task-level-CI.csv")
MD_OUT  = os.path.join(HERE, "CQ4_task-level-CI_report.md")

METHODS = ["EcoreGenTL", "OpenCode", "SWE-Agent-Mini"]
MODELS  = ["deepseek-v4-flash", "minimax-m3"]
# advisor template column order: DS+JavaBench, MM+JavaBench, DS+TLBench, MM+TLBench
ORDER = [("deepseek-v4-flash", "JavaBench"),
         ("minimax-m3",        "JavaBench"),
         ("deepseek-v4-flash", "TLBench"),
         ("minimax-m3",        "TLBench")]

# read archive
cells = {}
with open(CSV_IN, encoding="utf-8-sig") as f:
    for r in csv.DictReader(f):
        vals = [float(r[f"p{i}"]) for i in range(1, 11) if r[f"p{i}"].strip() != ""]
        cells[(r["method"], r["model"], r["benchmark"])] = {
            "n": int(r["n"]),
            "vals": vals,
            "rows": r["csv_rows"],
            "source": r["source"],
        }

def ci(vals, conf=CONF):
    n = len(vals)
    mean = float(np.mean(vals))
    if n < 2:
        s = 0.0
    else:
        s = float(np.std(vals, ddof=1))
    sem = s / np.sqrt(n) if n > 0 else 0.0
    # n>=30 -> normal; n<30 -> t  (all our cells are <30, so t)
    # All-zero edge case: std=0 -> sem=0 -> interval degenerates to [mean, mean]
    if s == 0.0:
        lo = hi = mean
        dist = "t (degenerate, std=0)"
    elif n >= 30:
        lo, hi = stats.norm.interval(conf, loc=mean, scale=sem)
        dist = "normal"
    else:
        lo, hi = stats.t.interval(conf, df=n-1, loc=mean, scale=sem)
        dist = "t"
    return dict(n=n, mean=mean, std=s, sem=sem, lo=lo, hi=hi, dist=dist)

results = {k: ci(v["vals"]) for k, v in cells.items()}

# ---- console ----
print(f"{'method':15s} {'model':22s} {'bench':9s} {'n':>3} {'mean':>7} {'std':>7} {'sem':>7} {'lo':>8} {'hi':>8} {'clip_lo':>8} dist")
for method in METHODS:
    for model in MODELS:
        for bench in ["TLBench", "JavaBench"]:
            k = (method, model, bench)
            r = results[k]; r2 = cells[k]
            print(f"{method:15s} {model:22s} {bench:9s} {r['n']:3d} {r['mean']:7.1f} {r['std']:7.1f} {r['sem']:7.2f} "
                  f"{r['lo']:8.1f} {r['hi']:8.1f} {max(0.0,r['lo']):8.1f} {r['dist']}")

# ---- markdown report ----
L = []
L.append("# CQ4: task-level Pass@1 Confidence Intervals\n")
L.append("## Method\n")
L.append(f"- Confidence level: {CONF:.2f}")
L.append("- Distribution: Student's **t** (all cells have n < 30: n=10 for TLBench, n=4 for JavaBench).")
L.append("- Standard error: SE = std(ddof=1) / sqrt(n)  (unbiased std).")
L.append("- Implementation: `scipy.stats.t.interval(conf, df=n-1, loc=mean, scale=sem)`.")
L.append("- Pass@1 is bounded in [0,100]%; negative lower bounds are clipped to 0 in the `reported` column (raw value kept for transparency).\n")

L.append("## Detailed table (per cell)\n")
L.append("| Method | Model | Benchmark | n | Mean% | Std% | SE% | CI lower (raw) | CI upper | CI lower (reported, clipped) |")
L.append("|---|---|---|---:|---:|---:|---:|---:|---:|---:|")
for method in METHODS:
    for model in MODELS:
        for bench in ["TLBench", "JavaBench"]:
            k = (method, model, bench)
            r = results[k]; r2 = cells[k]
            L.append(f"| {method} | {model} | {bench} | {r['n']} | {r['mean']:.1f} | {r['std']:.1f} | {r['sem']:.2f} | "
                     f"{r['lo']:.1f} | {r['hi']:.1f} | {max(0.0,r['lo']):.1f} |")

L.append("\n## Advisor-template layout (DS+JavaBench, MM+JavaBench, DS+TLBench, MM+TLBench)\n")
L.append("Cells below use the **reported** lower bound (negative values clipped to 0).\n")
for method in METHODS:
    parts = []
    for model, bench in ORDER:
        r = results[(method, model, bench)]
        lo = max(0.0, r['lo'])
        if r['std'] == 0.0:
            # all-zero cell: degenerate; report "0% (n={n}, all samples failed)"
            parts.append(f"0% (n={r['n']}, all samples failed)")
        else:
            parts.append(f"{lo:.0f}-{r['hi']:.0f}%")
    L.append(f"- {method}: " + ", ".join(parts))

L.append("\n## Inputs (archived in CQ4_task-level-CI.csv)\n")
L.append("Per-task Pass@1 (%) in advisor-template order. JavaBench not in pass@k.csv; values are #fully-passing-samples/5 from RQ1 raw.\n")
for method in METHODS:
    for model, bench in ORDER:
        v = cells[(method, model, bench)]
        L.append(f"- {method} | {model} | {bench}: " + ", ".join(str(int(x)) for x in v['vals']) +
                 f"   (rows: {v['rows'] if v['rows'].strip() else 'RQ1 raw'})")

with open(MD_OUT, "w", encoding="utf-8") as f:
    f.write("\n".join(L))
print("\nsaved", MD_OUT)
