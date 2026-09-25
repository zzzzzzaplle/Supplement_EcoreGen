# ICSE审稿意见

# 原文阅读分析



## Review \#955A

### Overall Scores

|Category|Score|Description|分析|
|---|---|---|---|
|Overall merit|3|Weak accept||
|Novelty|3|Substantial \- presents a clearly new idea, method, result, artifact, dataset, use case, workflow, or perspective||
|Rigor|2|Fair \- some weaknesses, but the main claims are at least partially supported|**适配难度**：虽然 EMF 在社区中应用广泛，但目前仅在 Xtext 上实现，作者需讨论将 EcoreGenTL 迁移到 Acceleo 的工程难度有多大。<br>**参数与配置缺乏依据**：作者需要交代自身在 MDE 领域的专业经验背景，并明确解释实验中模型采样温度（Temperature）和推理配置（Reasoning Configuration）是如何选择的，目前论文缺乏这部分的选型依据（Rationale）。<br>**指标完整度**：现有的正确性与完整性指标是恰当的，但“生成耗时”与“Token 消耗量”同样属于不可忽视的关键维度。<br>**基线检查**：必须引入纯 EMF 模板基线作为 Sanity Check。|
|Relevance|3|High \- addresses an important SE problem and is likely to influence research or practice under clear assumptions||
|Verifiability and Transparency|3|Good \- sufficient information is provided to understand the work and support verification of the main claims||
|Presentation|4|Excellent \- highly clear, well structured, and effective in communicating the contribution||

### Paper Summary

This paper proposes EcoreGenTL, an MDE\-based tool that exploits LLMs for code generation\. In particular, the process is guided by requirements written in natural language and an Ecore input model converted into a PlantUML textual specification\. Afterward, the generated specification is reviewed by the LLM to reduce any possible hallucinations and used to generate the code using the EMF template\-based component\. The code specification is eventually completed using the LLM\-driven generation injected with EMF\-specific rules\.

> 本文提出了 EcoreGenTL，这是一款结合LLM进行代码生成的模型驱动工程（MDE）工具。整个流程由NL req驱动，并将输入的 Ecore 元模型转换成 PlantUML 文本规约，以此作为后续步骤的输入，随后，生成的规约会经由 LLM 进行自Review以减少幻觉，并通过基于 EMF 的模板组件生成代码骨架最后，在注入 EMF 特定规则后，利用 LLM 完成方法体代码的补全\(\)。
> 
> 

To evaluate the approach, a tool implementation is provided and tested on two different benchmarks leveraging five prominent LLMs\. In addition, an ablation study and an error case analysis are discussed\.

> 为评估该方法，作者提供了工具实现，并在两个基准测试上利用 5 个主流大模型进行了实验测试；此外，还进行了消融实验和错误案例分析\(\)
> 
> 

### Strengths

- Relevant topic\.

- Evaluation is well\-conducted\.

- Replication package is provided\.

### Weaknesses

- A non\-LLM baseline, such as EMF template\-based generation, should be considered as a sanity check。

> 缺基线 :少一个纯传统 EMF 模板生成、完全不引入 LLM 的基线对比作为 Sanity Check\(\)
> 
> 

- Generalizability, in terms of framework and dataset size, might be an issue\.

> 方法的通用性和外部有效性受到质疑\(\)
> 
> 

- Time and token consumption, especially for closed\-source models, can be reported\.

> 可补充时间与 Token 消耗 尤其是closed\-source models
> 
> 

- Some experimental settings need to be justified\.

> 实验的参数与配置缺乏合理解释 
> 
> 找一些文章用0\.7的理由;
> 
> 



### Detailed Comments  for authors

Overall, the paper targets a relevant problem in the MDE community and proposes a structured and automated approach to augment MDE template\-based generation with LLMs\. While the evaluation and methodology are rigorous, there are concerns about the generalizability of the approach\. The authors properly acknowledge this limitation, but the dataset size remains very low, and the approach could be adapted at least to another EMF\-based engine, such as Acceleo\.



In this respect, the experimental settings systematically explore many relevant configurations\. However, a configuration that relies only on EMF template\-based generation, without LLMs, should be further explored\. On the positive side, the ablation and error case analyses further investigate the generation process and highlight the limits of the approach\.



The authors should also consider reporting the required time to obtain the final code and the token cost for closed\-source models\. Overall, the reviewer supports acceptance of the paper due to the relevant topic, potential practical impact for the MDE community, and rigor of the evaluation, although the mentioned limitations should be addressed before publication\.



### Novelty

The approach integrates and evaluates the use of LLMs with MDE template\-based code generation\. It is well discussed and framed with respect to the current state of the art, highlighting the novel contribution\. Although generalizability might be an issue, the process appears structured and useful for practitioners\.

> 尽管泛化性可能是一个问题，但整个流程看起来结构清晰且useful
> 
> 



### Rigor

The approach is well explained and introduces different automated loops for reviewing artifacts produced by LLMs\. Although it is only applied to Xtext\-based template\-based generation, EMF is a widely adopted framework in the MDE community\.

The authors should discuss how difficult it would be to apply EcoreGenTL to another well\-known template\-based code generator, such as Acceleo\. Regarding the evaluation, the overall process systematically explores different configurations, and the evaluation of failure cases is highly relevant from a practical perspective\.

> 尽管该方法仅应用于基于 Xtext 的模板生成，但 EMF 是 MDE 社区广泛采用的框架。在这方面，作者应讨论将 EcoreGenTL 应用于另一个著名的基于模板的代码生成器（即 Acceleo）的难度有多大
> 
> 

Concerning the evaluation, the overall process systematically explores different configurations, and the evaluation of failure cases is very relevant from a practical point of view\. 

