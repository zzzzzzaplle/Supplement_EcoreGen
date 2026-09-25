# EcoreGenTL Rebuttal Draft

> Working note (remove before submission): `[TBD]` marks information not supported by the current materials or still requiring confirmation. This draft follows the requested structure: positioning, common questions, revision plan, and concise reviewer-specific responses.

We thank all reviewers for their constructive feedback. Our goal is not to replace MDE with LLMs, but to investigate how their complementary strengths can improve project-level code generation. Templates provide a compilable, structurally sound skeleton; LLMs complete business semantics under operation-level specifications, compressed context, and EMF rules. AST merging confines changes to target method bodies, and compilation feedback repairs local errors. The contribution is this end-to-end fusion rather than merely placing MDE and LLMs side by side. The intended users are MDE engineers and tool maintainers with existing Ecore/EMF assets, and the target phase is code generation and semantic completion after the model structure has stabilized.

## Common Questions

**CQ1: Fairness and additional assets (A2; C1–C2).** All methods receive the same task semantics: the same natural-language requirement and class diagram. Ecore and the PlantUML supplied to baselines are equivalent representations of that diagram. The fixed EMF templates are an internal component of EcoreGenTL, not additional task labels or manually provided solutions; baselines receive PlantUML because it is their native input form. JavaBench is itself a skeleton-completion benchmark, and our ablations already isolate requirement decomposition/review, compression, context retrieval, and fixing. To remove the remaining concern, we will add an equal-skeleton comparison under matched budgets `[TBD]`. A pure-EMF sanity check compiles 100% but passes 0% of functional tests because its method bodies are empty, confirming that templates provide structure but not business semantics.

**CQ2: Scale, private data, and external validity (A3; B; C3).** We agree that the original 14 tasks provide only preliminary evidence. We therefore evaluated seven additional tasks from public, repository-level RealBench, increasing the total to 21. We extracted repository-level requirements, constructed Ecore diagrams, and migrated the original Python tests to Java while preserving their semantics and scenarios. Across the same five LLMs, EcoreGenTL achieves an average Pass% of 81.13%, versus 65.96% for the strongest average baseline, OpenHands (+15.17 pp). Against the best baseline selected separately for each model, the average gain remains 10.56 pp. Project Pass@1/Pass@3 are 0.25/0.40; we will therefore avoid equating method-level Pass% with complete-project success. We will release the RealBench adaptations, tests, and full results. Release of TLBench `[TBD with advisor]` and whether the JavaBench adaptation was frozen before method development `[TBD]` will be stated accurately.

**CQ3: Decomposition, review, repair, and attribution (B).** EcoreGenTL generates structured operation specifications from the original requirement and all operations in Ecore/PlantUML. Review then re-reads the original requirement, diagram, and generated specifications; it can recover first-pass omissions but cannot eliminate decomposition faults, which we will acknowledge more explicitly. “Updating the Ecore model” only updates operation documentation annotations, never classes, fields, references, or signatures. The retry limit of three applies only to compilation-error fixing. We will report mean repair rounds, limit-hit rate, and repair success `[TBD]`, and clarify recovery opportunities and budgets for VL-Fix and EcoreGenTL. Root causes were assigned per unique fault site by two independent authors and resolved by consensus; we will clarify that Table V records the most direct primary cause while interactions may exist.

**CQ4: Settings and cost (A3–A4; C4).** Reasoning was disabled uniformly to avoid vendor-specific reasoning modes and budget differences. Temperature 0.7 preserves useful diversity across five samples per task without excessive randomness. We will add the authors’ MDE experience `[TBD]` and report calls, input/output tokens, estimated cost, repair rounds, and task-level confidence intervals by model/method/benchmark `[TBD]`. Because latency is affected by shared APIs and network conditions, we will report reproducible usage measures and label wall-clock time as environment-dependent. Ecore/template authoring and maintenance time will also be reported `[TBD]`; our primary setting assumes existing MDE assets.

## Revision Plan

1. Add the full RealBench evaluation and public materials, and narrow generalization claims to the evidence from 21 tasks.
2. Add pure-template and equal-skeleton controls, project Pass@1/confidence intervals, cost, and repair-loop statistics.
3. Clarify annotations, the limits of review, root-cause protocol, and generic versus EMF-specific components; strengthen comparison with M2T, skeleton-guided completion, and compiler-in-the-loop agents.
4. Add executable showcase/RQ instructions, scripts, raw results, and missing DOIs.

## Reviewer-Specific Responses

### Reviewer A

**A1: Acceleo.** Porting is feasible but not cost-free. Decomposition/review, compression, context retrieval, AST merging, and compilation fixing are reusable; the generator adapter, Acceleo/MTL templates, and engine-specific coding rules must change. We will clarify this boundary.

**A2–A4: Non-LLM baseline, settings, scale, and cost.** Please see CQ1, CQ2, and CQ4. We will also add missing DOIs.

### Reviewer B

**B1: Stakeholders/phases.** The target users are engineers maintaining existing Ecore/EMF assets; the target phase is post-modeling code generation and business-logic completion, not requirements elicitation or automatic modeling.

**B2: Requirement–model connection/review.** Please see CQ3. Each specification is bound to an Ecore operation through its annotation, and review re-checks the original requirement rather than only the first-pass output. We will explicitly acknowledge the remaining fragility.

**B3–B4: Repair fairness, attribution, and artifact.** Please see CQ3. We will clarify retry scope and budgets, add convergence statistics, and provide runnable showcase/evaluation scripts, parameters, and raw results.

### Reviewer C

**C1–C2: Equal skeleton and human cost.** Please see CQ1/CQ4. EcoreGenTL receives no extra business answer; templates are fixed method infrastructure. We will nevertheless quantify this distinction with an equal-skeleton control and authoring cost.

**C3: Release/freeze.** Please see CQ2. RealBench supplies public, independent evidence; TLBench release scope and the JavaBench freeze point will be reported after confirmation.

**C4: Calls, tokens, latency, cost, repairs, and CIs.** Please see CQ4. We will report project-level metrics rather than presenting method-level Pass% as complete-project success.

**Other comments.** We will narrow the novelty claim and sharpen distinctions from M2T, skeleton-guided completion, and compiler-in-the-loop agents. We also agree that line coverage alone does not establish behavioral adequacy; public RealBench tests and releasable task-level materials will improve transparency.
