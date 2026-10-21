## 在Rebuttal中明确公开提出的Todo
    1.补充 RealBench 与 OpenHands 评测结果
        "...we also evaluated seven RealBench (FSE'26, doi:10.1145/3797106) tasks and a new agent baseline, OpenHands; results align with current trends and will be added in revision."
        在正文评估章节补充 7 个 RealBench 任务上的评测数据及 OpenHands agent baseline 的对比结果。
        已经搞定Openhands,RealBench的minimax m3数据异常,重跑中...目前完成llm -m3
    2.补充完整的 Token 消耗统计数据
        "We measured token consumption on JavaBench with gemini-3.1-flash-lite (G) and deepseek-v4-flash (DS)... We will include full statistics in revision."
        补充方法与基线在 JavaBench 上的完整 token 开销统计表格及分析。
         model(5) x method(6:llm,llm-fix,opencode,openhand,swe,ecoretl) x benchmark=9
    3.讨论需求分析/分解的开放性局限与 Review 机制
        "We agree that requirement analysis remains an open issue, as discussed in prior work (ICSE'25, doi:10.1109/ICSE55347.2025.00108; ICSE'26, doi:10.1145/3744916.3764572). Although limited, the review step partially mitigates this issue, as our ablation study confirms. We will discuss this in the revision."
        在正文中引用相关文献并讨论需求分解固有限制，说明 review 阶段的部分缓解机制。
    4.澄清基线对比协议（重试与迭代控制）
        "VL-Fix uses a retry limit of 3 (see supplement: Experiments/RQ1/Scripts/CodeGen/VL-Fix-TLBench.py, L400; VL-Fix-JavaBench.py, L483). The two agent baselines determine their own iteration counts. We will clarify the protocol in the revision."
        在实验设置章节清晰阐明各方法的重试机制（VL-Fix/EcoreGenTL 设为 3 次，agent 基线由自身策略自主循环）。
    5.补充完整的任务级（Task-level）Pass@1 置信区间
        "Full confidence intervals will be included in the revision. For token consumption, see CC2. For repair costs, see B5/B6."
        在正文结果部分完整报告 task-level 的 Pass@1 置信区间统计。

## MetaReviewer提出的 Todo

    M1. 基线需使用相同 skeleton 进行公平比较 【新要求，rebuttal 未说服】
        原文："The key comparison remains biased, i.e., the baselines are not evaluated with the same skeleton."
        Reviewer C 核心质疑：EcoreGenTL 内部使用 Ecore 生成完整 skeleton，而 baseline 仅获得 NL+PlantUML，输入不对等。
        rebuttal 中辩称"MDE 代码生成是内部能力而非外部输入"，但 meta-reviewer 明确不接受该解释。
        需要做：设计一组对照实验，将 Ecore 生成的 skeleton 同样提供给 baseline（VL/VL-Fix/OpenCode/SWE-Agent-Mini/OpenHands），
        在此条件下重新跑实验，报告各方法在获得相同 skeleton 后的 Pass@1 对比，从而隔离 EcoreGenTL 的分解、上下文路由、修复等组件的增量贡献。

    M2. 正式报告 RealBench 评测结果 【与 rebuttal 承诺 #1 重复】
        原文："The added RealBench results are appreciated, but the results need to be reported"
        rebuttal 中承诺补充 7 个 RealBench 任务及 OpenHands baseline，需在正文中以表格形式完整呈现结果与分析。

    M3. 细化 motivation 和 scope 【新要求】
        原文："The motivation and the scope need to be refined"
        对应 Reviewer B 意见：需更明确说明目标用户（stakeholders）和受益最大的软件工程阶段。
        需要做：在 Introduction/Approach 部分明确阐述 (1) 方法面向的用户群体（MDE 工程师/SE 开发者）；
        (2) 在软件开发生命周期中的定位（model-to-code 阶段）；(3) 与已有 LLM-for-MDE 工作的边界（本文聚焦代码生成而非模型生成）。

    M4. 充分讨论需求分解（requirement decomposition）的局限 【与 rebuttal 承诺 #3 重复】
        原文："The requirement decomposition issue should be mentioned and discussed properly"
        多位 reviewer 指出需求分解仍是主要错误来源，review 步骤只能部分缓解。
        需要做：在正文 Discussion/Threats 中明确承认需求分解是当前管线的薄弱环节，
        引用相关文献（ICSE'25/ICSE'26），分析 review 步骤能修复和不能修复的错误类型，讨论未来改进方向。

    M5. 报告故障归因分析细节并提供评估者一致性指标 【新要求】
        原文："Report the failure cause analysis and provide inter-agreement indexes for the human-based evaluation."
        分为两部分：
        (a) 故障归因分析：需在正文中详细报告 root-cause attribution 的判定流程、归因标准和具体案例，
            而不只是给出 Table V 的汇总数字。回应 Reviewer B 对 requirement/completion/EMF-knowledge faults 边界模糊的质疑。
        (b) 评估者一致性指标：rebuttal 中仅报告"393 个故障中 9 个分歧（2.29%）"，
            meta-reviewer 要求使用标准统计指标（如 Cohen's Kappa 或 Krippendorff's Alpha）量化两位标注者的一致性，
            并在正文中正式报告该指标值。

    

## 需要自己完善的
    1.中文半角/全角符号
    2.是否有个人信息泄漏