However, the dataset size might limit generalizability, even though different domain applications are considered\. The authors should justify some experimental settings, such as reporting their expertise in the MDE field and clarifying why specific temperature and reasoning configurations were selected\. Currently, the rationale behind those choices is not fully justified\.



The quantitative metrics are relevant to the paper’s target, namely correctness and completeness\. However, execution time and token consumption are also relevant dimensions to consider\. Finally, the authors should introduce another baseline relying only on EMF template\-based generation as a sanity check\. //汇报纯模板的sanity check



### Relevance

Investigating the combined use of MDE and LLMs is a relevant topic\. The proposed tool has practical value for the MDE community because it combines deterministic checks with LLM\-based code augmentation\. The systematic evaluation, even on a small dataset, provides relevant insights for the community\.

> 表达对方法认可 ， 尽管dataset小
> 
> 

### Verifiability

The implementation is provided as supplementary material\. The README contains minimal yet effective instructions to run the tool\. Therefore, the results can be replicated\.

> README 包含了精简但有效的工具运行指引。因此，这些结果是可以复现的
> 
> 

### Presentation

The paper is overall well written and well structured\. Figures and tables summarize the obtained results properly\. Regarding the bibliography, DOI information should be inserted for each entry to improve verifiability\.

> 写作良好 
> 
> 关于参考文献，为了便于查验，每项文献条目都应该加上 DOI\(\)
> 
> 

### Questions for Authors’ Response

1. Is it possible to apply the tool to another template\-based engine, such as Acceleo?

> 问题1：是否可以将该工具应用于另一个基于模板的引擎（如 Acceleo
> 
> 核心痛点：质疑系统是否紧耦合在 Xtext 体系中，方法是否具备通用性
> 
> • 回复论据：系统在架构上是模块化解耦的。核心流程（需求分解映射、元模型语义转 Javadoc 注释、JDT AST 精准方法体回填、以及基于编译反馈的修复循环）完全独立于底层生成器。适配 Acceleo 仅需将方法骨架与签名的渲染模板改写为 Acceleo（MTL）语法，核心的 LLM 填充、AST 抽取与拼接逻辑 100% 可以复用。
> 
> 

2. Is it possible to add a non\-LLM baseline as a sanity check?

> 问题2：是否可以添加一个非 LLM 的基线作为合理性检查？
> 
> 直接给出纯模板（Pure EMF Template without LLM）的实测数据。在纯模板模式下，仅生成类骨架、字段与空方法体（或抛出 `UnsupportedOperationException`）：
> 
> - 语法编译通过率：100%（因为模板生成的结构完全符合 Java 语法）；
> 
> - 功能测试通过率（Pass Rate）：通常为 0%（因为没有实际业务逻辑实现）。
> 
> - 这一鲜明对比直接证明了 LLM 填充在实现业务语义上的决定性作用，消除了“性能提升纯靠模板骨架自带”的质疑。
> 
> 

3. Is it possible to add the rationale behind the experimental settings, such as dataset size, temperature, and reasoning configuration?

> 问题3：是否可以补充实验设置背后的依据，例如数据集规模、温度以及推理配置？
> 
> 

4. Is it possible to report execution time and token consumption for the experimented LLMs?

> 问题4：是否可以汇报所测试 LLM 的执行时间和 Token 消耗？
> 
> 

## Review \#955B



### Overall Scores

|Category|Score|Description|分析|
|---|---|---|---|
|Overall merit|3|Weak accept|整体倾向接收，但需要回应泛化性、实验设置和复现说明等问题。|
|Novelty|2|Incremental \- modest extension, adaptation, combination, or improvement|认为将 MDE 传统生成与 LLM 补全结合是已有技术/工作流的渐进式拓展（Modest extension/combination）|
|Rigor|3|Good \- appropriate methods and evidence adequately support the claims|认可实验方法论的大体框架（多模型、多基线、消融实验），比另外两位审稿人给的 Rigor 分数都要高|
|Relevance|2|Moderate \- relevant and useful, but the expected impact is limited or narrowly scoped|认为问题重要，但当前规模和泛化范围限制了实际影响判断。<br>|
|Verifiability and Transparency|2|Fair \- Some information is provided, but not enough to support independent scrutiny, verification, or replication where appropriate|认为论文对部分内部机制说明不足（如需求分解与 Ecore 交互、根因分析判定标准）<br>|
|Presentation|3|Good \- clear, well organized, and easy to follow|认可文章清晰有条理，但建议改善若干概念衔接和模型相关/通用部分的解释。|
|Artifact Assessment:|2||扣分项。审稿人实际下载并检查了复现包，认为缺少一键复现实验的脚本指引|



### Paper Summary

The paper proposes EcoreGentTL, a hybrid code\-generation pipeline that integrates template\-based techniques with LLM\-driven completion to address the limitations of standalone approaches within EMF contexts\. The methodology leverages Ecore models and natural\-language requirements to derive operation\-level specifications, subsequently generating a structurally correct Java skeleton\. An LLM then populates the method bodies, ensuring the skeleton's integrity through compression, context retrieval, AST\-based merging, and an iterative compile\-fix loop\. Evaluation against JavaBench and a private benchmark, using five LLMs, demonstrates significant improvements in both compilation and test success rates compared to vanilla and agent\-based baselines\.



### Strengths

- Clear and practical idea

- The results suggest a practical approach to retrofitting existing MDE tools instead of replacing them

- The empirical evaluation covers two benchmarks, five LLMs, four main baselines, and an ablation study that isolates the contribution of each major component of EcoreGenTL

    

### Weaknesses

