感谢审稿人提出的建设性评审意见。

本文旨在探讨两种主流的代码生成范式——基于模板的生成（template-based）与大模型驱动的生成（LLM-driven）——能否实现有效结合。我们通过 EcoreGenTL 论证了其可行性，将 EMF 代码生成器与 LLMs 进行了集成，并深入探讨了其中的关键挑战：Ecore-aware context retrieval（感知 Ecore 的上下文检索）、code compression（代码压缩）、AST-based merging（基于抽象语法树的代码合并）以及 MDE knowledge injection（MDE 领域知识注入）。据我们所知，本研究是在 AI for MDE 背景下首批系统性探索该方向的工作之一。这项工作展示了传统 MDE 与新兴 LLM 技术如何实现优势互补，为未来的混合代码生成（hybrid code generation）提供了启示与见解。

Common Concerns（共同关切）

CC1-Benchmark size（基准规模）
JavaBench 是一个高质量的项目级（project-level）Java benchmark，包含 4 个 tasks（每个任务 2,560–6,926 LOC；分别包含 13–29 个 classes，32–53 个 methods），保证了任务的复杂性。鉴于项目级 Java benchmark 的稀缺性，我们补充构建了包含 10 个跨领域任务的 TLBench（详见附件 Experiments/Benchmarks）。
为了验证外部有效性（external validity），我们还进一步在 7 个 RealBench（FSE'26, doi:10.1145/3797106）任务上进行了评测，并引入了新的 agent baseline（OpenHands）；实验结果与当前趋势一致，将在 revision 中补充。

CC2-Token&time cost（Token 与时间开销）
我们在 JavaBench 上使用 gemini-3.1-flash-lite (G) 和 deepseek-v4-flash (DS) 测试了 token consumption。EcoreGenTL 的消耗为 3.46M/G、10.31M/DS，与 OpenCode（3.24M/G、11.2M/DS）相当；SWE-Agent-Mini（619K/G、6.05M/DS）、VL（165K/G、281K/DS）和 VL-Fix（171K/G、359K/DS）消耗的 token 较少但性能表现更差。我们将在 revision 中加入完整的统计数据。
由于受远程 API 服务与网络状况影响，执行时间无法进行完全公平的衡量；因此 token consumption 作为其代理指标（proxy）。

Revision Plan（修改计划）
1. 增强 Introduction：进一步明确研究动机（motivation）与适用范围（scope）。
2. 补充更多评估结果与分析（further evaluation results and analyses）。
3. 澄清方法与评估的具体细节（method/evaluation details）。

Response to Reviewer A（回复审稿人 A）

A1-Adaptability to Acceleo（对 Acceleo 的适应性）
我们的方法可以推广至其他从 Ecore 模型生成代码的 MDE 工具，但 EcoreGenTL 需要适配各个框架特定的编码风格与约束。例如，Acceleo 可能不会像 EMF 那样使用 @generated 标记，这会影响 AST-based merger（基于抽象语法树的代码合并器）等组件的具体实现。

A2-Pure EMF baseline（纯 EMF 基准）
标准的 EMF 代码生成器仅输出静态代码结构，不包含方法体实现（no method bodies），因此无法通过需要具体方法实现的单元测试。对所有 JavaBench/TLBench 任务进行的健全性检查（sanity check）证实：代码均可编译通过（Compilation@1=100%），但 Pass@1/3 均为 0%；test-level（用例级）通过率仅为 15.58%（JavaBench）和 0.37%（TLBench）。

A3-Experimental setting（实验设置）
1. 关于数据集规模（dataset size），参见 CC1。
2. 我们将 temperature 设置为 0.7，这是代码生成领域广泛采用的设置，能够在多样性（diversity）与稳定性（stability）之间取得平衡（Ren et al., EMNLP'25, doi:10.18653/v1/2025.emnlp-main.419; Tian et al., ICSE'25, doi:10.1109/ICSE55347.2025.00108）。
3. 各 LLMs 的推理控制参数（reasoning controls）在选项和语义上存在差异（例如 DeepSeek-V4-Flash 为 none/low/high/max，MiniMax-M3 为 disabled/enabled）。为尽量减少方差（minimize variance），我们在支持关闭 reasoning 的情况下将其统一关闭，或设置为最低档位。

A4-Time&token consumption（时间与 Token 开销）
参见 CC2。

Response to Reviewer B（回复审稿人 B）

B1-Stakeholders and phases（利益相关者与研发阶段）
我们提出了一种将 MDE template-based 生成与 LLM-driven 代码生成统一到单一流水线（pipeline）中的混合范式。与现有侧重于 LLM 辅助模型构建（model generation）的工作不同，我们聚焦于模型到代码转换阶段（model-to-code transformation）。
作为 AI for MDE 的一项贡献，本工作高度契合会议主题，对 MDE 和 SE 社区均具有重要参考价值。

B2-Ecore model updates（Ecore 模型更新机制）
EMF 通过 EAnnotations 处理文档说明，并在生成的代码中序列化为 docstrings。为复用 EMF 内置的 docstring 生成机制，EcoreGenTL 在方法上追加 EAnnotations 以存储 LLM 生成的 specifications，而不会修改类图结构（class diagrams，正文第 3-4 页）。

