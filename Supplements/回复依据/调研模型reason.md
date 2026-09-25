**强力证据**
- GPT-5.4 Mini 官方说明支持 `none`、`low`、`medium`、`high`、`xhigh`，其中 `none` 是默认值；`temperature` 仅在 reasoning effort 为 `none` 时支持。这会成为解释实验设置的重要证据。[GPT-5.4 Mini 官方文档](https://developers.openai.com/api/docs/models/gpt-5.4-mini)

## 调研计划

### 1. 锁定实际实验配置

逐个核对本地代码、请求日志和配置文件，整理下表：

| Method | Model | Temperature | Reasoning 设置 | 设置位置 | API 是否实际接受 |
|---|---|---:|---|---|---|
| EcoreGenTL | 5 models | 0.7 | 待逐模型确认 | Java/配置文件 | 待查 |
| VL | 5 models | 0.7 | none/最低档 | Python 脚本 | 待查 |
| VL-Fix | 5 models | 0.7 | none/最低档 | Python 脚本 | 待查 |
| SWE-Agent-Mini | 5 models | 待查 | 明确为 `none` | agent 脚本 | 待查 |
| OpenCode | 5 models | 未显式覆盖 | 未显式覆盖 | OpenCode runtime | 待查 |

重点不是代码里“写了什么”，还要确认 provider 是否接受、忽略或重写这些参数。

### 2. 核对五个模型的官方 reasoning 语义

只使用官方 API 文档、官方模型卡或官方技术报告，分别确认：

- `minimax-m3`
- `gpt-5.4-mini`
- `qwen3.6-flash`
- `gemini-3.1-flash-lite`
- `deepseek-v4-flash`

每个模型记录：

1. 是否支持关闭 reasoning；
2. 支持哪些 effort/level；
3. 默认 reasoning 状态；
4. 是否同时支持 `temperature`；
5. reasoning 开启时是否忽略或拒绝 temperature；
6. 不同档位能否与其他模型直接对应。

这一步将决定能否保留：

> 不同模型的 reasoning 档位语义不可比。

如果官方资料不能支持，就改成更谨慎的：

> The models expose heterogeneous reasoning controls, so we disabled reasoning where the API allowed an explicit non-reasoning setting.

### 3. 为 temperature=0.7 搜集文献依据

检索 ICSE、FSE、ASE、TSE、TOSEM 等论文及其 replication package，筛选满足以下条件的文献：

- 任务是 code generation 或相近的软件工程生成任务；
- 采用多次采样，而非确定性单次生成；
- 明确报告 `temperature=0.7`；
- 最好涉及多个 LLM 或 Pass@k。

输出一个证据表：

| Paper | Venue | Task | Models | Temperature | Samples | 可比性 |
|---|---|---|---|---:|---:|---|

措辞决策规则：

- 若找到多篇高度可比的正式论文：可以写“0.7 is commonly used in prior code-generation studies”。
- 若证据数量少或任务差异大：不能写“常用值”，只能写“we fixed temperature at 0.7 as a controlled sampling setting”。
- 不寻找支持“0.7 最优”的证据，因为论文没有做 temperature sensitivity study，不能声称最优。

### 4. 分开论证 temperature 与 reasoning

两者不能共用一套理由。

Temperature 的可能论证：

- 所有可控方法统一使用 0.7；
- 控制方法间的 sampling randomness；
- 在 5 次采样中保留非零多样性；
- 不声称 0.7 最优。

Reasoning 的可能论证：

- reasoning 会增加 token、时间和额外计算；
- 不同模型暴露的 reasoning 控制不同；
- 在可控方法中关闭 reasoning，使比较聚焦于 pipeline，而不是额外推理预算；
- 但必须准确说明 SWE-Agent-Mini 也显式关闭 reasoning；
- OpenCode 的实际配置需要单独报告，不能笼统称为默认或自适应。

### 5. 重新判断公平性结论

当前草稿中的这句话风险较高：

> 该设置只约束我们自己的方法，未限制任何 agent baseline。

因为 SWE-Agent-Mini 同样设置了 `reasoning.effort=none`。

调研后需要在以下两种表述中选择：

如果 SWE-Agent-Mini 与可控方法都关闭 reasoning：

> We disabled reasoning for EcoreGenTL, VL/VL-Fix, and SWE-Agent-Mini; OpenCode used its runtime configuration because the wrapper did not expose the same control.

如果日志证明 OpenCode 自主使用 reasoning：

> OpenCode retained its runtime reasoning behavior, while the other controllable methods used non-reasoning mode. This does not disadvantage OpenCode, but the configurations are not identical and will be reported explicitly.

不能再写“两个 agents 均不受影响，因此对我们更严格”，除非日志能够证明。

### 6. 形成最终证据文件

如果你批准正式执行，我会先产出一份独立依据，不直接改 rebuttal：

`回复依据/temperature-reasoning-evidence.md`

内容包括：

- 五个模型的官方配置能力表；
- 各实验方法的实际请求参数；
- temperature=0.7 的论文证据；
- 哪些原草稿表述成立、哪些需要删除；
- 推荐的 AQ3 中文文本；
- 后续英文稿可使用的精简引用。

### 7. 最后才改 AQ3

只有以下事实全部锁定后才更新：

- 五个模型的准确名称；
- temperature 是否实际生效；
- 每个模型的 reasoning 参数；
- SWE-Agent-Mini 与 OpenCode 的真实设置；
- 至少一组可靠的 temperature 文献依据。

最终 AQ3 会按以下结构写：

1. 数据集见 CQ1。
2. Temperature：设置、理由、文献依据、公平性。
3. Reasoning：实际 scope、关闭理由、各模型配置差异。
4. Revision：承诺补充完整实验配置。

整个执行过程中不会把“常见”“默认”“不可比”或“对我们更严格”写入 rebuttal，除非有明确的代码或官方资料支持。