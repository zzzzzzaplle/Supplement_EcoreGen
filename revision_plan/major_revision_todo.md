# ICSE Major Revision Todo List

**Meta-review 要求和 Rebuttal 明确承诺均为必做项；明确承诺已在对应条目中标出。**

## P0：Meta-review 明确要求

- [ ] **1. 补充相同 skeleton 条件下的公平对比实验。**

  将同一 Ecore 模型生成的初始 Java skeleton 提供给 VL、VL-Fix、OpenCode、SWE-Agent-Mini，以及新增的 OpenHands，与 EcoreGenTL 对比。

  > The key comparison remains biased, i.e., the baselines are not evaluated with the same skeleton.
  >
  > — [MajorRevision · M1](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/MajorRevision原始意见.txt:379)

- [ ] **2. 正式补入 RealBench 和 OpenHands 的结果。**【Rebuttal 明确承诺】

  > The added RealBench results are appreciated, but the results need to be reported
  >
  > — [MajorRevision · M2](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/MajorRevision原始意见.txt:381)

  > For external validity, we also evaluated seven RealBench (FSE'26, doi:10.1145/3797106) tasks and a new agent baseline, OpenHands; results align with current trends and will be added in revision.
  >
  > — [Rebuttal · CC1](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/rebuttal_en.md:6)

- [ ] **3. 细化 motivation、目标用户和适用范围。**【Rebuttal 明确承诺】

  > The motivation and the scope need to be refined
  >
  > — [MajorRevision · M3](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/MajorRevision原始意见.txt:383)

  > Enhance the introduction by clarifying the motivation and scope.
  >
  > — [Rebuttal · Revision Plan 1](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/rebuttal_en.md:11)

- [ ] **4. 讨论需求分解与 specification review 的局限。**【Rebuttal 明确承诺】

  > The requirement decomposition issue should be mentioned and discussed properly
  >
  > — [MajorRevision · M4](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/MajorRevision原始意见.txt:385)

  > Although limited, the review step partially mitigates this issue, as our ablation study confirms. We will discuss this in the revision.
  >
  > — [Rebuttal · B4](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/rebuttal_en.md:34)

- [ ] **5. 补全故障归因流程，并报告标注者一致性指标（如 Cohen's κ）。**

  > Report the failure cause anaylsis and provide inter-agreement indexes for the human-based evaluation.
  >
  > — [MajorRevision · M5](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/MajorRevision原始意见.txt:387)

## P1：其余必做项——Rebuttal 承诺与初审遗留问题

- [ ] **6. 补充完整 token 消耗统计**【Rebuttal 明确承诺：完整 token 统计】

  > Model calls, tokens, latency, repair iterations, and human Ecore/template authoring cost are missing.
  >
  > — [初审 · Reviewer C](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:234)

  > We will include full statistics in revision.
  >
  > — [Rebuttal · CC2（节选）](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/rebuttal_en.md:8)

- [ ] **7. 补充项目级 Pass@1 的任务级置信区间。**【Rebuttal 明确承诺】

  > Full confidence intervals will be included in the revision.
  >
  > — [Rebuttal · C4（节选）](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/rebuttal_en.md:58)

- [ ] **8. 澄清各阶段及基线的重试上限、迭代预算，并报告修复收敛情况。**【Rebuttal 明确承诺：比较协议】

  > The two agent baselines determine their own iteration counts. We will clarify the protocol in the revision.
  >
  > — [Rebuttal · B6（节选）](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/rebuttal_en.md:38)

  > Logs show the stopping condition is usually met within 1-2 iterations, though some errors persist after 3.
  >
  > — [Rebuttal · B5](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/rebuttal_en.md:36)

- [ ] **9. 补入纯 EMF baseline，说明 temperature、reasoning 等实验设置的依据。**

  > Is it possible to add a non-LLM baseline for a sanity check?
  >
  > — [初审 · Reviewer A2](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:98)

  > Is it possible to add the rationale behind the experimental settings, e.g., dataset size, temperature, and reasoning configuration?
  >
  > — [初审 · Reviewer A3](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:100)

- [ ] **10. 交代 benchmark 构建与测试适配、数据固定时间，并公开可发布的任务与测试。**

  > Can the ten TLBench tasks or equivalent anonymous materials be released, and were JavaBench adaptations frozen before method development?
  >
  > — [初审 · Reviewer C3](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:253)

- [ ] **11. 澄清 Ecore 注解更新、review 输入和 AST merge 范围，讨论迁移 Acceleo 所需适配。**

  > EcoreGenTL appends EAnnotations to methods to store LLM-generated specifications, without modifying class diagrams (pages 3-4).
  >
  > — [Rebuttal · B2（节选）](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/rebuttal_en.md:30)

  > Is it possible to apply the tool to another template-based engine, i.e., Acceleo?
  >
  > — [初审 · Reviewer A1](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:96)

- [ ] **12. 完善复现包，补齐环境、运行步骤和 benchmark 执行、结果汇总脚本。**

  > However, the available package does not give clear instructions on how to reproduce the evaluation.
  >
  > — [初审 · Reviewer B（Artifact assessment）](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:182)

## P2：可选实验与提交收尾

- [ ] **13. 可选：补充 prompt 敏感性及 AST merging 等组件的消融实验。**

  > For instance, it would be helpful if the authors discussed how sensitive the pipeline is to initial prompt variations.
  >
  > — [初审 · Reviewer B](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:156)

  > with ablations isolating decomposition, context routing, AST merging, and repair.
  >
  > — [初审 · Reviewer C（节选）](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:239)

- [ ] **14. 统一全文结果、补齐 DOI，并完成格式、匿名性和逐点回复检查。**

  > Concerning the bibliography, the DOI should be inserted for each entry for the sake of verifiability.
  >
  > — [初审 · Reviewer A（DOI 要求；其余为提交收尾）](/Users/macbookair/Desktop/CodeBase/Supplement_EcoreGen/Supplements/原始审稿意见.md:92)
