回复 BQ3:It is important to explain how the review step can recover missing preconditions or other omissions that were already lost in the first decomposition stage.
ICSE 2025
Tian, Chen, and Zhang, “Fixing Large Language Models’ Specification Misunderstanding for Better Code Generation,” ICSE 2025.
这篇论文与你们的问题几乎完全对应。它指出：
- LLM 面对复杂 programming specification 时，仍难以形成正确理解；
- CoT 等 thought-eliciting 方法可能产生错误的 specification understanding；
- 即使加入 self-improvement，仍不能保证完整理解 specification；
- LLM 在生成代码时还可能遗漏其已经得到的 specification understanding 中的重要内容；
- 其提出的 μFiX 正是通过检查和修正 specification misunderstanding 改善代码生成，Pass@1 相比最强基线平均提升 35.62%。
论文：ICSE 2025 页面，全文，DOI: 10.1109/ICSE55347.2025.00108。
这支持：
1. LLM 会误解复杂规约；
2. review/self-improvement 能够缓解这种误解；
3. 这种机制不能保证消除全部误解。
第二强依据：ICSE 2026
Tian and Chen, “Aligning Requirement for Large Language Model’s Code Generation,” ICSE 2026.
论文指出，即使已有 agent-based code-generation techniques，LLM 对 specification 的感知仍会产生 persistent misalignment；其提出的 Specine 专门识别和校正 LLM 所理解的 specification，并使 Pass@1 相比最强基线平均提升 29.60%。
它能直接支持“现有 agent 或 review 机制也不能完全消除 requirement misalignment”。
https://doi.org/10.1145/3744916.3764572