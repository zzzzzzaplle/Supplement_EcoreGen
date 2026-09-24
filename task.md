# Task Progress: 投稿附件中文清理与双盲匿名合规化 (Supplement Clean-Up)

> 本文档由顶会投稿附件检查与排查建议自动初始化，作为未来 Agent 与协作者执行代码库清理、英文规范化以及双盲评审（Double-Blind Review）合规改造的任务看板与进度跟踪器。  
> **核心目标**：彻底解决投稿附件中存在的**中文字符/全角标点残留**以及**双盲评审违规（作者姓名、单位标识、本地硬编码路径泄密）**风险，确保附件满足 ICSE/FSE/ASE/ISSTA 等国际顶会要求。

---

## 📊 进度看板 (Progress Dashboard)

| 阶段 / 任务模块 | 优先级 | 涉及文件数 | 状态 | 负责人 / Agent | 完成进度 |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Phase 0: 自动化扫描工具与基线报告** | P0 | 全库脚本 | [x] 已完成 | Agent | 100% |
| **Phase 1: 致命级双盲评审泄密点清理 (Anonymity)** | P0 | 4 个文件 | [ ] 待开始 | Agent | 0% |
| **Phase 2: 打包工具与 Showcase 构建物清理 (Jar/Showcase)** | P1 | Showcase 项目 | [ ] 待开始 | Agent | 0% |
| **Phase 3: 基准数据集模型与规格文档标准化 (Benchmarks)** | P2 | 34 个文件 | [ ] 待开始 | Agent | 0% |
| **Phase 4: 基准测试用例注释英文规范化 (Test Code)** | P3 | 29 个测试文件 | [ ] 待开始 | Agent | 0% |
| **Phase 5: 实验复现脚本清理 (Reproduction Scripts)** | P4 | 6 个 .mwe2 | [ ] 待开始 | Agent | 0% |
| **Phase 6: 大模型生成原始输出批量清洗 (Raw Results)** | P5 | 953 个文件 | [ ] 待开始 | Agent | 0% |
| **Phase 7: 全库终验扫描、可复现性验证与收尾** | P0 | 全库闭环 | [ ] 待开始 | Agent | 0% |

**状态标识说明**：
- `[ ]` 待办 (Pending)
- `[/]` 进行中 (In Progress)
- `[x]` 已完成 (Completed)
- `[-]` 评估后跳过/不适用 (Skipped / N/A)

---

## 🤖 Agent 执行守则与协作规范 (Execution Guidelines for Agents)

1. **原子化执行与实时打勾**：
   - 每次领取一个子任务前，先将对应行状态改为 `[/]`；完成并验证通过后，将 `[/]` 改为 `[x]`，并在下方“执行日志”中追加简明记录（时间、修改内容、测试结果）。
2. **代码逻辑零破坏原则 (Preserve Logic & Syntax)**：
   - 绝不能破坏 Java 代码语法、缩进、字符串转义以及 Ecore 模型 XMI 结构。
   - 替换注释或全角标点时，确保单测依然能正常编译运行，断言行为不变。
3. **分级防御双盲风险**：
   - 优先解决可能导致系统 Desk Reject（直接秒拒）的敏感词（`Zeqing`、`USTB`、本地 Windows 绝对路径）。
4. **工具化批量处理**：
   - 针对 953 个 Raw Results 文件，必须先在小样本上做 dry-run 验证，禁止盲目大面积正则替换导致文件损毁。

---

## 📋 详细任务清单 (Detailed Task Breakdown)

### Phase 0: 自动化扫描工具与基线建立 (Baseline & Tools)
- [x] **0.1 固化全库中文字符与全角标点检测工具**
  - **路径**: `scripts/scan_chinese.py` (已实现)
  - **规则**: 正则扫描 `[\u4e00-\u9fff]` (汉字)、`[\uff01-\uff5e]` (全角符号如 `：`、`，`、`（`、`）`)、`[\u3000-\u303f]` (中文标点如 `、`、`。`、`【`、`】`)。
  - **验收标准**: 输出命中文件路径、行号及内容预览，支持 JSON 结构化报告保存 (`scripts/baseline_chinese_report.json`)。
