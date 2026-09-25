# Standard EMF Template Sanity Check

## 结论摘要

仅使用 standard EMF template 时，JavaBench 与 TLBench 的 14 个任务均能生成可编译代码，但没有任何任务通过全部测试：

| Benchmark | Compiled tasks | Fully passed tasks | Pass@1 | Macro Pass% | Micro Pass% |
|---|---:|---:|---:|---:|---:|
| JavaBench | 4/4 | 0/4 | 0% | 10.23% | 48/308 = 15.58% |
| TLBench | 10/10 | 0/10 | 0% | 0.40% | 1/270 = 0.37% |

结果表明：EMF 模板可以可靠生成结构正确、可编译的代码骨架，但不能独立实现 benchmark 要求的业务行为。部分单元测试通过主要来自 EMF 自动生成的字段、构造器、Factory、getter/setter、枚举和反射基础设施，而非业务 operation 的实现。

## 实验设置

流程如下：

```text
Ecore
-> 删除全部 EOperation body（包括 generated=manual）
-> 使用 org.eclipse.emf.codegen.ecore.generator.Generator
-> 生成 EMF Java 模型骨架
-> 添加仅用于编译的 API-only scaffold
-> 编译功能代码与原始 JUnit 测试
-> 运行测试
```

关键配置与控制：

- Java 11，`basePackage=edu`，`suppressInterfaces=true`；
- 未实现的 EOperation 统一生成为 `UnsupportedOperationException` stub；
- 不调用 LLM、Spring AI、MWE2、代码补全或修复环节；
- 每个任务只生成一份确定性输出；
- 原测试代码、JUnit 与依赖 Jar 保持不变；
- 兼容处理只修正 class-only 布局、错误的 `toString` 签名，并为 PA20/PA21 添加缺失的 API 类型声明；这些声明不含业务实现；
- 日志确认 JavaBench 所有 EOperation 均被清空，`preservedRealBodies=0`。

## JavaBench 明细

| Task | Compile | Passed tests | Total tests | Pass% | Full-task pass |
|---|---:|---:|---:|---:|---:|
| PA19 | Yes | 3 | 50 | 6.00% | No |
| PA20 | Yes | 0 | 45 | 0.00% | No |
| PA21 | Yes | 40 | 145 | 27.59% | No |
| PA22 | Yes | 5 | 68 | 7.35% | No |

PA21 的较高部分通过率主要来自结构、Factory、getter/setter、枚举与 reflection sanity tests。所有业务 EOperation body 均已删除。

## 确定性生成器与 @3

Standard EMF template 是确定性生成器，没有三个独立随机样本，因此正式结果应优先报告 `Compile@1=100%`、`Pass@1=0%`，并将 `Compile@3`、`Pass@3` 标为 `N/A`。

若表格必须沿用 @3 列，可明确标注为“同一确定性过程重复三次”。此时三次输出完全相同，`Compile@3=100%`、`Pass@3=0%`，但不能解释为三个独立样本。

## Rebuttal 可用表述

纯 EMF 模板只实现结构性代码，不实现 operation 的业务逻辑。快速 sanity check 显示，JavaBench 和 TLBench 的 14 个任务均可编译，但没有任何任务通过全部测试（两个 benchmark 的 Pass@1 均为 0%）。少量通过的单元测试主要检查 EMF 自动生成的结构性基础设施。这说明模板保证结构正确性，而 LLM completion 对业务语义不可替代。

## 原始结果路径（内部复核）

以下为实验仓库中的相对路径，仅用于内部复核。Rebuttal 中只需在 AQ2 回答审稿人的具体问题并报告关键数字，不需要上传这些新增结果文件：

- `script/test/reports/javabench-strict-skeleton/summary.csv`
- `script/test/reports/javabench-strict-skeleton/summary@passk.csv`
- `script/test/reports/tlbench-strict-skeleton/OurBench.csv`
- `script/test/reports/tlbench-strict-skeleton/OurBench_pass@k.csv`
- `ouput/javabench-emf-strict-skeleton-only-summary.json`
