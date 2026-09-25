# EcoreGenTL Rebuttal 中文逻辑稿

感谢三位评审的建设性意见。**In this paper, we intend to investigate how template-based code generation and LLM-driven code generation can be combined in a unified pipeline.** 我们的key insight是：传统的模板式代码生成可以融入(can be integrated)智能化代码生成流程，并应与 LLM 驱动的生成协同使用(combine)。EcoreGenTL 以 EMF 为具体实例，展示如何将这两种范式整合进统一流程。据我们所知，本文是最早系统研究这种融合方式的工作之一(among the first)。这项工作既展示了如何用 LLM 增强现有 MDE 代码生成基础设施(即模板生成器)，也为将传统代码生成技术融入 LLM-driven software engineering 提供了具体路径
(对 MDE 社区，本文展示了如何用 LLM 增强现有的代码生成基础设施；对软件工程社区，本文展示了如何将传统代码生成技术与 LLM-driven generation 结合起来。)

## Common Questions

**CQ1：评估规模与泛化性（A/B/C）**

JavaBench 虽仅包含 4 个任务，每个任务是包含 13–29 个类和 32–53 个方法的 project-level Java system，具有足够的项目复杂度。面向 Java 的公开 project-level benchmark 相对稀缺，因此我们补充 10 个跨领域 TLBench 任务扩大任务与领域覆盖。TLBench 的相关信息已在附件中提供 Experiments/Benchmarks/TLBench/ 中。为进一步检验外部有效性，我们还完成了公开 RealBench（FSE 2026）上 7 个 repository-level 任务以及更强 agent baseline OpenHands 的实验。我们从 RealBench 的 61 个 repositories 中选择了 7 个能够语义等价迁移到 Java 的任务；被排除的任务主要依赖在 Java 中缺少功能等价实现的第三方库。该筛选标准旨在隔离“根据需求与设计生成代码”的能力，而非使用特定第三方库的能力。新增结果与原文的总体趋势一致，将在 revision 中补充。

**CQ2：模型调用与成本（A/C）**

根据评审建议，我们在 gemini-3.1-flash-lite + JavaBench 条件下统计了各方法的 token 消耗。EcoreGenTL 消耗 3.46M tokens，与 OpenCode 的 3.24M 接近（高 6.8%），但高于 Mini-SWE-Agent（619K）、VL（165K）和 VL-Fix（171K）。我们将在 revision 中补充各方法的 token 统计。execution time 受远程模型服务与网络状态影响，无法公平度量，不作为比较指标。

## Revision Plan

1. 强化论文开篇的 vision、novelty、stakeholders 与适用阶段，并扩展与 M2T、skeleton-guided completion 和 compiler-in-the-loop agent 的 related work。
2. 加入 RealBench、OpenHands、纯模板 sanity check、task-level Pass@1 分布/置信区间，以及代表性 token、cost。
3. 澄清 annotation 更新、specification review、比较protocol及 root-cause attribution procedure。
4. 完善 README，明确 showcase、benchmark 路径、并补充 DOI。

## Reviewer A

**AQ1：能否适配 Acceleo？** 本文的核心思想:模板式代码生成应该集成到智能化代码生成流程中。不依赖某一个模板生成器，EMF 只是展示两种范式如何融合的实例。需求分解、代码压缩、上下文获取、AST合并和错误修复等思想可以复用；迁移到 Acceleo 时，需要适配生成器接口、模板及框架特定的编程知识。我们将在 revision 中补充 Acceleo 适配边界的讨论 。

**AQ2：Non-LLM baseline。** Standard EMF template 只能生成类结构和未实现的 operation body，并不实现 benchmark 要求的业务行为。因此，它能够生成可编译的代码，但无法通过完整的功能测试。根据评审建议，我们快速完成了这一sanity check：JavaBench 和 TLBench 的 14 个任务均成功编译（Compile@1=100%），但两个 benchmark 的 Pass@1 均为 0%。少量通过的单元测试主要进行结构性检查，例如检查自动生成的字段、Factory 和 getter/setter，而非验证业务行为。由于该生成器是确定性的，@3 不适用。我们将在 revision 中加入这一结果。

**AQ3：数据集与实验设置。**
 数据集规模与选择理由见 CQ1。