- [x] **0.2 固化双盲匿名性扫描工具**
  - **路径**: `scripts/check_anonymity.py` (已实现)
  - **规则**: 正则检查 `zeqing` (大小写不敏感)、`ustb`、Windows 绝对盘符路径 (`[A-Za-z]:\...`)、`OneDrive` 等。
  - **验收标准**: 生成基线报告 (`scripts/baseline_anonymity_report.json`)，支持清洗前后 diff 验证。
- [x] **0.3 记录清理前初始基准指标 (Initial Baseline Check)**
  - 确认待清理中文文件数：**993 个**（含 Benchmarks 34 个、Scripts 6 个、Raw Results 953 个），命中总行数：**7,105 行**。
  - 确认作者名泄密文件数：**1 个文件** (`EcoreGenTL-TLBench-tests.py`)。
  - 确认本地硬编码绝对路径文件数：**4 个文件** (`EcoreGenTL-TLBench-tests.py`, `EcoreGenTL-JavaBench-tests.py`, `CR1_TotalBudgetofAllProjectsTest.java`, `CR4_FundingGroupTypeTest.java`)。
  - 确认机构 URI 标注文件数：**10,031 个文件** (包含 `http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen`)。

---

### Phase 1: 【P0 致命级】双盲评审匿名性信息彻底清除 (Double-Blind Cleansing)
> **风险说明**：国际顶会严格遵循双盲评审，任何审稿人一旦在脚本或路径中看到作者名字拼音或单位标识，有义务直接上报 PC Chair，将直接触发 **Desk Reject（无条件拒稿）**。

- [ ] **1.1 修复测试脚本与测试用例中硬编码的本地绝对路径**
  - **目标文件清单**:
    - `Supplements/Experiments/RQ1/Scripts/Test/EcoreGenTL-TLBench-tests.py` (作者姓名 + OneDrive + Windows 绝对路径)
    - `Supplements/Experiments/RQ1/Scripts/Test/EcoreGenTL-JavaBench-tests.py` (Line 62: `E:\eclipse-dsl\plugins` Windows 绝对路径)
    - `Supplements/Experiments/Benchmarks/TLBench/OPMS/testcode_Baseline/CR1_TotalBudgetofAllProjectsTest.java` (Line 140: 编译器警告附带 `D:\eclipse-workshop\...`)
    - `Supplements/Experiments/Benchmarks/TLBench/OPMS/testcode_Baseline/CR4_FundingGroupTypeTest.java` (Line 140: 编译器警告附带 `D:\eclipse-workshop\...`)
  - **待修改项 (EcoreGenTL-TLBench-tests.py)**:
    - Line 86: 清除 `r"C:\Users\Zeqing\OneDrive\2025-USTB\iecoregen\result-RQ2\E-Workspace"`
    - Line 89: 清除 `r"C:\Users\Zeqing\OneDrive\2025-USTB\iecoregen\lib"`
    - Line 94-96: 清除 `r"E:\eclipse-dsl\plugins\..."` 硬编码 Windows 盘符路径
    - Line 702: 清除 `r"C:\Users\Zeqing\OneDrive\2025-USTB\iecoregen\Codebase_Python\reports"`
  - **重构方案**:
    - 改为相对路径或通过环境变量配置：
      ```python
      WORKSPACE = Path(os.environ.get("IECOREGEN_E_WORKSPACE", Path(__file__).resolve().parents[3] / "result-RQ2" / "E-Workspace"))
      LIB_DIR = Path(os.environ.get("IECOREGEN_LIB_DIR", Path(__file__).resolve().parents[4] / "lib"))
      ```
  - **验收标准**: 4 个文件内无任何 `Zeqing`、`OneDrive`、`C:\`、`D:\`、`E:\` 字符串，且在缺失环境变量时有合理的相对路径 fallback。

- [ ] **1.2 机构标识 `edu.ustb` / `ustb.edu.cn` 评估与匿名化方案落地**
  - **背景**: `http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen` 被作为 Ecore 注解的 source URI，广泛存在于 Ecore 模型和生成的 Java 包注释中（共 10,032 处）。
  - **决策事项**:
    - [ ] 方案 A：批量统一替换为匿名 URI，例如 `http://www.example.org/sei/mde/EnhancedCodeGen` 或 `http://anonymous.org/mde/EnhancedCodeGen`。
    - [ ] 方案 B：评估是否会破坏 Xtext/EMF 代码生成器的内部 URI 匹配。若生成器强依赖该字符串，需在源码生成器与模型中同步替换，或在文档中说明这是工具框架的既有 Schema URI。
  - **验收标准**: 明确方案决策，若采用方案 A，全库执行无死角替换并不破坏测试与构建。

