# CQ4: task-level Pass@1 Confidence Intervals

## Method

- Confidence level: 0.95
- Distribution: Student's **t** (all cells have n < 30: n=10 for TLBench, n=4 for JavaBench).
- Standard error: SE = std(ddof=1) / sqrt(n)  (unbiased std).
- Implementation: `scipy.stats.t.interval(conf, df=n-1, loc=mean, scale=sem)`.
- Pass@1 is bounded in [0,100]%; negative lower bounds are clipped to 0 in the `reported` column (raw value kept for transparency).

## Detailed table (per cell)

| Method | Model | Benchmark | n | Mean% | Std% | SE% | CI lower (raw) | CI upper | CI lower (reported, clipped) |
|---|---|---|---:|---:|---:|---:|---:|---:|---:|
| EcoreGenTL | deepseek-v4-flash | TLBench | 10 | 58.0 | 42.6 | 13.48 | 27.5 | 88.5 | 27.5 |
| EcoreGenTL | deepseek-v4-flash | JavaBench | 4 | 15.0 | 19.1 | 9.57 | -15.5 | 45.5 | 0.0 |
| EcoreGenTL | minimax-m3 | TLBench | 10 | 56.0 | 37.5 | 11.85 | 29.2 | 82.8 | 29.2 |
| EcoreGenTL | minimax-m3 | JavaBench | 4 | 10.0 | 20.0 | 10.00 | -21.8 | 41.8 | 0.0 |
| OpenCode | deepseek-v4-flash | TLBench | 10 | 32.0 | 42.4 | 13.40 | 1.7 | 62.3 | 1.7 |
| OpenCode | deepseek-v4-flash | JavaBench | 4 | 5.0 | 10.0 | 5.00 | -10.9 | 20.9 | 0.0 |
| OpenCode | minimax-m3 | TLBench | 10 | 30.0 | 48.3 | 15.28 | -4.6 | 64.6 | 0.0 |
| OpenCode | minimax-m3 | JavaBench | 4 | 0.0 | 0.0 | 0.00 | 0.0 | 0.0 | 0.0 |
| SWE-Agent-Mini | deepseek-v4-flash | TLBench | 10 | 32.0 | 44.4 | 14.05 | 0.2 | 63.8 | 0.2 |
| SWE-Agent-Mini | deepseek-v4-flash | JavaBench | 4 | 0.0 | 0.0 | 0.00 | 0.0 | 0.0 | 0.0 |
| SWE-Agent-Mini | minimax-m3 | TLBench | 10 | 28.0 | 41.3 | 13.06 | -1.6 | 57.6 | 0.0 |
| SWE-Agent-Mini | minimax-m3 | JavaBench | 4 | 0.0 | 0.0 | 0.00 | 0.0 | 0.0 | 0.0 |

## Advisor-template layout (DS+JavaBench, MM+JavaBench, DS+TLBench, MM+TLBench)

Cells below use the **reported** lower bound (negative values clipped to 0).

- EcoreGenTL: 0-45%, 0-42%, 28-88%, 29-83%
- OpenCode: 0-21%, 0% (n=4, all samples failed), 2-62%, 0-65%
- SWE-Agent-Mini: 0% (n=4, all samples failed), 0% (n=4, all samples failed), 0-64%, 0-58%

## Inputs (archived in CQ4_task-level-CI.csv)

Per-task Pass@1 (%) in advisor-template order. JavaBench not in pass@k.csv; values are #fully-passing-samples/5 from RQ1 raw.

- EcoreGenTL | deepseek-v4-flash | JavaBench: 40, 0, 20, 0   (rows: RQ1 raw)
- EcoreGenTL | minimax-m3 | JavaBench: 40, 0, 0, 0   (rows: RQ1 raw)
- EcoreGenTL | deepseek-v4-flash | TLBench: 20, 100, 80, 0, 40, 100, 100, 100, 0, 40   (rows: 102,103,104,105,110,106,107,108,109,111)
- EcoreGenTL | minimax-m3 | TLBench: 0, 80, 100, 20, 60, 80, 100, 80, 20, 20   (rows: 112,113,114,115,120,116,117,118,119,121)
- OpenCode | deepseek-v4-flash | JavaBench: 20, 0, 0, 0   (rows: RQ1 raw)
- OpenCode | minimax-m3 | JavaBench: 0, 0, 0, 0   (rows: RQ1 raw)
- OpenCode | deepseek-v4-flash | TLBench: 0, 60, 0, 0, 100, 0, 80, 80, 0, 0   (rows: 152,157,162,167,172,177,182,187,192,197)
- OpenCode | minimax-m3 | TLBench: 0, 0, 0, 0, 100, 0, 100, 100, 0, 0   (rows: 153,158,163,168,173,178,183,188,193,198)
- SWE-Agent-Mini | deepseek-v4-flash | JavaBench: 0, 0, 0, 0   (rows: RQ1 raw)
- SWE-Agent-Mini | minimax-m3 | JavaBench: 0, 0, 0, 0   (rows: RQ1 raw)
- SWE-Agent-Mini | deepseek-v4-flash | TLBench: 0, 40, 0, 0, 100, 0, 100, 80, 0, 0   (rows: 202,206,210,214,218,222,226,230,234,238)
- SWE-Agent-Mini | minimax-m3 | TLBench: 0, 20, 0, 0, 80, 0, 100, 80, 0, 0   (rows: 203,207,211,215,219,223,227,231,235,239)



## 解释
我们使用的是 无偏估计,
脚本 用的是t分布