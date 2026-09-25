# Task-level Pass@k Confidence Intervals

## 统计口径

- 统计单位：benchmark 中的独立 task/problem，而不是单个测试、方法或生成样本。
- 条件：每个 `benchmark × method × model` 单独计算。
- 点估计：各 task 的 project Pass@1/Pass@3 的算术平均。
- 区间：对 task 进行有放回重采样，计算均值的 95% percentile bootstrap CI。
- 重采样：100,000 次，固定随机种子 955；JavaBench 只有 4 个 task，因此使用全部 `4^4=256` 种 bootstrap 重采样组合。
- 样本量：TLBench 为 10 个 task；JavaBench 为 4 个 task。

该区间刻画跨 task 的不确定性。它不把同一 task 的 5 个生成样本当成独立 task，也不额外估计 task 内的生成随机性。JavaBench 只有 4 个 task，其区间很宽，只能视为探索性结果。

## 数据校验

- TLBench CSV 删除了 3 个空白行后，共有 250 条有效记录，即 5 methods × 5 models × 10 tasks；不存在重复的 method-model-task 键或缺失指标。
- JavaBench 共整理 100 条记录，即 5 methods × 5 models × 4 tasks。
- VL-Fix 模型名中的 `-fix` 后缀仅表示方法条件，计算时已去除，以便和其他方法按同一基座模型比较。


## TLBench：均值与 95% CI

| Method | Model | Mean Pass@1 [95% CI] | Mean Pass@3 [95% CI] |
|---|---|---:|---:|
| EcoreGen | deepseek-v4-flash | 58.0% [32.0%, 82.0%] | 74.0% [48.0%, 96.0%] |
| EcoreGen | minimax-m3 | 56.0% [34.0%, 78.0%] | 78.0% [56.0%, 96.0%] |
| EcoreGen | qwen3.6-flash | 44.0% [22.0%, 66.0%] | 65.0% [36.0%, 90.0%] |
| EcoreGen | gemini-3.1-flash-lite | 56.0% [32.0%, 88.0%] | 65.0% [36.0%, 90.0%] |
| EcoreGen | gpt-5.4-mini | 66.0% [38.0%, 92.0%] | 75.0% [48.0%, 97.0%] |
| OpenCode | deepseek-v4-flash | 32.0% [8.0%, 58.0%] | 40.0% [10.0%, 70.0%] |
| OpenCode | minimax-m3 | 30.0% [0.0%, 60.0%] | 30.0% [0.0%, 60.0%] |
| OpenCode | qwen3.6-flash | 18.0% [0.0%, 38.0%] | 29.0% [0.0%, 58.0%] |
| OpenCode | gemini-3.1-flash-lite | 14.0% [0.0%, 32.0%] | 25.0% [0.0%, 51.0%] |
| OpenCode | gpt-5.4-mini | 26.0% [4.0%, 52.0%] | 35.0% [9.0%, 64.0%] |
| Mini-SWE-Agent | deepseek-v4-flash | 32.0% [8.0%, 60.0%] | 39.0% [10.0%, 69.0%] |
| Mini-SWE-Agent | minimax-m3 | 28.0% [6.0%, 54.0%] | 36.0% [10.0%, 66.0%] |
| Mini-SWE-Agent | qwen3.6-flash | 20.0% [0.0%, 44.0%] | 26.0% [0.0%, 52.0%] |
| Mini-SWE-Agent | gemini-3.1-flash-lite | 20.0% [0.0%, 50.0%] | 20.0% [0.0%, 50.0%] |
| Mini-SWE-Agent | gpt-5.4-mini | 24.0% [0.0%, 48.0%] | 30.0% [0.0%, 60.0%] |
| VL-Fix | deepseek-v4-flash | 34.0% [10.0%, 60.0%] | 45.0% [16.0%, 74.0%] |
| VL-Fix | minimax-m3 | 26.0% [4.0%, 52.0%] | 35.0% [9.0%, 64.0%] |
| VL-Fix | qwen3.6-flash | 16.0% [0.0%, 38.0%] | 25.0% [0.0%, 51.0%] |
| VL-Fix | gemini-3.1-flash-lite | 22.0% [0.0%, 50.0%] | 26.0% [0.0%, 52.0%] |
| VL-Fix | gpt-5.4-mini | 22.0% [4.0%, 44.0%] | 37.0% [9.0%, 65.0%] |
| VL | deepseek-v4-flash | 34.0% [10.0%, 60.0%] | 45.0% [16.0%, 74.0%] |
| VL | minimax-m3 | 26.0% [4.0%, 52.0%] | 35.0% [9.0%, 64.0%] |
| VL | qwen3.6-flash | 16.0% [0.0%, 38.0%] | 25.0% [0.0%, 51.0%] |
| VL | gemini-3.1-flash-lite | 22.0% [0.0%, 50.0%] | 26.0% [0.0%, 52.0%] |
| VL | gpt-5.4-mini | 22.0% [4.0%, 44.0%] | 37.0% [9.0%, 65.0%] |