---

### Phase 2: 【P1 极高危】打包工具与 Showcase 构建物清理 (Showcase & Jar)
- [ ] **2.1 检查并排查 Showcase 目录**
  - **目录**: `Supplements/EcoreGenTLShowcase/`
  - **任务项**:
    - 检查 `model/Library.ecore`, `Prompt_Templates.md`, `README.md`, `src/GenLibrary.mwe2`, `conf.properties`。
    - 确保 Showcase 内所有配置文件、提示词模板及示例文档 100% 英文，无中文注释及敏感路径。
- [ ] **2.2 若提供二进制 Jar (`EcoreGenTL.jar`)，清理内部源码及敏感信息**
  - **目标**: 针对打包在 Jar 包内的源码文件清理中文注释：
    - `edu/ustb/sei/ai/LLMPort.xtend`:
      - Line 55: `// 匹配 ````json ... ```` 或 ```` ... ```` 格式` -> 翻译为英文
      - Line 58: `// 获取第一个捕获组...` -> 翻译为英文
      - Line 65: `// 如果没有匹配到代码块，返回原字符串` -> 翻译为英文
    - `edu/ustb/sei/mde/eecg/facade/IEcoreGenCLI.xtend`:
      - Line 20, 54: `// 设置为必需参数` -> 翻译为英文
  - **打包重构建议**: 重新构建 Jar 包，建议仅包含编译生成的 `.class` 字节码及必要 resource，剔除 `.xtend` 源码文件，避免逆向或解压泄露。

---

### Phase 3: 【P2 高危】基准数据集定义标准化 (Benchmarks - 34 个文件)
> **说明**: 基准文件是学术审稿人评估研究可信度、方法有效性（Construct Validity）的核心。任何中文提示词或注释都会引发“是否来自中文数据集”、“LLM理解偏差”等质疑。

#### 3.1 JavaBench 模型与文档 (2 个关键文件)
- [ ] **3.1.1 清理 `PA20.ecore` 操作体中的中文注释**
  - **文件**: `Supplements/Experiments/Benchmarks/JavaBench/PA20/PA20.ecore`
  - **具体位置与替换内容**:
    - Line 115: `// EMF 中 players 被映射为 EList，直接操作集合` -> `// In EMF, players is mapped to EList; manipulate the collection directly`
    - Line 119: `// 初始化二维数组，使用 EDataType 中定义的 instanceClassName (edu.pa20.Piece)` -> `// Initialize 2D array using instanceClassName defined in EDataType (edu.pa20.Piece)`
    - Line 125: `// 使用 EMF Factory 实例化 Place` -> `// Instantiate Place using EMF Factory`
    - Line 130: `// 使用 getPlayers().get(index) 访问 EList 中的元素` -> `// Access elements in EList via getPlayers().get(index)`
    - Line 130: `// 使用 getX(), getY() 和 getSize() 获取属性` -> `// Retrieve attributes using getX(), getY(), and getSize()`
    - Line 130: `// EMF 默认的 equals 往往是引用比较，保险起见采用坐标值比对` -> `// Default equals in EMF often checks reference equality; compare coordinates for safety`
    - Line 130: `// 获取二维数组并赋值` -> `// Retrieve 2D array and assign value`
    - Line 155: `// 使用 EMF 原生的复制机制作为基础` -> `// Use native EMF copy mechanism as foundation`
    - Line 155: `// 1. players 是非 containment 引用...` -> `// 1. players is non-containment reference; maintain reference semantics during clone`
    - Line 155: `// 2. 深拷贝 initialBoard (PieceArray2D)...` -> `// 2. Deep copy initialBoard (PieceArray2D); allocate new array to decouple references`
    - Line 155: `// 浅拷贝 Piece 引用即可（符合原逻辑）` -> `// Shallow copy Piece references (consistent with original logic)`
    - Line 155: `// 3. centralPlace 处理说明...` -> `// 3. centralPlace note: containment="true" automatically handles deep copy via EcoreUtil.copy()`