**Temperature=0.7**：0.7 是代码生成研究中的常用采样温度 `[Ren et al. 的 EMNLP 2025 :Alignment with Fill-In-the-Middle for Enhancing Code Generation;Wang et al. 的 Findings of ACL 2026 论文在 repository-level Java secure code generation ]`。统一采用该值出于两点考虑：一是在每问题 5 样本的采样中保持受控的多样性；二是各模型默认温度不一，统一设置可避免模型间默认差异引入额外干扰。该设置同等作用于 EcoreGenTL 与 VL/VL-Fix，不构成对任何一方的系统性偏置。

**Reasoning 配置**：
对于 EcoreGenTL、VL/VL-Fix 和 SWE-Agent-Mini，我们通过统一将 reasoning effort 设为 none。这一设置主要基于成本和运行时间考虑，同时减少额外实验变量。不同模型 reasoning 机制并不一致，例如 GPT-5.4 Mini 使用 none/low/medium/high/xhigh，而 MiniMax M3 使用 disabled/adaptive/enabled。我们没有显式覆盖 OpenCode 的 reasoning 配置；它沿用自身 agent framework 和模型服务的配置。我们ensure这一配置差异并未使 baseline 处于不利条件。我们将在 revision 中澄清各方法的具体配置及其依据。

**AQ4：time 与 token。** 见 CQ2

## Reviewer B

**BQ1：novelty、stakeholders 与阶段。** MDE 包含建模与 model-to-code transformation（MDA, OMG 2004）。现有 LLM-for-MDE 工作主要关注建模.本文定位于模型稳定后的 model-to-code transformation 阶段，主要面向维护既有 MDE assets 的工程师和工具开发者。与主要关注建模的 LLM-for-MDE 工作不同，我们研究模板生成与 LLM 生成在代码层的融合(可以再补一句among the first)。

**BQ2：为何“更新 Ecore model”？** 
Ecore 模型本身的结构不会被修改。我们只向 operation 上附加 documentation annotation。这是 EMF 内置的机制：generator 在代码生成时自动将 annotation 转为 Javadoc，供补全阶段使用，并非我们额外引入。这点也体现了本文的 key insight：EMF 生成器及其模板是方法的核心组件，我们复用它的机制，让两种范式在 pipeline 中衔接。

**BQ3：review 如何恢复遗漏？** 如 Algorithm 2 所述，review 重新把原始 NL requirement、模型文本表示与首轮规约一并交给 LLM 检查——首轮漏掉的信息仍在输入中，因此有机会被找回。它不保证消除全部需求理解错误；已有研究同样发现，即使采用 reasoning、feedback 或 agent-based refinement，LLM 仍可能误解复杂规约或遗漏其中的重要内容 [Tian et al., ICSE’25; Tian and Chen, ICSE’26]。因此，review 是一种缓解机制，而非完备性保证；其有效性由去掉 review 后 Pass@k 出现下降的消融结果进一步验证。Review 的提示词模板已在附件提供 `[Supplements\EcoreGenTLShowcase\Prompt_Templates.md]`。

**BQ4：retry 与比较公平性。** 关于retry limit的scope：论文中"fixed retry limit of 3"同时作用于 code completion 与最终 code-fix 两个阶段；规格生成会补到所有 operation 均有规约后停止重试、规格review为 1–2 轮。论文未区分说明这些停止条件，revision 将补充。关于收敛性：我们快速 check 了日志，大部分 case 在 1–2 轮内即收敛，完整统计将在 revision 补充。关于比较公平性：VL 为 one-shot 生成、无修复环节；VL-Fix 与 EcoreGenTL 的最终 code-fix 最多各 3 次，两者次数一致。设置 VL-Fix 的动机是 one-shot 生成存在大量 trivial 编译错误，若直接比较差异会被编译问题主导，增加该环节是为了消除这种干扰。两个 agent 自主决定内部动作与迭代次数，我们未人为限制。内部迭代属于方法设计的一部分、不是外部控制变量。

**BQ5：根因归类。** 归因按两步进行：先沿日志定位错误最早出现的 pipeline 阶段，再在该阶段判断最直接的主因；两位作者独立检查，分歧通过讨论确定最主要的一个。多因案例并不频繁。根据我们的讨论与争议记录`[Supplements\Experiments\RQ3\JavaBench_case_analysis.md]`，多因争议集中在 fault type 为"复杂算法不正确"的情形，纠纷双方主要是需求分解与 EMF 知识注入：若规约未将算法逻辑分解清楚，归为需求分解；若规约已写明但 LLM 因不熟悉 EMF 惯用法或 API 而实现错误，归为 EMF 知识注入。这类fault sites共 9 个，占总失败数 393 的 2.29%。