- The requirement\-to\-model connection is not clear, and the paper shows that requirement decomposition remains a major source of errors

> 需求到模型的映射连接机制不明确，且论文自身的数据表明“需求分解错误”依然是最大的失效诱因
> 
> 

- The evaluation is limited in scale: the paper studies 14 problems in total, and one benchmark is private, so the external validity is promising but not fully convincing\.

> 样本规模仅 14 个，且含私有数据集，外部有效性不足。
> 
> 

### Detailed Comments for Authors



**Novelty\.**  

The paper presents a novel and interesting idea\. While the MDE community has explored various applications of LLMs to support modelling tasks, this work distinguishes itself by leveraging language models to complement established template\-based techniques\. However, to enhance the contribution, the authors should be more explicit regarding the intended stakeholders and the specific software engineering phases that would benefit most from the proposed approach\.

> 审稿人希望明确stakeholders **与 ****the specific software engineering phases\(****适用研发阶段）**
> 
> 

**Rigor\.**  

The evaluation is generally well designed, with multiple models, multiple baselines, and ablations\. The main weakness is that the benchmark suite is modest, and the analysis of failure causes is not fully demonstrated\. For instance, the paper’s own results show that requirement decomposition remains a major source of failure, which suggests that the specification layer is not solid enough\. For instance, it would be helpful if the authors discussed how sensitive the pipeline is to initial prompt variations\. //根据我们需求分解失败例子多的分析 而希望我们讨论Prompt 中采用的结构化模式（例如固定的 JSON/Markdown Schema 或元模型字段占位符）对自然语言变体具备一定鲁棒性

Additionally:

- page 4, “After collecting all revised specifications, EcoreGenTL will update the Ecore model to reflect the changes”: This sentence is confusing because the Ecore model is introduced as an input to the pipeline\. Please clarify whether the model itself is modified, or whether only the stored operation\-specification annotations are updated\.

> **审稿人觉得这句话容易让人误解。因为前文说 Ecore model 是输。现在又说会 “update the Ecore model”，疑惑：是修改了原始 Ecore 模型本身？还是只是把修改后的 operation specification 存回 Ecore model 的 annotation 里？**
> 
> **审稿人想让你澄清“更新 Ecore model”具体指什么。**
> 
> 
> 
> 

- page 4, “correcting any omissions, misinterpretations, or reversed requirements”: The review step is useful, but it is not completely clear why it should fix the kind of decomposition error shown earlier\. It is important to explain how the review step can recover missing preconditions or other omissions that were already lost in the first decomposition stage\.

> 审稿人说论文 Review 步骤没有解释它为什么能修复前面* ****需求分解***** **阶段已经犯下的错误。比如如果第一步分解需求时漏掉了某个 precondition，那么后面的 review step 怎么知道这个 precondition 被漏掉了？
> 
> 审稿人是在质疑：如果信息在第一阶段已经丢失，后续 review 凭什么能恢复它？
> 
> ```Plain Text
> 
> ```
> 
> 

- page 5, “while errors ≠ ∅ do”: The repair loop is plausible\. Unfortunately, the paper is not reporting detailed metrics on convergence rates or retry limits, such as how many iterations are usually needed and whether the loop ever reaches the retry limit without resolving the errors\.

> 审稿人认为没有报告这个循环实际运行得怎么样。比如通常需要几轮才能修好？有没有经常达到最大重试次数还修不好？审稿人想表达的是：你只展示了有这个循环，但没有给出它的运行统计，所以 不知道它是否稳定有效。
> 
> 

- page 6, “with a fixed retry limit of 3”: It is not clear if this limit applies only to code fixing or also to earlier completion stages, and and whether the chosen limit is sufficient in practice\. Without that, it is hard to judge the robustness of the iterative repair mechanism\.

> 审稿人不清楚这个 retry limit=3 到底适用于哪一部分。是只适用于代码修复阶段？还是也适用于前面的 code completion 或 req specification 阶段？另外，为什么 3 次足够？有没有依据？
> 
> 审稿人是在说：很难判断该迭代修复机制的robustness 
> 
> 

- page 6, “agentic and vanilla LLM baselines”: The evaluation compares against useful baselines, but Table III leaves some methodological details implicit, especially around how repair iterations are counted across methods\. Please make the comparison protocol more explicit so readers can tell whether the baselines and EcoreGenTL are being given equivalent opportunities to recover from errors\.

> 审稿人认为你们的 baseline 是有意义的，但比较协议还不够清楚。特别是 repair iterations 在不同方法中是怎么算的：EcoreGenTL 有几次修复机会？baseline 有没有同样的修复机会？如果不同方法获得的纠错机会不一样，那比较可能不公平。审稿人想知道各方法是否被给予了等价的错误恢复机会。
> 
> 它想让我们对比迭代的次数?  是不是 比较的公平性?  从公平性的角度回答这个事情,迭代次数我们看作是一个方法内部\.在这个环节是对比纯LM \+fix 
> 
> 目的减少琐碎的语法错
> 
> 

- page 9, “Table V presents the root cause attribution of each fault type”: The root\-cause analysis is interesting, but the attribution is not clear\. For instance, it is not convincing that requirement faults, completion faults, and EMF\-knowledge faults can always be separated so sharply, because these causes may interact in the same failing case\.

    > 归因方式是否足够清楚或可靠?；比如一个失败案例可能既有 requirement decomposition 问题，又有 completion 问题，还可能涉及 EMF knowledge 不足。审稿人认为这些原因可能互相交织，不一定能被清楚地分成独立类别。
    > 
    > 看下在原文怎么讲的这一快\.通过log日志 先定位阶段 再 原因 有些阶段一个原因 有些阶段很多原因 ,这个问题也不频繁
    > 
    > 这个过程看文章写了哪些?
    > 
    > 查了原始文档 有多个定位的原因 不超过 xxx
    > 
    > 

    

**Relevance\.**  

This is a highly relevant problem for software engineering; many MDE systems provide robust structural generation but rely on developers for semantic completion\. Consequently, a method that enhances completeness without compromising the generated skeleton could have significant practical impact\. As previously noted in the evaluation, I suggest the authors expand their discussion on the following points:

- page 10, “Most of the errors can be attributed to incorrect requirement decomposition and insufficient EMF knowledge”: While this is an important finding, it highlights a primary weakness: requirement decomposition remains a significant bottleneck, even following the review step\. I encourage the authors to acknowledge this limitation more explicitly and discuss the specific components of the pipeline that remain fragile\.

> 论文设计了专门的“审查步骤（Review step）”来纠错与校验需求，但在实证归因中，作者又承认“需求分解错误”依然是失败的主要诱因。审稿人认为 这构成了一个逻辑反差：既然 Review 模块的作用是保障质量，为什么关键错误依然大量发生在此处？这表明该模块并没有达到预期的把关效果。
> 
> 找一个代码生成文章 需求分解错 也比较常见的\. 需求分析错误是代码生成中比较常见的问题, 找顶会文章
> 
> 
> 
> 

- page 10, “The evaluation is limited to 14 problems”: This limitation should be emphasized more strongly\. The results are promising, but the current scale is still too small to support stronger claims about broader generalization and practical relevance\.

    > 审稿人认为论文在正文中对“仅有 14 个任务”的局限性轻描淡写了，态度不够严肃。
    > javabench本身比较复杂\.原因是现在对java语言 project\-level 代码生成的benchmark 相对来说比较少
    > 
    > 然后 说我们已经补了另外一个RealBench的结果 我们会在revision把结果补上
    > 
    > 

    

**Verifiability and transparency\.**  

The pipeline is described in enough detail to understand the major steps, but some parts need further clarification: how the requirement decomposition and review interact with the Ecore model, how the iterative repair loop behaves in practice, and how the root\-cause attribution was operationalized\. The paper also mentions a supplemental binary and showcase project\. However, based on the available materials, it is not clear how to reproduce the evaluation or inspect the internal logic of the tool\. Providing a clear instruction or script to run the benchmarks would significantly improve the artifact's usability \(see comments below in the "Artifact assessment" section\)\.

> 1\.需求分解和审查是如何与 Ecore 模型交互的、2\.迭代修复循环在实际运行中表现如何、3\.以及根因归因是如何具体操作落地的。
> 
> 3\.基于现有的材料，目前尚不清楚如何复现评估实验，也不清楚如何检查该工具的内部逻辑。如果能提供运行基准测试的清晰指引或脚本，将显著提高制品的可用性
> 
> 



**Presentation\.**  

The paper is generally clear and well organized, with a readable structure and a good separation between the approach, implementation, evaluation, and error analysis\. That said, some conceptual transitions would benefit from better explanations, especially around which parts of the pipeline are model\-dependent and which are generic EMF/LLM aspects\. 

* [ ] 待与老师商量

> 文章在一些概念之间的过渡解释还不够充分，特别是没有讲清楚 pipeline 里哪些部分依赖具体的 Ecore model，哪些部分是通用的 EMF/LLM 机制。
> 
> 

### Artifact Assessment

Partially Satisfactory, i\.e\., the artifacts partially contain what is declared in the submission form or the paper\. 

* [ ] 待与老师商量

### Comments on Artifact Assessment

The paper states that the supplemental material includes the EcoreGenTL binary and a simple showcase project\. However, the available package does not give clear instructions on how to reproduce the evaluation\. A README file is made available, even though it describes the content of the package without providing any details on the way to reproduce the performed experiments or even to just execute the approach on an illustrative scenario\.

> 应该说明如何复现实验； or even \.\.\. 至少应该说明如何跑一个简单示例
> 
> 介绍 showcase
> 
> 



## Review \#955C



### Overall Scores

|Category|Score|Description|分析|
|---|---|---|---|
|Overall merit|2|Weak reject|整体倾向拒稿，主要原因是实验规模、baseline 公平性和成本统计不足。|
|Novelty|2|Incremental \- modest extension, adaptation, combination, or improvement<br>|认为方法有工程价值，但 MDE \+ LLM 本身不新，需要更清楚地区分与已有生成、骨架引导和编译反馈 agent 方法的差异。|
|Rigor|2|Fair \- some weaknesses, but the main claims are at least partially supported<br>|认为主要实验存在输入资产和预算不对等的问题，且数据集规模太小，支撑广泛结论不足。|
|Relevance|3|High \- addresses an important SE problem and is likely to influence research or practice under clear assumptions|认可问题重要，尤其是在已有 Ecore/EMF 资产的结构化领域中具有实际价值。|
|Verifiability and Transparency|3|Good \- Sufficient information is provided to understand the work and support verification of the main claims|认可 supplementary material 已提供，但未展开更多复现细节评价。|
|Presentation|3|Good \- clear, well organized, and easy to follow|认为论文结构清楚、易于理解。|



### Paper Summary

EcoreGenTL combines Ecore/EMF template generation with LLM\-generated Java method bodies\. It deterministically generates project structure and signatures, decomposes requirements, retrieves and compresses context, generates method bodies, confines merging through AST\-based body replacement, and iterates on compiler feedback\. Across four adapted JavaBench problems and ten private TLBench problems with five LLMs, the paper reports higher method\-level pass rates than pure LLM generation\.



### Strengths

- The division of labor is sensible: deterministic generators handle structure and interfaces, while LLMs fill behavior\.

- Body\-only AST merging and compiler repair reduce the chance of structural corruption\.

- Five models and repeated samples are evaluated rather than one curated backend\.

- Method\-level improvements are large enough to suggest that scaffolding helps this task class\.

- The system workflow and examples are described clearly\.

    

### Weaknesses



- There are only 14 problems, ten from private TLBench, limiting statistical and external validity\.

> 数据集规模
> 
> 

- JavaBench is adapted into Ecore\-friendly multi\-class projects; both tests and scaffolds may favor the proposed approach\.

> 质疑改写的客观性——JavaBench 任务被作者主动改写为适合 Ecore 的多类工程，审稿人怀疑改写后的测试用例和骨架结构本身就对本方法存在偏向性（Evaluation Bias）
> 
> 

- Baselines receive natural language plus PlantUML, while EcoreGenTL has executable Ecore/template assets that encode signatures and require extra engineering\.

> **严重**：**输入不对等**。基线只有自然语言需求和 PlantUML；EcoreGenTL 得到的是更强的输入
> 
> 

- The headline Pass% is method\-level; exact project Pass@1 remains low, and methods are not independent samples\.

* [x] 待和老师讨论

> 质疑 Pass% 指标 想知道每一个benchmark内部各个指标的范围 画一个箱形图,内部的每一个case的比较 不一定是Pass%
> 
> 

- Model calls, tokens, latency, repair iterations, and human Ecore/template authoring cost are missing\.

> 论文缺少成本信息 
> 
> 

- Line coverage does not establish behavioral adequacy, and private tests cannot be inspected\.

> 代码行覆盖率高，并不能证明行为正确。
> 
> 同时，如果测试是私有的，外部读者无法检查测试是否合理
> 
> 

### Detailed Comments for Authors

**Novelty\.**  

Combining Ecore/EMF template generation for structure with LLM completion for behavior is sensible, and body\-only AST merging plus context routing have engineering value\. MDE plus generative AI is not itself new, so the paper should distinguish EcoreGenTL more precisely from model\-to\-text generation, skeleton\-guided completion, and compiler\-in\-the\-loop agents, with ablations isolating decomposition, context routing, AST merging, and repair\.

> 审稿人认为“MDE 结合生成式 AI”本身不是全新方向，要求论文更清晰地区分 EcoreGenTL 与传统“模型转文本（M2T）”、“骨架引导补全（Skeleton\-guided completion）”以及“编译闭环 Agent”的边界，并通过严密的消融实验精准量化需求分解、上下文路由、AST 合并与修复各自的独立贡献
> 
> 

* [ ] 优先级 : 中

> 创新性不容置疑 但是 我们未来会提高我们的related work
> 
> 

**Rigor\.**  

The main comparison confounds the method with unequal input assets and budgets\. Pure LLM baselines receive natural language and PlantUML, whereas EcoreGenTL has an Ecore model and templates that generate complete signatures\. Give every baseline the same Ecore\-derived skeleton, then isolate component gains and account for human model/template authoring and maintenance\. Four adapted JavaBench and ten private TLBench problems are too few for broad claims\.

> 两边输入不一致；审稿人建议：应该让所有 baseline 都拿到同样由 Ecore 生成的 skeleton，再比较 EcoreGenTL 的其他组件到底带来了多少提升
> 
> 实验规模太小
> 
> 

**Relevance\.**  

In structured domains that already maintain Ecore/EMF assets, deterministic scaffolding plus LLM behavior completion could be useful and safer for interfaces\.

> Relevance \& Verifiability \& Presentation 审稿人认可
> 
> 

**Verifiability \& transparency\.**  

Artifact is provided as supplementary material\.



**Presentation\.**  

The paper is in general well structured and easy to follow\.



### Questions for Authors’ Response



1. If all baselines receive the complete skeleton derived from the same Ecore model, how much do EcoreGenTL's decomposition, context, and repair stages add?

> 审稿人：如果 baseline 也拿到和 EcoreGenTL 一样的 Ecore\-derived skeleton，也就是完整代码骨架、类结构、方法签名，那么 EcoreGenTL 剩下的几个模块还能带来多少额外提升？
> 
> 首先这个观点不成立,我的input是相等的,其次javabench  paper就是一个 skeleton benchmark; 最主要的问题告诉他我们的输入是一致
> 
> 

2. How much human time is required to create and maintain Ecore models and templates, and can the same information be exposed to baselines?

> 审稿人：创建和维护 Ecore model、template 需要多少人工时间？这些人工准备的信息，能不能也给 baseline 使用？
> 
> 模版是emf内部的东西 它是我方法的一部分 LLM看不到这个模版
> 
> 我的输入是类图 只不过我的类图表现形式是ecore\(这里输入等价性是一样的,他们只是不同的\)
> 
> 找文章看看 有没有证明llm生成xml 或者ecore类图 不行;
> 
> 

3. Can the ten TLBench tasks or equivalent anonymous materials be released, and were JavaBench adaptations frozen before method development?

> 审稿人问两个问题。
> 
> 第一，10 个 TLBench 任务能不能公开？如果不能，能不能发布匿名化或等价材料？因为 TLBench 是私有的，别人无法检查、复现或验证。
> 
> 第二，JavaBench 的改造是不是在方法开发前就固定了？
> 这里的 **frozen before method development** 意思是：在开发 EcoreGenTL 之前，JavaBench 的改造版本是否已经确定，之后没有根据方法表现再调整。
> 
> 他质疑 benchmark 是边开发方法边调整的
> 
> 

4. What are calls, tokens, latency, cost, and repair rounds per condition, and what are task\-level confidence intervals for project Pass@1?

* [ ] Token可以考虑 latency 我们实验条件受到网络延迟,这个度量没办法合理的做

> 前半句问每种实验条件下的成本和过程数据：
> 
> - 调用了多少次模型
> 
> - 用了多少 token
> 
> - 运行延迟/耗时是多少
> 
> - 花费多少
> 
> - repair loop 运行了多少轮
> 
> - 后半句问 project Pass@1 的任务级置信区间；
> 
> - **confidence intervals**
> 就是置信区间
> 
> - 对每一个benchmark 比如十个case pass@1做一个置信区间;
> 
> 我们的置信区间比baseline要高
> 
> 
> 

先用中文写,所有的词 中文很简洁,逻辑清楚\(6\~700字体\)

中文翻译 , 英文翻译;第一个稿不在乎字数 先保证逻辑 

# 识别下一步任务

### **按序号**

1. “A non\-LLM baseline \.\.\. should be considered as a sanity check\.”
来源 Reviewer A ；跳转link[ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-S0w9d9dlYoekHrxs354cZ1AXnNg)
\[分析\] 审稿人想确认提升不是仅由 EMF 骨架带来的。
\[回复思路\] 做 Pure EMF Template baseline：只生成结构/签名/空方法体，报告编译率、测试通过率；回复时强调该 sanity check 显示 LLM completion 对业务语义不可替代。



2. “apply \.\.\. to another \.\.\. generator, i\.e\., Acceleo\.”
来源 Reviewer A 跳转link[ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-VmeDd9KzGofC5DxvhbCciyCjngg)
\[分析\] 质疑方法是否紧耦合 Xtext/当前 EMF 生成器。
\[回复思路\] 说明 pipeline 模块化；Acceleo 主要需替换模板渲染/生成器接口，spec decomposition、context retrieval、AST merge、compile\-fix 可复用；语气要保守，不说零成本。

3. “dataset size remains very low\.”
来源 Reviewer A
\[分析\] 外部有效性不足。
\[回复思路\] 承认 14 个任务是初步证据；补充每个任务的类/方法/测试规模，并说明跨 5 个 LLM、2 个 benchmark、多个领域的一致性结果。

4. “time \.\.\. and token consumption \.\.\. can be reported\.”
来源 Reviewer A
\[分析\] 成本维度缺失，尤其 closed\-source model。
\[回复思路\] 补 calls、tokens、latency、estimated cost 表；依据是现有脚本已有 token usage 输出，但需要持久化/重跑统计。

5. “justify \.\.\. expertise \.\.\. temperature and reasoning\.”
来源 Reviewer A ;[ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-PfuJdxtBwouMGHxZuNhczTcmn9d)
\[分析\] 实验设置缺少 rationale。
\[回复思路\] 补作者 MDE 背景、temperature=0\.7 的探索/生成平衡理由、reasoning off 的公平性/成本控制理由。

6. “DOI should be inserted for each entry\.”
来源 Reviewer A
\[分析\] 纯 presentation/verifiability 小修。
\[回复思路\] camera\-ready 中补 DOI；rebuttal 简短承诺即可。

7. “intended stakeholders and \.\.\. software engineering phases\.”
来源 Reviewer B ; [ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-VZmtd6rEworPncxqhIOcsLsDnhf)
\[分析\] 贡献定位不够清楚。
\[回复思路\] 明确面向已有 Ecore/EMF 资产的 MDE 工程师、工具维护者；适用阶段是模型稳定后的代码生成/语义补全。

8. “requirement\-to\-model connection is not clear\.”
来源 Reviewer B ;[ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-EAxfdyD2Ao8z0ixC9LDcDDeunHf)
\[分析\] 不清楚 NL requirement 如何映射到 Ecore operations。
\[回复思路\] 解释 PlantUML/Ecore operation list \+ structured spec schema \+ per\-operation annotations 的交互流程。

9. “update the Ecore model \.\.\. confusing\.”
来源 Reviewer B ；[ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-Eda6dXnz1oRSafxOmyYccED2nPf)
\[分析\] 审稿人误解为修改原始结构模型。
\[回复思路\] 澄清只更新 operation\-specification annotations/documentation，不改变 classes/fields/references。

10. “review step \.\.\. recover missing preconditions?”
来源 Reviewer B ; [ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-IVpgdnDeGoQMm5xRcL9ca2DOnze)
\[分析\] 质疑 review 是否真的能恢复第一阶段遗漏的信息。
\[回复思路\] 承认 review 不是完美恢复；它重新对照原始 NL requirement、PlantUML 和 specs 来检查遗漏，因此能降低但不能消除 decomposition faults。

11. “metrics on convergence rates or retry limits\.”
来源 Reviewer B ; [ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-GeFjd5smCorN60xYULAcIHukn9b)
\[分析\] repair loop 看起来合理，但缺运行统计。
\[回复思路\] 补每种条件下平均 repair rounds、达到 retry limit 的比例、修复成功率。

12. “fixed retry limit of 3 \.\.\. applies only to code fixing?”
来源 Reviewer B ; [ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-CXCnd5XSYoFZKvxQJP9ccpmwnVb)
\[分析\] retry scope 和充分性不清楚。
\[回复思路\] 明确 retry=3 的适用阶段；用新统计说明 3 次是否覆盖绝大多数 compile\-fix convergence。

13. “baselines \.\.\. equivalent opportunities to recover from errors\.”
来源 Reviewer B [ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-Ll1CdGLexoYvG2xkB9OcwtgMn4e)
\[分析\] baseline 与 EcoreGenTL 的fix机会可能不公平。
\[回复思路\] 明确 VL\-Fix/EcoreGenTL/agent 的 repair protocol、最大轮次、失败计数方式；必要时补 equal\-budget 对照。

14. “root\-cause attribution \.\.\. not clear\.”
来源 Reviewer B
\[分析\] RQ3部分根因可能交织，不宜硬分。
\[回复思路\] 补主因归类 protocol：双人独立标注、争议讨论、以最直接导致失败的 fault site 为主因；承认多因交互。

15. “requirement decomposition remains a significant bottleneck\.”
来源 Reviewer B [ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-OgphdpVZ6o21Whxs0qVczEgFnCf)
\[分析\] 需求分解还做了review结果还是一个瓶颈，审稿人认为这是一个逻辑反差。
\[回复思路\] 更明确承认为 limitation，并说明未来会加入 stricter schema、prompt variation robustness、human\-in\-the\-loop check。

16. “not clear how to reproduce the evaluation\.”
来源 Reviewer B
\[分析\] artifact 内容有，但入口和说明不够。至少要说明那个showcase的用例 [ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-Jyuedc0sioI5aExcHdycio2ln3f)
\[回复思路\] 立刻补 README：环境、API 配置、showcase、RQ1/RQ2 运行脚本、raw results 复算方法。

17. “which parts \.\.\. model\-dependent and which are generic\.”
来源 Reviewer B  [ICSE审稿意见](https://rcndaxauxyi4.feishu.cn/wiki/C3fewSGybiXiNik7f0NcTkJonPd#share-LdWid6fLmo5M8hx2HnhcvysinHc)
\[分析\] 概念边界不清楚。\-待与老师商量因为我没理解
\[回复思路\] 增加一段：Ecore\-specific 是模板、EMF coding rules、生成器接口；generic 是 spec decomposition、LLM completion、context retrieval、AST merge、compile\-fix。

18. “unequal input assets and budgets\.”
来源 Reviewer C
\[分析\] 最严重质疑：baseline 输入弱于 EcoreGenTL。
\[回复思路\] 补 same\-skeleton baseline：所有 baseline 都拿 EMF\-derived skeleton，再比较 EcoreGenTL 的 decomposition/context/repair 增益。

19. “Pass% is method\-level; exact project Pass@1 remains low\.”
来源 Reviewer C
\[分析\] 质疑指标夸大效果，方法级样本不独立。
\[回复思路\] 补 project\-level Pass@1 和 task\-level confidence intervals；解释 Pass% 衡量 partial correctness，不替代 project\-level success。

20. “human time \.\.\. create and maintain Ecore models and templates\.”
来源 Reviewer C
\[分析\] 人工建模/模板成本被忽略。
\[回复思路\] 统计或估计每个 benchmark 的 Ecore/model/template 准备时间；说明适用场景是已有 MDE assets 的项目。

21. “TLBench tasks \.\.\. released \.\.\. JavaBench adaptations frozen?”
来源 Reviewer C
\[分析\] 私有 benchmark 和改造过程可能引入偏差。
\[回复思路\] 尽量发布 TLBench 或匿名化材料；说明 JavaBench adaptation 是否在方法开发前冻结。

22. “calls, tokens, latency, cost, and repair rounds\.”
来源 Reviewer C
\[分析\] 成本与预算透明度不足。
\[回复思路\] 与第 4 点合并补表，按 method/model/benchmark 报告调用次数、token、时间、费用、repair rounds。

23. “line coverage does not establish behavioral adequacy\.”
来源 Reviewer C
\[分析\] 高覆盖率不能证明测试质量，私有测试不可查。
\[回复思路\] 补测试设计原则、需求到测试映射；若能公开测试则公开，否则提供匿名化/等价测试摘要。

### **合并同类项**

1️⃣ 前文第 1 点 Reviewer A 与第 18 点 Reviewer C 相同：都要求补 baseline 公平性；优先做 Pure EMF baseline \+ same\-skeleton baseline。

2️⃣ 前文第 4 点 Reviewer A 与第 22 点 Reviewer C 相同：都要求成本统计；合并成一张 cost/latency/token/repair 表。

3️⃣ 前文第 3 点 Reviewer A、第 15 点 Reviewer B、第 21 点 Reviewer C 相近：都围绕外部有效性和 benchmark 可信度；统一回应“小规模初步证据 \+ 公开/匿名化材料 \+ 更克制 claim”。

4️⃣ 前文第 11、12、13 点 Reviewer B 与第 22 点 Reviewer C 相近：关心 repair loop 是否公平、稳定、透明；统一补 convergence/retry statistics。

5️⃣ 前文第 8、9、10、15 点 Reviewer B 是一组：都围绕 requirement decomposition/review 机制；需要在 rebuttal 中承认 limitation，并澄清 annotation 更新和 review 能力边界。

6️⃣ 前文第 16 点 Reviewer B 是最低争议高收益任务：马上改 README，可以直接提升 artifact assessment。

7️⃣ 前文第 19 点 Reviewer C 是指标风险：必须补 project\-level Pass`@1/CI`，并避免在 rebuttal 中继续过度强调 method\-level Pass%。

8️⃣ 前文第 2 点 Reviewer A 与第 17 点 Reviewer B 相近：都要求讲清 framework/general mechanism 边界；可以合并写“generic components vs EMF\-specific components”。

# 已经能回答的问题

### **RealBench 补充实验**

#### 实验信息

1. Dataset
RealBench，一个公开的 repository\-level code generation benchmark，来自真实软件开发实践。

2. 任务规模
使用 RealBench 中的 **7 个任务**：

    - AI\-Games\-Hex

    - ArborParser

    - FreddyRodgers\_emojichef

    - KOSASIH\_SpaceXplore\-Core

    - KOSASIH\_celestrion\-nexus\-core

    - encore\-ecosystem\_NodeFlow

    - joseph\-crowley\_directory\-to\-markdown

3. 适配方式
从 RealBench 抽取 repo\-level requirement，并参考原 benchmark / 原论文提供的 UML 类图设计 Ecore 类图。

4. 测试方式
严格根据 RealBench 原 Python benchmark 的测试用例迁移到 Java；测试语义和测试场景保持一致。 Python\-to\-Java test migration。

5. 模型设置
跑了论文中相同的 5 个 LLM：

    - deepseek\-v4\-flash

    - minimax\-m3

    - gpt\-5\.4\-mini

    - qwen3\.6\-flash

    - gemini\-3\.1\-flash\-lite

6. 结果状态
补充实验已经完成，结果表已整理。趋势与 JavaBench / TLBench 一致。

7. Supplementary
RealBench 相关材料后续需要加入 supplementary materials。

#### **结果**

1. EcoreGenTL 在 RealBench 上的平均结果：

    - compile@1: **0\.97**

    - compile@3: **1\.00**

    - pass@1: **0\.25**

    - pass@3: **0\.40**

    - pass\_rate: **81\.13%**

    - C\_pass\_rate: **83\.18%**

2. 按平均 pass\_rate 看，最强 baseline 是 OpenHands：

    - EcoreGenTL: **81\.13%**

    - OpenHands: **65\.96%**

    - LLM\-Fix: **63\.45%**

    - Mini\-swe\-Agent: **56\.21%**

    - OpenCode: **55\.94%**

    - LLM: **48\.84%**

3. EcoreGenTL 相比平均最强 baseline OpenHands：

    - pass\_rate 提升约 **\+15\.17 percentage points**

4. 如果按每个模型分别挑最佳 baseline，EcoreGenTL 仍然更好：

    - deepseek\-v4\-flash: 95\.60% vs 86\.57%, \+9\.03 pp

    - minimax\-m3: 86\.51% vs 66\.82%, \+19\.69 pp

    - gpt\-5\.4\-mini: 89\.49% vs 80\.96%, \+8\.53 pp

    - qwen3\.6\-flash: 64\.25% vs 61\.94%, \+2\.31 pp

    - gemini\-3\.1\-flash\-lite: 69\.80% vs 56\.55%, \+13\.25 pp

5. 平均提升约 **\+10\.56 pp**。

**可以直接回应的审稿意见**

1. “There are only 14 problems, ten from private TLBench, limiting statistical and external validity\.”

    1. 来源 Reviewer C
    \[分析\] 审稿人质疑原实验规模小，并且过度依赖私有 TLBench。
    \[如何回复\] 现在可以回答：我们新增了公开 RealBench 上的 7 个 repository\-level tasks。RealBench 公开、真实、仓库级，能缓解 external validity 和 private benchmark 的质疑。结果显示 EcoreGenTL 在 RealBench 上仍然取得最高平均 pass\_rate，pass@1, 趋势与原实验一致。

2. “The evaluation is limited in scale: the paper studies 14 problems in total, and one benchmark is private\.”

    1. 来源 Reviewer B
    \[分析\] 和 Reviewer C 类似，但语气更温和，主要担心 scale 和 transparency。
    \[如何回复\] 用 RealBench 直接补强：新增 7 个公开任务，使评估从 14 个任务扩展到 21 个任务，并加入真实仓库级场景。

3. “dataset size remains very low\.”

    1. 来源 Reviewer A
    \[分析\] Reviewer A 支持接收，但希望增强实验规模。
    \[如何回复\] 说明新增 RealBench 后，任务数量增加，且 benchmark 来源更独立、更贴近真实开发。

4. “Can the ten TLBench tasks or equivalent anonymous materials be released?”

    1. 来源 Reviewer C
    \[分析\] 审稿人担心私有 TLBench 不可检查。
    \[如何回复\] RealBench 不能完全替代 TLBench 释放问题，但可以显著缓和：我们新增了公开 benchmark 的实验，并会把 RealBench 适配材料加入 supplementary。TLBench 是否公开仍需和老师讨论。

5. “Line coverage does not establish behavioral adequacy, and private tests cannot be inspected\.”

    1. 来源 Reviewer C
    \[分析\] 审稿人认为 TLBench 测试可信度不足。
    \[如何回复\] 说明 RealBench 的测试来自原 benchmark 的 repo\-level tests，我们只是进行 Python\-to\-Java 迁移，并保持测试语义和场景一致，因此提供了更透明的行为验证补充。

**英文 rebuttal 草稿**

> To further address the concerns about evaluation scale, external validity, and the reliance on a private benchmark, we conducted an additional evaluation on RealBench, a public repository\-level code generation benchmark aligned with real\-world software development practices\. We selected seven RealBench tasks and adapted them to our setting by extracting repository\-level requirements and constructing Ecore models based on the UML class diagrams provided by the original benchmark\. The original RealBench tests were migrated from Python to Java while preserving the same test semantics and scenarios\.
> 
> We evaluated the same five LLMs as in the original submission\. The results show a trend consistent with JavaBench and TLBench\. EcoreGenTL achieves an average pass\_rate of 81\.13%, outperforming the strongest average baseline, OpenHands, by 15\.17 percentage points\. Even when comparing against the best baseline for each individual model, EcoreGenTL still improves pass\_rate by 10\.56 percentage points on average\. These results provide additional evidence that the benefits of EcoreGenTL are not specific to TLBench or the JavaBench adaptation\.
> 
> We will include the RealBench adaptation, tests, and result table in the supplementary materials\.
> 
> 