- [ ] **3.1.2 修复 `PA22.plantuml` 全角冒号**
  - **文件**: `Supplements/Experiments/Benchmarks/JavaBench/PA22/PA22.plantuml`
  - **修改项**: Line 147 `+ getMaxWidth() ：int` -> 替换为英文半角 `+ getMaxWidth() : int`

#### 3.2 TLBench 模型与设计文档 (5 个关键文件)
- [ ] **3.2.1 修复 `OPMS/project.ecore` 全角冒号**
  - **文件**: `Supplements/Experiments/Benchmarks/TLBench/OPMS/project.ecore`
  - **修改项**: Line 5 `functional requirement：` -> 替换为英文半角 `functional requirement:`
- [ ] **3.2.2 修复 `OPRS/conference.ecore` 全角冒号**
  - **文件**: `Supplements/Experiments/Benchmarks/TLBench/OPRS/conference.ecore`
  - **修改项**: Line 5 `functional requirement：` -> 替换为英文半角 `functional requirement:`
- [ ] **3.2.3 替换 `OPMS/UML_DESIGNED_level3.md` 全角逗号**
  - **文件**: `Supplements/Experiments/Benchmarks/TLBench/OPMS/UML_DESIGNED_level3.md`
  - **修改项**: Line 12, 26, 50, 70, 86, 102 等 `//getter，setter` -> 替换为 `//getter, setter`
- [ ] **3.2.4 替换 `R144_AirlineFlights/UML_DESIGNED_level3.md` 全角逗号**
  - **文件**: `Supplements/Experiments/Benchmarks/TLBench/R144_AirlineFlights/UML_DESIGNED_level3.md`
  - **修改项**: Line 54, 73 等 `//getter，setter` -> 替换为 `//getter, setter`
- [ ] **3.2.5 遍历排查 TLBench 其余子领域文档**
  - 确认 OLRS, ORS, R2, R123, R12, R132, R22 等目录下无遗漏的全角标点与中文字符。

---

### Phase 4: 【P3 高危】基准测试用例代码清理与英文规范化 (Test Code - 27 个测试文件)
> **说明**: 审稿人在复现验证时极大概率运行或阅读测试用例代码。中文注释会导致非 UTF-8 环境乱码甚至控制台报错。

#### 4.1 JavaBench 测试套件 (18 个测试文件)
- [ ] **4.1.1 清理 `PA20` 测试用例**
  - **文件**: `Supplements/Experiments/Benchmarks/JavaBench/PA20/testcode_EcoreGenTL/ConsolePlayerTests.java`
  - **清理项**: `// 当前字符串结束，切换到下一个` -> `// Current string ended; switch to next`, `// 递归读取下一个字符串` -> `// Recursively read next string`
- [ ] **4.1.2 批量规范化 `PA21` 17 个测试文件（共 88 行中文注释）**
  - **目标目录**: `Supplements/Experiments/Benchmarks/JavaBench/PA21/testcode_EcoreGenTL/`
  - **文件列表**:
    - [ ] `DirectionTest.java`
    - [ ] `EntityCellTest.java`
    - [ ] `EntityTest.java`
    - [ ] `ExtraLifeTest.java`
    - [ ] `GameBoardControllerTest.java`
    - [ ] `GameBoardTest.java`
    - [ ] `GameControllerTest.java`
    - [ ] `GameStateSerializerTest.java`
    - [ ] `GameStateTest.java`
    - [ ] `GemTest.java`
    - [ ] `MineTest.java`
    - [ ] `MoveResultTest.java`
    - [ ] `MoveStackTest.java`
    - [ ] `PlayerTest.java`
    - [ ] `PositionOffsetTest.java`
    - [ ] `PositionTest.java`
    - [ ] `StopCellTest.java`
  - **典型替换范例**:
    - `// 辅助函数：创建Position对象` -> `// Helper method: create Position object`
    - `// EMF生成的类使用protected构造函数` -> `// EMF generated classes use protected constructors`
    - `// 验证初始状态` -> `// Verify initial state`