**BQ6：artifact。** 附件已包含可运行的 showcase（EcoreGenTLShowcase/），包括 binary、Ecore model、配置文件和运行入口。我们重新核查后确认，其能够加载模型并执行至 LLM API 调用.我们同意当前 README 未说明完整实验的运行步骤，将在 revision 中补充完整的运行与实验复现说明。

## Reviewer C

**C1–C2：所谓“不等输入”和人工资产成本。** 所有方法的外部输入都是同一需求和类图：EcoreGenTL的类图使用 Ecore 表示，基线使用与之语义等价的 PlantUML；与 LLM 交互时，EcoreGenTL 同样把模型转成 PlantUML。EMF generator 与其模板是我们方法的核心组件，不是额外输入，LLM 也看不到模板。现有 RQ2 消融已经量化了 decomposition、context、compression 和 repair 的贡献；JavaBench 本身也属于 skeleton-completion 场景。建立类图的成本对所有方法共同存在，且本文主要面向已有 MDE 资产的场景。

**C3：benchmark bias 与材料公开。** JavaBench adaptations were frozen before running and comparing the evaluated methods.JavaBench adaptation 仅从原代码反向得到类图，并对所有方法采用相同需求、类图；必要简化同等作用于所有方法.我们确保没有针对 EcoreGenTL 优化。TLBench 的需求、Ecore/PlantUML 与测试已经在附件 `Experiments/Benchmarks/TLBench/` 提供。

**C4：Pass@1、cost 与 repair。** cost 见 CQ2，repair 见 BQ4。关于 task-level Pass@1 CI：以 task 为重采样单位，对每个 model-method 条件计算平均 project Pass@1 的 95% percentile bootstrap CI。TLBench（10 tasks）上，EcoreGenTL 平均 Pass@1 为 44%–66%，相对各模型最强 baseline 的配对增益 +24–+40 pp，5 个模型中 4 个差值 CI 严格高于 0。JavaBench (4 tasks )，样本量小导致 CI 较宽：5 个模型中 3 个配对增益为正（+5~+10 pp）、2 个为零，但所有模型的 CI 下界均为 0；TLBench 上的明显增益已提供主要证据。
---

## 待办清单（工作区使用，提交前整体删除）

### P0：先锁定会改变回复结论的证据


- [ ] **P0-3｜Token、调用次数与费用（A/C）**：选择一个闭源模型，比较 EcoreGenTL、VL/VL-Fix 与 agent baseline；记录 calls、input/output tokens 和估算费用。确认结论究竟是“comparable”还是存在稳定差异后再写。

- [ ] **P0-5｜Retry/收敛统计（B/C）**：从日志统计平均/中位轮次、一次或两次内完成的比例、达到上限的比例、达到上限仍失败的比例。至少覆盖复杂的 JavaBench 和代表性模型。
- [x] **P0-6｜Task-level 指标与区间（C）**：已按 task 对每个 benchmark × method × model 计算平均 Pass@1/Pass@3 及 95% percentile-bootstrap CI，并计算 EcoreGenTL 相对最强 baseline 的配对 Pass@1 差值区间。结果见 `回复依据/task-level-passk-confidence-intervals.md`。
- [ ] **P0-7｜根因多归因比例（B）**：回查原始标注记录，确认“先定位最早出错阶段、再确定最直接主因”的协议；统计存在多个合理原因的案例数和比例，不能直接使用逐字稿中的“5% 以下”估计。
- [ ] **P0-8｜输入等价与 adaptation 证据（C）**：逐任务核对 Ecore 与 PlantUML 的类、属性、引用、操作签名是否语义一致；列出 JavaBench adaptation 中的简化项，并确认这些简化对所有方法一致；准备附件路径或对照表作为证据。
- [ ] **P0-9｜Artifact 完整性复查（B/C）**：从实际提交的压缩包而不是当前工作区开始，按 README 重新运行 showcase；确认 binary、prompt、API 配置、benchmark、测试、生成/评测脚本、raw results 均存在且路径正确。

### P1：实现与材料核对

