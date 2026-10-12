We appreciate the constructive review comments.
This paper investigates whether the two popular code generation paradigms---template-based and LLM-driven---can be effectively combined. We demonstrate feasibility via EcoreGenTL, integrating an EMF code generator with LLMs, and discuss key challenges: Ecore-aware context retrieval, code compression, AST-based merging, and MDE knowledge injection. To our knowledge, we are among the first to systematically study this in the context of AI for MDE. This work shows how traditional MDE and emerging LLM techniques complement each other, offering insights for future hybrid code generation.
Common Concerns
CC1-Benchmark size
JavaBench is a high-quality project-level Java benchmark with four tasks (2,560-6,926 LOC; 13-29 classes, 32-53 methods each), ensuring complexity. Given scarce project-level Java benchmarks, we added TLBench with ten cross-domain tasks (see Experiments/Benchmarks).
For external validity, we also evaluated seven RealBench (FSE'26, doi:10.1145/3797106) tasks and a new agent baseline, OpenHands; results align with current trends and will be added in revision.
CC2-Token&time cost
We measured token consumption on JavaBench with gemini-3.1-flash-lite (G) and deepseek-v4-flash (DS). EcoreGenTL uses 3.46M/G, 10.31M/DS, comparable to OpenCode (3.24M/G, 11.2M/DS); SWE-Agent-Mini (619K/G, 6.05M/DS), VL (165K/G, 281K/DS), and VL-Fix (171K/G, 359K/DS) use fewer tokens but perform worse. We will include full statistics in revision.
Execution time cannot be fairly measured due to remote services and network conditions; token consumption serves as a proxy.
Revision Plan
1. Enhance the introduction by clarifying the motivation and scope.
2. Add further evaluation results and analyses.
3. Clarify method/evaluation details.
Response to Reviewer A
A1-Adaptability to Acceleo
Our approach generalizes to other MDE tools producing code from Ecore models, but EcoreGenTL must adapt to each framework's coding styles and constraints. For instance, Acceleo may not use @generated as EMF does, affecting components like the AST-based merger.
A2-Pure EMF baseline
The standard EMF generator emits only static structure, no method bodies, so it cannot pass tests requiring method implementations. A sanity check on all JavaBench/TLBench tasks confirms: code compiles (Compilation@1=100%) but yields 0% Pass@1/3; test-level pass rates 15.58% (JavaBench), 0.37% (TLBench).
A3-Experimental setting
1. Regarding dataset size, see CC1.
2. We set temperature to 0.7, a widely adopted setting balancing diversity and stability in code generation (Ren et al., EMNLP'25, doi:10.18653/v1/2025.emnlp-main.419; Tian et al., ICSE'25, doi:10.1109/ICSE55347.2025.00108).
3. Reasoning controls vary across LLMs in options and semantics (e.g., DeepSeek-V4-Flash: none/low/high/max; MiniMax-M3: disabled/enabled). To minimize variance, we standardized reasoning by disabling it where possible or using the lowest level.
A4-Time&token consumption
See CC2.
Response to Reviewer B
B1-Stakeholders and phases
We introduce a hybrid paradigm unifying MDE template-based and LLM-driven code generation in a single pipeline. Unlike existing efforts focusing on LLM-assisted model generation, we target model-to-code transformation.
As a contribution to AI for MDE, this work aligns with the conference topic and is relevant to both MDE and SE communities.
B2-Ecore model updates
EMF handles documentation through EAnnotations, serialized as docstrings in generated code. To reuse EMF's built-in docstring generation, EcoreGenTL appends EAnnotations to methods to store LLM-generated specifications, without modifying class diagrams (pages 3-4).
B3-Recovering omissions
Algorithm 2 provides LLMs with the original NL requirement and first-pass specifications. Since the original requirement remains fully available, LLMs can recover omitted details. The prompt template is provided in the supplement (EcoreGenTLShowcase/Prompt_Templates.md).
B4-Requirement decomposition
We agree that requirement analysis remains an open issue, as discussed in prior work (ICSE'25, doi:10.1109/ICSE55347.2025.00108; ICSE'26, doi:10.1145/3744916.3764572). Although limited, the review step partially mitigates this issue, as our ablation study confirms. We will discuss this in the revision.
B5-Retry limits
Our algorithms have no retry limit, but our implementation sets it to 3, an empirical, adjustable value. Beyond code fixing, requirement decomposition and code completion also retry on incomplete LLM output. Logs show the stopping condition is usually met within 1-2 iterations, though some errors persist after 3.
B6-Comparison protocol
We thank Reviewer-B for catching this. VL-Fix uses a retry limit of 3 (see supplement: Experiments/RQ1/Scripts/CodeGen/VL-Fix-TLBench.py, L400; VL-Fix-JavaBench.py, L483). The two agent baselines determine their own iteration counts. We will clarify the protocol in the revision.
B7-Failure causes analysis
Attribution has two steps: locate the stage where the error first occurs, then identify its primary cause. A faulty specification faithfully implemented is attributed to requirement decomposition; a correct specification implemented incorrectly is attributed to code completion. Most failures thus have a single primary cause. Two authors independently attributed causes and disagreed on only 9 of 393 failures (2.29%), all resolved by discussion into a single cause. See supplement files Experiments/RQ3/JavaBench_case_analysis.md and TLBench_case_analysis.md.
B8-Artifact
See EcoreGenTLShowcase/README.md in supplement to execute our showcase.
Response to Reviewer C
C1-Value added over the skeleton
EcoreGenTL and baselines receive the same external inputs: a requirement and a class diagram. MDE code generation is internal to EcoreGenTL, not an external input; providing the skeleton to baselines would be unfair.
Even with the skeleton, baselines must still generate method bodies. The ablation in Section V-B shows that removing decomposition, context, or repair each substantially reduces Pass@1, confirming these components are essential beyond the skeleton.
C2-Model/template cost and baseline exposure
Templates are part of the EMF generator and maintained by MDE engineers, so we incur no creation or maintenance effort for them. Class diagrams were created manually during evaluation, but this process can be automated (TraceDev [ISSTA'26, doi:10.1145/3832171]; CDGen [MODELS'26, doi:10.1145/3822455.3830323]; MetaGPT [ICLR'24, doi:10.48550/arXiv.2308.00352]).
All methods receive the same class-diagram information---only the format differs (Ecore vs. PlantUML). Therefore, the information content is identical.
C3-Benchmark availability & adaptation freeze
The JavaBench adaptation is independent of EcoreGenTL and does not bias the benchmark in our favor. As explained on page 6, we only derived Ecore/PlantUML class diagrams from the original Java classes, aggregated the Javadocs into a holistic requirement, and adapted the tests to EMF APIs.
Full details of TLBench and the adapted JavaBench are provided in the supplement (Experiments/Benchmarks) for review.
C4-Efficiency metrics & Pass@1 CIs
We calculated task-level Pass@1 under deepseek-v4-flash (DS) and minimax-m3 (MM). The task-level confidence intervals for DS+JavaBench/MM+JavaBench/DS+TLBench/MM+TLBench are:
− EcoreGenTL: 0-45%/0-42%/28-88%/29-83%
− OpenCode: 0-21%/0%/2-62%/0-65%
− SWE-Agent-Mini: 0%/0%/0-64%/0-58%
Full confidence intervals will be included in the revision. For token consumption, see CC2. For repair costs, see B5/B6.