#### 4.2 TLBench 测试套件 (9 个测试文件)
- [ ] **4.2.1 清理 OPMS 测试用例中的中文 javac 编译器警告**
  - **文件**:
    - `Supplements/Experiments/Benchmarks/TLBench/OPMS/testcode_Baseline/CR1_TotalBudgetofAllProjectsTest.java`
    - `Supplements/Experiments/Benchmarks/TLBench/OPMS/testcode_Baseline/CR4_FundingGroupTypeTest.java`
  - **修改项**: 清除代码头部粘贴的中文编译器警告（`* 注: ... 使用或覆盖了已过时的 API`），替换为英文注释或直接移除。
- [ ] **4.2.2 清理 OPRS 测试用例**
  - **文件**: `Supplements/Experiments/Benchmarks/TLBench/OPRS/testcode_Baseline/CR2_PaperConsensusTest.java`
  - **修改项**: `// 有未提交` -> `// Uncommitted`, `// 不一致` -> `// Inconsistent`
- [ ] **4.2.3 清理 R12_RentedCarGalleryManagementSystem 测试用例**
  - **文件**:
    - `Supplements/Experiments/Benchmarks/TLBench/R12_RentedCarGalleryManagementSystem/testcode_Baseline/CR2_RevenueCalculationTest.java`
    - `Supplements/Experiments/Benchmarks/TLBench/R12_RentedCarGalleryManagementSystem/testcode_Baseline/CR3_OverdueRentalsTest.java`
  - **修改项**:
    - `// ① Toyota Camry, 100 CNY × 3 天 = 300` -> `// 1. Toyota Camry, 100 USD x 3 days = 300` (移除 CNY 或改为通用货币/USD，中文“天”改 days)
    - `// 未到期` -> `// Not due`
    - `// 两条逾期` -> `// Two overdue entries`
    - `// 已归还` -> `// Returned`
- [ ] **4.2.4 清理 R144_AirlineFlights 测试用例**
  - **文件**:
    - `Supplements/Experiments/Benchmarks/TLBench/R144_AirlineFlights/testcode_Baseline/CR1_PublishFlightTest.java`
    - `Supplements/Experiments/Benchmarks/TLBench/R144_AirlineFlights/testcode_Baseline/CR4_CloseFlightTest.java`
    - `Supplements/Experiments/Benchmarks/TLBench/R144_AirlineFlights/testcode_Baseline/CR5_FlightReservationTest.java`
  - **修改项**: `// Helper methods (A模式)` -> `// Helper methods (Mode A)`
- [ ] **4.2.5 清理 R22_IPOApplication 测试用例**
  - **文件**:
    - `Supplements/Experiments/Benchmarks/TLBench/R22_IPOApplication/testcode_EcoreGenTL/CR2Test.java`
    - `Supplements/Experiments/Benchmarks/TLBench/R22_IPOApplication/testcode_EcoreGenTL/CR3Test.java`
  - **修改项**: `// 初始 eligible` -> `// Initially eligible`, `// eligible 才能创建申请（CR1约束）` -> `// Eligible required to create application (CR1 constraint)`

---

### Phase 5: 【P4 中高危】实验复现脚本清理 (Reproduction Scripts - 6 个文件)
> **目标目录**: `Supplements/Experiments/RQ1/Scripts/CodeGen/EcoreGenTL/`

- [ ] **5.1 清理 6 个 `.mwe2` 代码生成脚本中的中文注释**
  - [ ] `R123_School.mwe2`
  - [ ] `R12_RentedCarGalleryManagementSystem.mwe2`
  - [ ] `R132_MunicipalLibrary.mwe2`
  - [ ] `R144_AirlineFlights.mwe2`
  - [ ] `R22_IPOApplication.mwe2`
  - [ ] `R2_EmployeeManagementSystem.mwe2`
  - **具体修改内容**:
    - 将 `// 工作区根目录` 替换为 `// Workspace root directory`
    - 将 `//原来是edu.company` 替换为 `// Formerly edu.company` 或说明注释

---

### Phase 6: 【P5 中危】大模型生成原始输出批量清洗 (Raw Results - 953 个文件)
> **现状说明**:
> - 涉及文件数：953 个 Java 生成文件（共 6,965 行中文）。
> - 分布分布：`RQ1/Raw Results` (317 个), `RQ2/Raw Results` (636 个)。
> - 集中领域：PA20 (388), PA21 (251), PA22 (161), PA19 (153)。
> - 产生根因：部分国内开源基模（如 DeepSeek 等）在补全 Java 类方法时自发生成了中文注释（如 `// 1. 实例化 Helper`、`// 5. 直接使用 EMF 的 Coordinate` 等）。