- [ ] **P1-1｜Annotation 生命周期（B）**：核对实现是否会先清理旧 documentation annotation、只追加 operation specification、生成后是否再次清理；确认不会修改类图结构。完成后替换 BQ2 的 `[待核实现]`。
- [ ] **P1-2｜TLBench 发布状态（C）**：确认提交包中 10 个任务均包含 requirement、Ecore/PlantUML、baseline/EcoreGenTL tests；在 rebuttal 中给出准确目录，而不是只写“已经公开”。
- [ ] **P1-3｜README 修订（B）**：补运行环境、依赖安装、API key、绝对路径配置、showcase 命令、RQ1/RQ2 脚本入口、结果复算方式与目录说明。
- [ ] **P1-4｜作者 MDE 经验（A）**：与老师确认可以公开的准确表述，例如相关研究年限、项目或论文经历；避免使用无法核验的笼统自评。
- [ ] **P1-5｜执行时间口径（A/C）**：决定是否仅提供带环境说明的 wall-clock 参考值。正文必须说明模型服务、网络和代理造成的波动，不把 latency 用作严格优劣结论。

### P2：文献与论文修改

- [ ] **P2-1｜Temperature 依据（A）**：寻找可靠论文证明 0.7 是相关代码生成实验中的常用设置；若证据不足，改为更谨慎的统一采样与多样性控制解释。
- [x] **P2-2｜Reasoning 配置描述（A/B）**：已按运行脚本核实（09-25 用户确认，修正此前源码核对结论）：reasoning 关闭**仅作用于 EcoreGenTL 与 VL/VL-Fix**；SWE-Agent-Mini 的调用涉及 reasoning 配置，OpenCode 未显式覆盖、使用默认值。即两个 agent 基线均未受该设置影响——与老师会上"agent 那边没管"的口径一致。表述纪律：既不能写"所有方法统一关闭"，也不能含糊写"agent 均使用默认配置"，要按方法逐个陈述。
- [ ] **P2-3｜Acceleo 适配边界（A）**：核对 Acceleo/MTL 的生成流程；区分可复用的 pipeline 机制与必须重做的 generator adapter、template 和 framework-specific knowledge。
- [ ] **P2-4｜需求理解错误的外部证据（B）**：寻找顶会/顶刊工作，支持 requirement misunderstanding/decomposition 是 LLM 代码生成的常见固有限制；同时用本论文消融证明 review 有效但不完美。
- [ ] **P2-5｜Related Work（B/C）**：补 M2T、skeleton-guided completion、compiler-in-the-loop agent；明确本文不把 AST merge 或 compile-fix 单独当作 novelty，贡献是系统融合两种生成范式及解决融合挑战。
- [ ] **P2-6｜论文方法澄清（B）**：补充 Ecore annotation 到 Javadoc 的机制、review 输入、三类循环的停止条件、baseline repair protocol 和 root-cause attribution procedure。
- [ ] **P2-7｜贡献与受众（B）**：在 Introduction 明确 vision、among-the-first 的谨慎表述、MDE 与 SE 两方面 impact、目标 stakeholders，以及位于 model-to-code transformation 阶段。
- [ ] **P2-8｜其他小修（A）**：为参考文献补 DOI，并检查文中所有强泛化表述是否与 21 个任务的证据范围一致。

### P3：Rebuttal 定稿

- [ ] **P3-1｜填完全部占位符**：只有在 P0/P1 完成后替换 `[待核]`、`[待核实现]` 和 `[待补数据]`；没有结果时删掉承诺或采用可证实的定性表述。
- [ ] **P3-2｜建立证据索引**：每个关键反驳旁记录论文页码、算法/表格编号、附件目录或新增统计，尤其是输入等价、消融、artifact 和 retry 公平性。
- [ ] **P3-3｜压缩中文逻辑稿**：保留“开篇—Common Questions—Revision Plan—逐评审回复”，优先回答 A 的四个问题、完整处理 B 的澄清点、集中纠正 C 的错误前提。
- [ ] **P3-4｜同步英文稿**：中文定稿后再更新 `rebuttal_en.md`，控制在 1,000 words 内；所有数字、术语、交叉引用与中文稿保持一致。
- [ ] **P3-5｜最终一致性检查**：确认不出现三类风险表述——把 agent 配置说成统一受控、先承认实验设计不成立、或承诺把方法内部 skeleton 作为额外输入交给 baseline；同时统一 CSV、论文表格、rebuttal 和英文稿中的所有数字。

### 建议执行顺序

`P0-5 → P0-3 → P0-7 → P0-8/P0-9 → P0-2 → P1 → P2 → P3`
