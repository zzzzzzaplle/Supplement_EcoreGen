# Retry 实现与 Baseline 协议核对

已直接核对当前源码。**三个阶段的重试机制不同，不能统一写成“最多重试 3 次”。** 以下“尝试次数”均包含首次执行。

| 阶段 | 停止条件 | 实际上限 |
|---|---|---|
| **Requirement decomposition：规格生成** | `targetOperations` 为空，即所有目标操作都获得可解析的规格 | **循环没有次数上限**；回复若始终未覆盖剩余操作且不抛异常，会一直请求。源码：`EOperationAnnotator.xtend:155` |
| **Requirement decomposition：规格校验** | 完成规定轮数；没有“无需修改就提前退出”的判断 | 按类执行：待校验操作 **≤5 个时 1 轮，>5 个时 2 轮**。源码：`EOperationAnnotator.xtend:198` |
| **Code completion** | 回复产生非空 `ClassLens`，完成合并、保存后立即返回；否则耗尽次数退出 | 每类每次执行最多 **3 次请求，即首次＋2 次重试**。耗尽只记录 warning，不抛异常。源码：`CodeCompletion.xtend:79` |
| **Code fix：编译反馈循环** | `getCompilationError()` 返回空列表，或者达到轮数上限 | 最多 **3 轮“编译→修复”**。源码：`CodeFixing.xtend:58` |
| **Code fix：单类回复解析** | 非空 `ClassLens` 合并、保存后返回；否则耗尽次数退出 | 每轮每个错误类最多 **3 次请求**；耗尽后直接结束该次执行。源码：`CodeFixing.xtend:229` |

**另外还有一层异常重试：**

- 需求分解：整个模型增强及保存过程，异常时最多 **3 次尝试**，耗尽后抛异常。`ModelwareEnhancement.xtend:18`。
- 代码补全、单类修复：每类增强过程异常时最多 **3 次尝试**，每次重新进入内部循环；耗尽后抛异常。内部 3 次解析失败后正常返回，**不会触发这层重试**。`CodewareEnhancement.xtend:41`。

## Baseline 核对

| Baseline | 实际控制方式 | 上限/停止条件 |
|---|---|---|
| **VL** | 配置只执行 `generate + compile`；源码虽保留 `run_fix()` 辅助函数，但当前实验的 `PHASES` 不会调用它 | 一次生成，不执行编译反馈修复。`VL-TLBench.py:63`、`VL-JavaBench.py:66` |
| **VL-Fix** | 初次编译失败后循环执行“LLM 修复 → 重新编译”，编译成功即停止 | 最多 **3 次修复请求**。`VL-Fix-TLBench.py:398-454`、`VL-Fix-JavaBench.py:481-536` |
| **SWE-Agent-Mini** | 单次 `agent.run()` 中，由 agent 自主选择生成、编译、修改和提交动作 | 没有固定为 3 次 repair；受 **100 个 agent steps** 与单次运行 **1200 秒**上限约束。脚本同时显式设置 `reasoning.effort=none`。`SWE-Agent-Mini-TLBench.py:173-207`、`SWE-Agent-Mini-JavaBench.py:281-303` |
| **OpenCode** | 单次 `opencode run`；prompt 要求生成后编译并在出错时修复，具体工具调用与修复轮次由 OpenCode 自主决定 | wrapper 未设置 repair 次数，只设置单次运行 **1200 秒**超时；配置文件仅设置工具权限。`OpenCode-TLBench.py:85-185`、`OpenCode-JavaBench.py:251-343` |

VL/VL-Fix 中用于处理网络/API 异常的 `_call_openai()` 重试不属于 compiler-feedback repair，不应计入方法的 repair rounds。

## 可用于 Rebuttal 的结论

不能把所有方法描述成“统一最多重试 3 次”。更准确的比较协议是：

1. VL 是 one-shot generation，不含修复；VL-Fix 在初次编译失败后最多进行 3 次 compiler-feedback repair。
2. EcoreGenTL 的最终 code-fix 同样最多进行 3 轮“编译 → 修复”，因此它与 VL-Fix 在这一可直接对齐的阶段具有相同上限。
3. Agent baseline 的内部循环是其方法机制的一部分：SWE-Agent-Mini 自主行动但受 100-step/1200-second 上限约束，OpenCode 自主行动且只受 1200-second wrapper 超时约束。我们没有人为把 agent 的 repair 限制为 3 次。
4. EcoreGenTL 的 requirement decomposition、specification validation、code completion 和 code fix 具有不同停止条件，revision 中应分别说明，不能用一句“retry limit=3”概括整个 pipeline。