- [ ] **6.1 策略与安全性验证 (Dry-Run)**
  - 编写专用清洗脚本 `scripts/sanitize_raw_results.py`。
  - 处理规则：
    - 仅过滤/替换单行注释 `// ...` 及块级注释 `/* ... */` 中的中文字符或翻译为对应通用英文。
    - 严禁触碰任何代码逻辑、字符串字面量、控制流或变量声明。
  - 选取 5 个代表性文件进行 Dry-Run，diff 比对确认无任何代码结构损坏。
- [ ] **6.2 批量执行清洗处理**
  - 对 `RQ1/Raw Results` (317 个文件) 执行批量清洗。
  - 对 `RQ2/Raw Results` (636 个文件) 执行批量清洗。
- [ ] **6.3 语法合法性检验 (Syntax Validation)**
  - 抽样（或全量）校验清洗后的 Java 文件语法合法性，确保无破坏性变更。

---

### Phase 7: 【P6 必选闭环】全库终验扫描与可复现性收尾 (Verification & Audit)
- [ ] **7.1 全库终验扫描：中文与全角标点归零检验**
  - 运行 `python3 scripts/scan_chinese.py Supplements/`
  - **验收指标**:
    - `Benchmarks/`: 0 处命中
    - `Scripts/`: 0 处命中
    - `EcoreGenTLShowcase/`: 0 处命中
    - `Raw Results/`: 0 处命中 (或仅允许白名单)
    - 全库除本说明文档/历史记录外，中文字符与全角标点命中数为 **0**。
- [ ] **7.2 全库终验扫描：双盲匿名性归零检验**
  - 运行 `python3 scripts/check_anonymity.py Supplements/`
  - **验收指标**:
    - 无任何作者名拼音 `Zeqing` 残留（0 处）。
    - 无任何未授权机构标识或本地磁盘绝对路径残留（0 处）。
- [ ] **7.3 抽样测试复现验证 (Smoke Test)**
  - 运行修改后的 `EcoreGenTL-TLBench-tests.py`，验证脚本在脱离本地绝对路径后依然能通过相对路径或环境变量被正常加载与执行。
- [ ] **7.4 生成最终清理报告与交付说明**
  - 在根目录下输出 `CLEAN_REPORT.md` 或在 `task.md` 下方更新终验日志。
  - 确认 git status 变更清晰、干净，无临时生成的 scratch 脚本或垃圾缓存文件被误提交。

---

## 🛠 常用命令与检查工具集 (Quick Reference)

### 1. 快速检查全库中文
```bash
# 统计含有中文字符或全角标点的文件数量（排除本文档）
python3 -c "
import os, re
pattern = re.compile(r'[\u4e00-\u9fff]|[\uff01-\uff5e]|[\u3000-\u303f]')
matches = []
for root, dirs, files in os.walk('Supplements'):
    for f in files:
        if f in ['task.md', '检查中文回复建议']: continue
        p = os.path.join(root, f)
        try:
            with open(p, 'r', encoding='utf-8', errors='ignore') as fp:
                if pattern.search(fp.read()):
                    matches.append(p)
        except Exception: pass
print(f'Total matching files: {len(matches)}')
"
```

### 2. 检查双盲敏感词 (Zeqing / 本地绝对路径)
```bash
grep -rn -i "zeqing" Supplements/
grep -rn -i "OneDrive" Supplements/
grep -rn "C:\\Users\\" Supplements/
```

---

## 📝 执行历史与变更日志 (Execution Log)

| 日期时间 (Local) | 操作人 / Agent | 完成任务项 | 简要记录与验证结果 |
| :--- | :---: | :---: | :--- |
| 2026-09-23 13:26 | Agent | Phase 0 (0.1, 0.2, 0.3) | 固化扫描工具 `scripts/scan_chinese.py` 与 `scripts/check_anonymity.py`。完成全库基线扫描并输出报告：全库含中文文件 993 个（共 7,105 行）；作者姓名泄密 1 个文件；本地绝对路径泄密 4 个文件；机构 URI 注解涉及 10,031 个文件。全流程无修改任何已有业务脚本。 |

*(执行任务时请在此追加记录，保持全程可追溯)*