## JavaBench：均值与 95% CI

| Method | Model | Mean Pass@1 [95% CI] | Mean Pass@3 [95% CI] |
|---|---|---:|---:|
| EcoreGen | deepseek-v4-flash | 15.0% [0.0%, 30.0%] | 37.5% [0.0%, 75.0%] |
| EcoreGen | minimax-m3 | 10.0% [0.0%, 30.0%] | 22.5% [0.0%, 67.5%] |
| EcoreGen | qwen3.6-flash | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| EcoreGen | gemini-3.1-flash-lite | 5.0% [0.0%, 15.0%] | 15.0% [0.0%, 45.0%] |
| EcoreGen | gpt-5.4-mini | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| OpenCode | deepseek-v4-flash | 5.0% [0.0%, 15.0%] | 15.0% [0.0%, 45.0%] |
| OpenCode | minimax-m3 | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| OpenCode | qwen3.6-flash | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| OpenCode | gemini-3.1-flash-lite | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| OpenCode | gpt-5.4-mini | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| Mini-SWE-Agent | deepseek-v4-flash | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| Mini-SWE-Agent | minimax-m3 | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| Mini-SWE-Agent | qwen3.6-flash | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| Mini-SWE-Agent | gemini-3.1-flash-lite | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| Mini-SWE-Agent | gpt-5.4-mini | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| VL-Fix | deepseek-v4-flash | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| VL-Fix | minimax-m3 | 5.0% [0.0%, 15.0%] | 15.0% [0.0%, 45.0%] |
| VL-Fix | qwen3.6-flash | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| VL-Fix | gemini-3.1-flash-lite | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| VL-Fix | gpt-5.4-mini | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| VL | deepseek-v4-flash | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| VL | minimax-m3 | 5.0% [0.0%, 15.0%] | 15.0% [0.0%, 45.0%] |
| VL | qwen3.6-flash | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| VL | gemini-3.1-flash-lite | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |
| VL | gpt-5.4-mini | 0.0% [0.0%, 0.0%] | 0.0% [0.0%, 0.0%] |

## EcoreGenTL 与最强 Baseline 的配对 Pass@1 差值

最强 baseline 在每个 benchmark/model 内按观察到的平均 Pass@1 选择。随后按相同 task 计算 `EcoreGenTL - baseline`，并对 task 差值做 bootstrap。

| Benchmark | Model | Strongest baseline | Mean difference [95% CI] |
|---|---|---|---:|
| TLBench | deepseek-v4-flash | VL-Fix | +24.0 pp [+6.0, +48.0] |
| TLBench | minimax-m3 | OpenCode | +26.0 pp [+0.0, +54.0] |
| TLBench | qwen3.6-flash | Mini-SWE-Agent | +24.0 pp [+4.0, +44.0] |
| TLBench | gemini-3.1-flash-lite | VL-Fix | +38.0 pp [+12.0, +66.0] |
| TLBench | gpt-5.4-mini | OpenCode | +40.0 pp [+16.0, +66.0] |
| JavaBench | deepseek-v4-flash | OpenCode | +10.0 pp [+0.0, +20.0] |
| JavaBench | minimax-m3 | VL-Fix | +5.0 pp [+0.0, +15.0] |
| JavaBench | qwen3.6-flash | VL-Fix | +0.0 pp [+0.0, +0.0] |
| JavaBench | gemini-3.1-flash-lite | VL-Fix | +5.0 pp [+0.0, +15.0] |
| JavaBench | gpt-5.4-mini | VL-Fix | +0.0 pp [+0.0, +0.0] |

## 解释

1. TLBench 上，EcoreGenTL 的平均 project Pass@1 为 44%–66%，对应 task-level 95% CI 如上表。相对每个模型的最强 baseline，平均增益为 24–40 个百分点；5 个模型中有 4 个差值区间严格高于 0，MiniMax 的下界为 0。
2. JavaBench 上，EcoreGenTL 的平均 project Pass@1 为 0%–15%。由于只有 4 个 task，区间较宽，所有配对差值 CI 都包含 0，不能据此声称 task-level 统计差异已经确定。
3. 这些结果适合用于回应“请报告 task-level confidence intervals”，但不应把 CI 与显著性检验混为一谈，也不应把方法级 Pass% 当成 project Pass@1。

## Rebuttal 可用表述

我们以 task 为重采样单位，为每个 model-method 条件计算了平均 project Pass@1 的 95% percentile-bootstrap CI。TLBench 上，EcoreGenTL 的平均 Pass@1 为 44%–66%；与各模型最强 baseline 的配对增益为 24–40 pp，其中 4/5 个模型的 95% CI 严格高于 0。JavaBench 仅含 4 个复杂项目，EcoreGenTL 的均值为 0%–15%，区间较宽且配对增益区间包含 0；我们将透明报告这一不确定性，而不以方法级 Pass% 替代项目级成功率。