B3-Recovering omissions（遗漏信息的恢复机制）
Algorithm 2 同时向 LLMs 提供原始自然语言需求（original NL requirement）以及第一轮 specifications。由于原始需求始终完整保留，LLMs 能够恢复前序遗漏的细节。相关的 prompt template 已在补充材料中提供（EcoreGenTLShowcase/Prompt_Templates.md）。

B4-Requirement decomposition（需求分解问题）
我们赞同需求分析（requirement analysis）目前仍是一个开放性难题，这在先前的相关研究中也均有探讨（ICSE'25, doi:10.1109/ICSE55347.2025.00108; ICSE'26, doi:10.1145/3744916.3764572）。尽管仍有局限，review（审查）步骤能够在一定程度上缓解该问题，消融实验（ablation study）也证实了这一点。我们将在 revision 中深入探讨该问题。

B5-Retry limits（重试次数限制）
我们的算法设计本身不限制重试轮次（no retry limit），但在具体实现中将其设为 3，这是一个经验性的、可调的数值。除代码修复（code fixing）外，需求分解（requirement decomposition）和代码补全（code completion）在 LLM 输出不完整时也会触发重试。运行日志表明，停止条件通常在 1–2 轮迭代内即可满足，但仍有部分错误在达到 3 轮上限后依旧存在。

B6-Comparison protocol（基线对比协议）
感谢 Reviewer-B 指出这一点。VL-Fix 的重试上限同样设为 3（参见补充材料：Experiments/RQ1/Scripts/CodeGen/VL-Fix-TLBench.py 第 400 行；VL-Fix-JavaBench.py 第 483 行）。两个 agent baselines 则由自身策略自主决定迭代轮数。我们将在 revision 中明确该对比协议（protocol）。

B7-Failure causes analysis（失败归因分析）
归因分析分为两步：首先定位错误首次出现的阶段，然后识别其最直接主因（primary cause）。若 specification 本身有误但被忠实实现，则归因于 requirement decomposition（需求分解阶段）；若 specification 正确但实现错误，则归因于 code completion（代码补全阶段）。因此大部分失败案例均具备单一直接主因。两位作者独立进行了归因标注，在 393 个失败案例中仅对 9 例存在分歧（2.29%），分歧均通过讨论达成一致归为单一主因。详见补充材料文件 Experiments/RQ3/JavaBench_case_analysis.md 与 TLBench_case_analysis.md。

B8-Artifact（可复现构件与运行入口）
请参阅补充材料中的 EcoreGenTLShowcase/README.md 以执行并体验我们的 showcase。

Response to Reviewer C（回复审稿人 C）

C1-Value added over the skeleton（骨架之上的增量价值）
EcoreGenTL 与各 baselines 接收的外部输入完全相同：一份需求（requirement）与一份类图（class diagram）。MDE 代码生成是 EcoreGenTL 内部的执行机制，而非外部输入；直接将骨架代码（skeleton）提供给基线反而会导致比较不公平。
即便拥有 skeleton，基线仍需自行生成方法体（method bodies）。正文 Section V-B 的消融实验显示，移除 decomposition（需求分解）、context（上下文检索）或 repair（代码修复）均会导致 Pass@1 显著下降，证实了这些模块在骨架之外同样具有不可或缺的核心价值。

C2-Model/template cost and baseline exposure（模型/模板成本与基线信息暴露）
模板是 EMF 代码生成器自带的组成部分，由 MDE 领域工程师维护，因此我们在使用中无需承担额外的创建或维护成本。评测阶段的类图为人为构建，但这一过程完全可以通过现有技术实现自动化（如 TraceDev [ISSTA'26, doi:10.1145/3832171]、CDGen [MODELS'26, doi:10.1145/3822455.3830323]、MetaGPT [ICLR'24, doi:10.48550/arXiv.2308.00352]）。
所有方法接收到的类图信息完全一致，仅表示格式不同（Ecore 与 PlantUML），因此输入的信息内容是完全等价的。

C3-Benchmark availability & adaptation freeze（基准可用性与适配冻结）
JavaBench 的适配改造完全独立于 EcoreGenTL，并未对我们的方法产生偏向性偏差（bias）。正如正文第 6 页所述，我们仅从原始 Java 类中逆向提取 Ecore/PlantUML 类图，将原有 Javadoc 汇总为整体需求，并将测试适配至 EMF APIs。
TLBench 与适配后的 JavaBench 的全部细节均已在补充材料（Experiments/Benchmarks）中提供供审阅。

C4-Efficiency metrics & Pass@1 CIs（效率指标与 Pass@1 置信区间）
我们计算了 deepseek-v4-flash (DS) 与 minimax-m3 (MM) 在 task-level 上的 Pass@1。DS+JavaBench / MM+JavaBench / DS+TLBench / MM+TLBench 下的 task-level 置信区间（confidence intervals）分别为：
− EcoreGenTL: 0-45% / 0-42% / 28-88% / 29-83%
− OpenCode: 0-21% / 0% / 2-62% / 0-65%
− SWE-Agent-Mini: 0% / 0% / 0-64% / 0-58%
完整的置信区间数据将包含在 revision 中。关于 token consumption 参见 CC2，关于修复轮次开销（repair costs）参见 B5/B6。
