# Task D - JavaBench Review Progress

## 1. Overview

> Phase 1: PA19/PA20/PA21/PA22 Review Results

| ID | benchmark | system | model | sample | Report Status | Failed Test Cases | Extracted Failed Cases | Root Cause Analysis Done | Error Type | Review Notes |
|---|---|---|---|---|---|---|---|---|---|---|
| PA19001 | JavaBench | PA19 | qwen3.6-flash | sample2 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding | 3 javac errors |
| PA20001 | JavaBench | PA20 | qwen3.6-flash | sample1 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding | 24 javac errors |
| PA21001 | JavaBench | PA21 | gpt-5.4-mini | sample1 | Compile Failure | N/A |  | Yes | EMF Model Type Hierarchy Error | 3 javac errors |
| PA21003 | JavaBench | PA21 | gemini-3.1-flash-lite | sample3 | Compile Failure | N/A |  | Yes | Unfamiliar with EMF Model API | 3 javac errors |
| PA21004 | JavaBench | PA21 | qwen3.6-flash | sample1 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding | 15 javac errors |
| PA21005 | JavaBench | PA21 | qwen3.6-flash | sample3 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding | 2 javac errors |
| PA21006 | JavaBench | PA21 | qwen3.6-flash | sample4 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding | 1 javac error |
| PA22001 | JavaBench | PA22 | gemini-3.1-flash-lite | sample3 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding+Missing import | 10 javac errors |
| PA22002 | JavaBench | PA22 | gemini-3.1-flash-lite | sample4 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding+Missing import | 8 javac errors |
| PA22003 | JavaBench | PA22 | gemini-3.1-flash-lite | sample5 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding+Missing import | 7 javac errors |
| PA22004 | JavaBench | PA22 | qwen3.6-flash | sample3 | Compile Failure | N/A |  | Yes | LLM invented classes and methods | 10 javac errors |
| PA22005 | JavaBench | PA22 | qwen3.6-flash | sample4 | Compile Failure | N/A |  | Yes | EMF API Misunderstanding | 4 javac errors |

## 2. Compile Failure Section

> Compile failure samples organized by testcase. If an ID has multiple failed test cases, split into separate testcase blocks with `Test Assertion`, `Test Case`, `Functional Code`, `Root Cause Analysis`, `Error Type`.

### PA19001

- Sample: `JavaBench/PA19/qwen3.6-flash/sample2`
- Status: `Compile Failure`
- Compilation Errors:
  1. `Game.java:416` - Constructor Coordinate in class Coordinate cannot be applied to given types; required: no arguments; found: int,int
  2. `Game.java:419` - Cannot reference non-static method createFillableCell(Coordinate,Pipe) from static context
  3. `Game.java:477` - Cannot find symbol isEmpty()
- Functional Code: `E-Workspace/PA19/qwen3.6-flash/sample2/pa19/Game.java`
- Root Cause Analysis:
  1. **Coordinate constructor error**: `new Coordinate(row, colIdx)` — EMF's Coordinate class only has a no-argument constructor; LLM incorrectly assumed a parameterized constructor exists. Correct approach: use `Pa19Factory.eINSTANCE.createCoordinate()` then `setRow()/setCol()`
  2. **Static context referencing non-static method**: `Pa19Helper.createFillableCell(coord, pipe)` — `createFillableCell` is an instance method, but LLM called it as static
  3. **Cannot find isEmpty()**: `getCellStack().isEmpty()` — `getCellStack()` returns `CellStack` type; its `isEmpty()` actually exists on the `getStack()` EList. Correct approach: `getCellStack().getStack().isEmpty()`
- Error Type: `EMF API Misunderstanding`

### PA20001

- Sample: `JavaBench/PA20/qwen3.6-flash/sample1`
- Status: `Compile Failure`
- Compilation Error Summary: 24 javac errors total, involving:
  - `ConsolePlayer.java:10` - Cannot find symbol EcoreUtil
  - `Piece.java:167,170,181,184,187` - Multiple rule validation methods called as static
  - `Archer.java:95-107` - Multiple type mismatches and missing rule methods
  - `Knight.java:98-113` - Multiple rule check methods missing
- Functional Code: `E-Workspace/PA20/qwen3.6-flash/sample1/pa20/`
- Root Cause Analysis:
  1. **Wrong package import**: `org.eclipse.emf.ecore.EcoreUtil` does not exist; correct is `org.eclipse.emf.ecore.util.EcoreUtil`
  2. **EMF instance methods called as static**: `VacantRule.validate()`, `OutOfBoundaryRule.validate()`, `NilMoveRule.validate()`, `OccupiedRule.validate()`, `FirstNMovesProtectionRule.validate()`, `KnightMoveRule.check()`, `KnightBlockRule.check()` etc. are all instance methods, but LLM incorrectly called them as `ClassName.method()` static
  3. **Invented methods**: `Piece.generateGeometricCandidates()`, `Piece.validatePieceSpecificRules()` methods do not exist
  4. **Type mismatch**: `game.getBoard()[x][y]` returns `Piece[][]`, cannot assign to `Place` type; `Move` only has no-argument constructor, but LLM assumed `Move(int,int,int,int)` parameterized constructor
  5. **Wrong method call**: LLM assumed `Place` has `getPiece()` method, but it does not exist
- Error Type: `EMF API Misunderstanding`

### PA21001

- Sample: `JavaBench/PA21/gpt-5.4-mini/sample1`
- Status: `Compile Failure`
- Compilation Error Summary: 3 javac errors
  - `GameBoardController.java:161` - Player cannot be converted to Cell
  - `GameBoardController.java:215` - Cell cannot be converted to Mine
  - `GameBoardController.java:305` - Player cannot be converted to Cell
- Functional Code: `E-Workspace/PA21/gpt-5.4-mini/sample1/pa21/`
- Root Cause Analysis:
  1. `board.getPlayer()` returns `Player` type, LLM incorrectly assigns to `Cell` type variable — `Player` and `Cell` are both subtypes of `BoardElement` but are not in a parent-child relationship
  2. `board.getCell2()` returns `Cell`, LLM attempts to cast `Cell` directly to `Mine` (both are sibling classes in the EMF model `Mine extends EntityCell extends Cell`, no direct inheritance relationship)
  - Error Type: `EMF Model Type Hierarchy Error` (The cause is more like insufficient context information; Log21272 also lists all code, then navigation results include + GameBoard, + MoveResult, then getcodesummary gets GameBoard, MoveResult, Alive, Dead, Invalid (all subtypes of MoveResult), as well as Position, PositionOffset, Gem, ExtraLife, Mine, StopCell, Wall, Player — but did not obtain Mine's parent classes EntityCell and Cell, so Player-Cell relationship was unclear, causing hallucination)

### PA21002

- Sample: `JavaBench/PA21/minimax-m3/sample5`
- Status: `Compile Failure`
- Compilation Error Summary: 47 javac errors, involving a large number of symbol not found errors in GameStateSerializer and InertiaTextGame
- Functional Code: `E-Workspace/PA21/minimax-m3/sample5/pa21/`
- Root Cause Analysis: This sample only generated 2 Java files (`GameStateSerializer.java` and `InertiaTextGame.java`), missing all EMF model classes (`GameState`, `Cell`, `Position`, `GameBoard`, `Entity`, `Wall`, `EntityCell`, `Pa21Factory` etc.). LLM did not generate EMF model class files, only auxiliary classes.
- Error Type: `LLM did not generate model classes`

### PA21003

- Sample: `JavaBench/PA21/gemini-3.1-flash-lite/sample3`
- Status: `Compile Failure`
- Compilation Error Summary: 3 javac errors
  - `GameBoardController.java:145` - Player.getPosition() method not found
  - `GameBoardController.java:165` - Cell cannot be converted to Mine
  - `GameBoardController.java:201` - Player.setPosition() method not found
- Functional Code: `E-Workspace/PA21/gemini-3.1-flash-lite/sample3/pa21/`
- Root Cause Analysis:
  1. `Player` class does not have `getPosition()`/`setPosition()` methods — LLM incorrectly assumed Player has position attributes; in reality, Player is positioned through references in `GameBoard`
  2. `Cell` cannot be cast to `Mine` — Same type hierarchy error as PA21001
- Error Type: `Unfamiliar with EMF Model API`

### PA21004

- Sample: `JavaBench/PA21/qwen3.6-flash/sample1`
- Status: `Compile Failure`
- Compilation Error Summary: 15 javac errors, mainly involving Position constructor error and EntityCell.getentity() method not found
- Functional Code: `E-Workspace/PA21/qwen3.6-flash/sample1/pa21/`
- Root Cause Analysis:
  1. **Position constructor error**: EMF's Position class only has no-argument constructor; LLM incorrectly used `new Position(int, int)` parameterized constructor. Code: `new Position(nextPos.getX() + ..., nextPos.getY() + ...)`
  2. **Cannot find getentity()**: Should be `getEntity()` (capital E), method name case error
  3. **Cannot find getLives()/setLives() methods**: `GameBoard` does not have these methods
  4. **Cannot find isUnlimitedLives() method**: `GameState` does not have this method
- Error Type: `EMF API Misunderstanding`

### PA21005

- Sample: `JavaBench/PA21/qwen3.6-flash/sample3`
- Status: `Compile Failure`
- Compilation Error Summary: 2 javac errors
  - `GameBoardController.java:159` - Position constructor error
  - `GameState.java:743` - GameBoardView constructor error
- Functional Code: `E-Workspace/PA21/qwen3.6-flash/sample3/pa21/`
- Root Cause Analysis: Same as PA21004; EMF's Position and GameBoardView classes only have no-argument constructors; LLM incorrectly used parameterized constructors
- Error Type: `EMF API Misunderstanding`

### PA21006

- Sample: `JavaBench/PA21/qwen3.6-flash/sample4`
- Status: `Compile Failure`
- Compilation Error Summary: 1 javac error
  - `GameBoard.java:292` - Cannot infer type arguments for BasicEList<>
- Functional Code: `E-Workspace/PA21/qwen3.6-flash/sample4/pa21/`
- Root Cause Analysis: `new BasicEList<>(board[row])` cannot infer generic type parameters; should explicitly write `new BasicEList<Cell>(board[row])` or use factory method
- Error Type: `EMF API Misunderstanding`

### PA22001

- Sample: `JavaBench/PA22/gemini-3.1-flash-lite/sample3`
- Status: `Compile Failure`
- Compilation Error Summary: 10 javac errors, mainly involving Position constructor error, toString() return type error, InputEngine type cast error, HashMap/List/Collections not found
- Functional Code: `E-Workspace/PA22/gemini-3.1-flash-lite/sample3/pa22/`
- Root Cause Analysis:
  1. **Position constructor error**: `new Position(int, int)` — EMF's Position class only has no-argument constructor
  2. **toString() return type error**: `GameStateTransition`'s `toString()` method declared as `void` but actually returns `String`; LLM incorrectly overrode the parent's `String toString()`
  3. **Type cast error**: `InputEngine`/`RenderingEngine` cannot be converted to `InternalEObject` — LLM misunderstood EMF's proxy/reference mechanism. InputEngine and RenderingEngine are interfaces that extend EObject (not InternalEObject). EObject and InternalEObject are sibling interfaces with no inheritance relationship. The correct approach should use `(InternalEObject)(EObject)inputEngine` for double-casting, or change the field type to InternalEObject
  4. **Missing imports**: Missing imports for `List`, `HashMap`, `Collections`
- Error Type: `EMF API Misunderstanding+Missing import`

### PA22002

- Sample: `JavaBench/PA22/gemini-3.1-flash-lite/sample4`
- Status: `Compile Failure`
- Compilation Error Summary: 8 javac errors, mainly involving InputEngine type cast error, ActionResult to String cast error, HashMap/List/Collections not found
- Functional Code: `E-Workspace/PA22/gemini-3.1-flash-lite/sample4/pa22/`
- Root Cause Analysis: Same as PA22001; additionally, `ActionResult` cannot be converted to `String` (should call `getMessage()` instead of direct casting). Code: `re.message(result);` (line 215), where result is of type ActionResult. Root cause: `RenderingEngine.message(String)` accepts a String parameter, but LLM directly passed an ActionResult object. Correct approach: first check if `result instanceof Failed`, then call `((Failed) result).getReason()` to get the string (sample3/sample5 both use the correct approach)
- Error Type: `EMF API Misunderstanding+Missing import`

### PA22003

- Sample: `JavaBench/PA22/gemini-3.1-flash-lite/sample5`
- Status: `Compile Failure`
- Compilation Error Summary: 7 javac errors, mainly involving InputEngine type cast error, HashMap/List/Collections not found
- Functional Code: `E-Workspace/PA22/gemini-3.1-flash-lite/sample5/pa22/`
- Root Cause Analysis: Same as PA22001's missing import issue (HashMap/List/Collections), InputEngine type cast error
- Error Type: `EMF API Misunderstanding+Missing import`

### PA22004

- Sample: `JavaBench/PA22/qwen3.6-flash/sample3`
- Status: `Compile Failure`
- Compilation Error Summary: 10 javac errors, mainly involving Move.getPosition1/getPosition2 methods not found, MoveUp/MoveLeft/MoveDown/MoveRight classes not found
- Functional Code: `E-Workspace/PA22/qwen3.6-flash/sample3/pa22/`
- Root Cause Analysis:
  1. **Invented methods**: `Move` class does not have `getPosition1()`/`getPosition2()` methods
  2. **Invented classes**: `MoveUp`, `MoveLeft`, `MoveDown`, `MoveRight` classes do not exist in the EMF model
- Error Type: `LLM invented classes and methods`

### PA22005

- Sample: `JavaBench/PA22/qwen3.6-flash/sample4`
- Status: `Compile Failure`
- Compilation Error Summary: 4 javac errors, involving InputEngine/RenderingEngine type cast error, ActionResult.getMessage() not found, Pa22Package.createPosition() not found
- Functional Code: `E-Workspace/PA22/qwen3.6-flash/sample4/pa22/`
- Root Cause Analysis:
  1. **EMF API Misunderstanding**: `InputEngine`/`RenderingEngine` cannot be cast to `InternalEObject`; `Pa22Package.eINSTANCE` should use `Pa22Factory.eINSTANCE` to create objects
  2. **Method does not exist**: `ActionResult` type does not have `getMessage()` method
- Error Type: `EMF API Misunderstanding`

## 3. Test Case Failure List
Test Case Failure Overview Table
| ID | benchmark | system | model | sample | Report Status | Failed Test Cases | Extracted Failed Cases | Root Cause Analysis Done | Review Notes |
|---|---|---|---|---|---|---|---|---|---|
| PA19003 | JavaBench | PA19 | deepseek-v4-flash | sample3 | Test Failure | 2 | Yes | Yes |  |
| PA19004 | JavaBench | PA19 | deepseek-v4-flash | sample4 | Test Failure | 4 | Yes | Yes |  |
| PA19005 | JavaBench | PA19 | deepseek-v4-flash | sample5 | Test Failure | 7 | Yes | Yes |  |
| PA19006 | JavaBench | PA19 | gemini-3.1-flash-lite | sample1 | Test Failure | 4 | Yes | Yes |  |
| PA19007 | JavaBench | PA19 | gemini-3.1-flash-lite | sample2 | Test Failure | 3 | Yes | Yes |  |
| PA19008 | JavaBench | PA19 | gemini-3.1-flash-lite | sample3 | Test Failure | 6 | Yes | Yes |  |
| PA19009 | JavaBench | PA19 | gemini-3.1-flash-lite | sample4 | Test Failure | 4 | Yes | Yes |  |
| PA19010 | JavaBench | PA19 | gemini-3.1-flash-lite | sample5 | Test Failure | 6 | Yes | Yes |  |
| PA19011 | JavaBench | PA19 | minimax-m3 | sample1 | Test Failure | 3 | Yes | Yes |  |
| PA19012 | JavaBench | PA19 | minimax-m3 | sample4 | Test Failure | 1 | Yes | Yes |  |
| PA19013 | JavaBench | PA19 | minimax-m3 | sample5 | Test Failure | 5 | Yes | Yes |  |
| PA19014 | JavaBench | PA19 | gpt-5.4-mini | sample1 | Test Failure | 6 | Yes | Yes |  |
| PA19015 | JavaBench | PA19 | gpt-5.4-mini | sample2 | Test Failure | 2 | Yes | Yes |  |
| PA19016 | JavaBench | PA19 | gpt-5.4-mini | sample3 | Test Failure | 6 | Yes | Yes |  |
| PA19017 | JavaBench | PA19 | gpt-5.4-mini | sample4 | Test Failure | 2 | Yes | Yes |  |
| PA19018 | JavaBench | PA19 | gpt-5.4-mini | sample5 | Test Failure | 8 | Yes | Yes |  |
| PA19019 | JavaBench | PA19 | qwen3.6-flash | sample1 | Test Failure | 6 | Yes | Yes |  |
| PA19020 | JavaBench | PA19 | qwen3.6-flash | sample3 | Test Failure | 5 | Yes | Yes |  |
| PA19021 | JavaBench | PA19 | qwen3.6-flash | sample4 | Test Failure | 13 | Yes | Yes |  |
| PA19022 | JavaBench | PA19 | qwen3.6-flash | sample5 | Test Failure | 8 | Yes | Yes |  |
| PA20002 | JavaBench | PA20 | deepseek-v4-flash | sample1 | Test Failure | 2 | Yes | Yes |  |
| PA20003 | JavaBench | PA20 | deepseek-v4-flash | sample2 | Test Failure | 11 | Yes | Yes |  |
| PA20004 | JavaBench | PA20 | deepseek-v4-flash | sample3 | Test Failure | 4 | Yes | Yes |  |
| PA20005 | JavaBench | PA20 | deepseek-v4-flash | sample4 | Test Failure | 8 | Yes | Yes |  |
| PA20006 | JavaBench | PA20 | deepseek-v4-flash | sample5 | Test Failure | 9 | Yes | Yes |  |
| PA20007 | JavaBench | PA20 | gemini-3.1-flash-lite | sample1 | Test Failure | 1 | Yes | Yes |  |
| PA20008 | JavaBench | PA20 | gemini-3.1-flash-lite | sample2 | Test Failure | 14 | Yes | Yes |  |
| PA20009 | JavaBench | PA20 | gemini-3.1-flash-lite | sample3 | Test Failure | 11 | Yes | Yes |  |
| PA20010 | JavaBench | PA20 | gemini-3.1-flash-lite | sample4 | Test Failure | 7 | Yes | Yes |  |
| PA20011 | JavaBench | PA20 | gpt-5.4-mini | sample1 | Test Failure | 17 | Yes | Yes | model-doc defect+code error | Main cause: ProtectionRule not configured (numProtectedMoves defaults to 5); Player == comparison |
| PA20012 | JavaBench | PA20 | gpt-5.4-mini | sample2 | Test Failure | 10 | Yes | Yes | model-doc defect+code error | Main cause: ProtectionRule not configured; getWinner missing source check |
| PA20013 | JavaBench | PA20 | gpt-5.4-mini | sample3 | Test Failure | 4 | Yes | Yes | model-doc defect+model-doc ambiguity | ProtectionRule not configured; getWinner "current player" ambiguous |
| PA20014 | JavaBench | PA20 | gpt-5.4-mini | sample4 | Test Failure | 19 | Yes | Yes | model-doc defect+code error | Knight missing ProtectionRule; getAvailableMoves source uses centralPlace |
| PA20015 | JavaBench | PA20 | gpt-5.4-mini | sample5 | Test Failure | 4 | Yes | Yes | model-doc defect | ProtectionRule not configured; Archer model-doc missing path scan algorithm |
| PA20016 | JavaBench | PA20 | minimax-m3 | sample3 | Test Failure | 4 | Yes | Yes |  |
| PA20017 | JavaBench | PA20 | minimax-m3 | sample4 | Test Failure | 4 | Yes | Yes |  |
| PA20018 | JavaBench | PA20 | minimax-m3 | sample5 | Test Failure | 6 | Yes | Yes |  |
| PA20019 | JavaBench | PA20 | minimax-m3 | sample2 | Test Failure | 1 | Yes | Yes |  |
| PA20020 | JavaBench | PA20 | minimax-m3 | sample1 | Test Failure | 17 | Yes |  |  |
| PA20021 | JavaBench | PA20 | qwen3.6-flash | sample2 | Test Failure | 11 | Yes |  |  |
| PA20022 | JavaBench | PA20 | qwen3.6-flash | sample3 | Test Failure | 23 | Yes |  |  |
| PA20023 | JavaBench | PA20 | qwen3.6-flash | sample4 | Test Failure | 16 | Yes |  |  |
| PA20024 | JavaBench | PA20 | qwen3.6-flash | sample5 | Test Failure | 9 | Yes |  |  |
| PA21007 | JavaBench | PA21 | deepseek-v4-flash | sample1 | Test Failure | 4 | Yes | Yes | code defect+model-doc defect | Invalid missing newPosition; Dead missing origPosition |
| PA21008 | JavaBench | PA21 | deepseek-v4-flash | sample2 | Test Failure | 1 | Yes | Yes | code defect+model-doc defect | Dead missing origPosition |
| PA21009 | JavaBench | PA21 | deepseek-v4-flash | sample4 | Test Failure | 3 | Yes | Yes | model-doc defect | Dead's origPosition specification incomplete |
| PA21010 | JavaBench | PA21 | deepseek-v4-flash | sample5 | Test Failure | 5 | Yes | Yes | code defect+model-doc defect | Invalid missing newPosition; Dead missing origPosition+newPosition |
| PA21011 | JavaBench | PA21 | gpt-5.4-mini | sample2 | Test Failure | 7 | Yes | Yes | model-doc defect+code error | StopCell stops ahead (model-doc correct); Alive missing origPosition (model-doc defect); undoMove reads wrong position |
| PA21012 | JavaBench | PA21 | gpt-5.4-mini | sample3 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | Invalid missing newPosition (model-doc defect); Alive missing origPosition; StopCell stops ahead |
| PA21013 | JavaBench | PA21 | gpt-5.4-mini | sample4 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | Invalid missing newPosition; Alive/Dead missing origPosition; undoMove did not move Player |
| PA21014 | JavaBench | PA21 | gpt-5.4-mini | sample5 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | Invalid missing newPosition; Alive missing origPosition; undoMove only changes Position not Cell |
| PA21015 | JavaBench | PA21 | minimax-m3 | sample1 | Test Failure | 11 | Yes | Yes | code error | No model-doc; Invalid missing newPosition; stops at Mine/Gem; new EntityCell detached from board |
| PA21016 | JavaBench | PA21 | minimax-m3 | sample2 | Test Failure | 5 | Yes | Yes | model-doc defect+code error | Invalid missing newPosition; Player not restored to original position after Dead |
| PA21017 | JavaBench | PA21 | minimax-m3 | sample5 | Test Failure | 4 | Yes | Yes | model-doc defect | Invalid missing newPosition (other logic correct) |
| PA21018 | JavaBench | PA21 | gemini-3.1-flash-lite | sample1 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | Invalid missing newPosition; Alive missing origPosition; undoMove did not clear Player |
| PA21019 | JavaBench | PA21 | gemini-3.1-flash-lite | sample2 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | Invalid missing newPosition; StopCell logic error; Gem not removed from board |
| PA21020 | JavaBench | PA21 | gemini-3.1-flash-lite | sample4 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | Invalid missing newPosition; Alive missing origPosition; undoMove did not clear Player |
| PA21021 | JavaBench | PA21 | gemini-3.1-flash-lite | sample5 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | Invalid missing newPosition; Alive missing origPosition; undoMove did not clear Player |
| PA21022 | JavaBench | PA21 | qwen3.6-flash | sample2 | Test Failure | 14 | Yes | Yes | model-doc defect+code error | undoMove did not clear Player; StopCell stops ahead; Dead missing newPosition; GameController cascade |
| PA21023 | JavaBench | PA21 | qwen3.6-flash | sample5 | Test Failure | 51 | Yes | Yes | model-doc defect+code error | MoveStack.moves not initialized; Invalid missing newPosition; GameController complete failure |
| PA22021 | JavaBench | PA22 | deepseek-v4-flash | sample1 | Test Failure | 4 | Yes | Yes | model-doc defect+code error | NPE on null player; gameLoop missing getUndoQuota; render char error; undo does not restore position |
| PA22022 | JavaBench | PA22 | deepseek-v4-flash | sample2 | Test Failure | 6 | Yes | Yes | model-doc defect+code error | checkpoint called every time instead of only on box push; undoQuota not checked; NPE null player |
| PA22023 | JavaBench | PA22 | deepseek-v4-flash | sample3 | Test Failure | 5 | Yes | Yes | model-doc defect+code error | checkpoint called every time; NPE null player; render char error; undo position error |
| PA22024 | JavaBench | PA22 | deepseek-v4-flash | sample4 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | checkpoint called every time; Player1 input parse error; gameLoop missing getUndoQuota; render line count error |
| PA22025 | JavaBench | PA22 | deepseek-v4-flash | sample5 | Test Failure | 7 | Yes | Yes | model-doc defect+code error | checkpoint called every time; undoQuota not checked; NPE null player; undo position error |
| PA22006 | JavaBench | PA22 | gemini-3.1-flash-lite | sample1 | Test Failure | 4 | Yes | Yes | model-doc defect+code error | NPE null player; gameLoop missing getUndoQuota; render char error; undo does not restore position |
| PA22007 | JavaBench | PA22 | gemini-3.1-flash-lite | sample2 | Test Failure | 5 | Yes | Yes | model-doc defect+code error | NPE null player; gameLoop missing getUndoQuota; pushBox insufficient calls; isWin error |
| PA22008 | JavaBench | PA22 | minimax-m3 | sample1 | Test Failure | 9 | Yes | Yes | model-doc defect+EMF knowledge error | history Stack not initialized; NPE null player; pushBox direction error; undo position error |
| PA22009 | JavaBench | PA22 | minimax-m3 | sample2 | Test Failure | 4 | Yes | Yes | model-doc defect+EMF knowledge error | history Stack not initialized; gameLoop missing getUndoQuota; render line count error; undo position error |
| PA22010 | JavaBench | PA22 | minimax-m3 | sample3 | Test Failure | 6 | Yes | Yes | model-doc defect+EMF knowledge error | history Stack not initialized; gameLoop/gameExit missing calls; undo position error |
| PA22011 | JavaBench | PA22 | minimax-m3 | sample4 | Test Failure | 11 | Yes | Yes | model-doc defect+EMF knowledge error | Transition.moves Map not initialized; NPE null player; pushBox insufficient calls; gameLoop missing getUndoQuota |
| PA22012 | JavaBench | PA22 | minimax-m3 | sample5 | Test Failure | 6 | Yes | Yes | model-doc defect+EMF knowledge error | history Stack not initialized; gameLoop missing getUndoQuota; render char error; undo position null |
| PA22013 | JavaBench | PA22 | gpt-5.4-mini | sample1 | Test Failure | 13 | Yes | Yes | model-doc defect+EMF knowledge error | Transition.moves Map not initialized; pushBox/HitAnotherPlayer logic error; gameLoop missing getUndoQuota |
| PA22014 | JavaBench | PA22 | gpt-5.4-mini | sample2 | Test Failure | 5 | Yes | Yes | model-doc defect+code error | gameLoop missing getUndoQuota; render char error (B vs a); undo position null |
| PA22015 | JavaBench | PA22 | gpt-5.4-mini | sample3 | Test Failure | 6 | Yes | Yes | model-doc defect+code error | checkpoint called every time; gameLoop/gameExit/gameWin missing calls; render line count error; undo position null |
| PA22016 | JavaBench | PA22 | gpt-5.4-mini | sample4 | Test Failure | 7 | Yes | Yes | model-doc defect+EMF knowledge error | history Stack not initialized; checkpoint called every time; render B vs a; gameLoop missing getUndoQuota |
| PA22017 | JavaBench | PA22 | gpt-5.4-mini | sample5 | Test Failure | 4 | Yes | Yes | model-doc defect+code error | NPE null player; gameLoop missing getUndoQuota; render B vs a; undo position null |
| PA22018 | JavaBench | PA22 | qwen3.6-flash | sample1 | Test Failure | 11 | Yes | Yes | model-doc defect+code error | checkpoint called every time; pushBox insufficient calls; hitWall logic error; render $ vs a |
| PA22019 | JavaBench | PA22 | qwen3.6-flash | sample2 | Test Failure | 9 | Yes | Yes | model-doc defect+EMF knowledge error | history Stack not initialized; pushBox excessive calls; NPE null player; render . vs space |
| PA22020 | JavaBench | PA22 | qwen3.6-flash | sample5 | Test Failure | 9 | Yes | Yes | model-doc defect+EMF knowledge error+code error | history Stack not initialized; NPE null player; Box incorrectly cast to Player; render $ vs a |

Failed Test Cases (format: ID: TestFile.TestMethod;)
- PA19003: FillableCellTest.givenCell_assertSingleCharRepresentation(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19004: CellStackTest.givenStack_whenPop_incUndoCount(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19005: GameTest.givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange(), GameTest.givenGame_ifUndoPipe_stepCountIncreases(), GameTest.givenGame_ifPipeCanBePlaced_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()
- PA19006: GameTest.givenGame_ifUndoPipe_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()
- PA19007: MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19008: CellStackTest.givenEmptyStack_ifPop_returnNull(), CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange(); GameTest.givenGame_ifUndoPipe_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19009: GameTest.givenGame_ifUndoPipe_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()
- PA19010: GameTest.givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange(), GameTest.givenGame_ifUndoPipe_stepCountIncreases(), GameTest.givenGame_ifPipeCanBePlaced_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19011: MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19012: GameTest.givenGame_ifUndoPipe_stepCountIncreases()
- PA19013: GameTest.givenGame_ifUndoPipe_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()
- PA19014: CellStackTest.givenEmptyStack_ifPop_returnNull(), CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange(), CellStackTest.givenStack_whenPop_incUndoCount(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19015: MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19016: CellStackTest.givenEmptyStack_ifPop_returnNull(), CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange(), CellStackTest.givenStack_whenPop_incUndoCount(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19017: CellStackTest.givenEmptyStack_whenPush_undoCountDoesNotChange(), CellStackTest.givenStack_whenPop_incUndoCount()
- PA19018: CellStackTest.givenEmptyStack_whenPush_undoCountDoesNotChange(), CellStackTest.givenEmptyStack_ifPop_returnNull(), CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange(), CellStackTest.givenStack_whenPop_incUndoCount(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), PipeQueueTest.givenQueue_afterGivenPipes_assertNewPipesAreGenerated()
- PA19019: GameTest.givenGame_ifUndoPipe_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), PipeQueueTest.givenQueue_afterGivenPipes_assertNewPipesAreGenerated()
- PA19020: CellStackTest.givenEmptyStack_ifPop_returnNull(), CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()
- PA19021: CellStackTest.givenEmptyStack_ifPop_returnNull(), CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange(); GameTest.givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange(), GameTest.givenGame_ifUndoPipe_stepCountIncreases(), GameTest.givenGame_ifSkipPipe_stepCountIncreases(), GameTest.givenGame_ifPipeCanBePlaced_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail(); PipeQueueTest.givenQueue_ifQueueConsumeIsMutable_thenSucceed(), PipeQueueTest.givenQueue_ifQueueContainsGivenPipes_thenSucceed(), PipeQueueTest.givenQueue_afterGivenPipes_assertNewPipesAreGenerated()
- PA19022: CellStackTest.givenEmptyStack_ifPop_returnNull(), CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange(); PipeTest.connection(); GameTest.givenGame_ifUndoPipe_stepCountIncreases(); MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail(), MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess(), MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()
- PA20002: GameTieBreakerTests.testMoveAndDeadlock(); ArcherTests.testGetAvailableMovesComplex()
- PA20003: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinSingle(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); IntegratedTestsWithArcherTests.testTieBreak(), IntegratedTestsWithArcherTests.testCaptureAllWin(); GameTieBreakerTests.testMoveAndDeadlock(); KnightTests.testGetAvailableMovesCapture(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(), IntegratedTestsWithoutArcherTests.testCaptureAllWin(); FirstNMovesProtectionTests.testNoProtection()
- PA20004: IntegratedTestsWithArcherTests.testTieBreak(); KnightTests.testGetAvailableMovesCapture(); ArcherTests.testGetAvailableMovesComplex(); FirstNMovesProtectionTests.testNoProtection()
- PA20005: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); GameUnitTests.testWinByLeaveCentralPlace(); IntegratedTestsWithArcherTests.testTieBreak(), IntegratedTestsWithArcherTests.testCaptureAllWin(); KnightTests.testGetAvailableMovesCapture(); ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(), IntegratedTestsWithoutArcherTests.testCaptureAllWin(); FirstNMovesProtectionTests.testNoProtection()
- PA20006: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); GameUnitTests.testWinByLeaveCentralPlace(); IntegratedTestsWithArcherTests.testTieBreak(); KnightTests.testGetAvailableMovesCapture(); ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(); ConsolePlayerTests.testNextMoveInvalidInput4(); FirstNMovesProtectionTests.testNoProtection()
- PA20007: ArcherTests.testGetAvailableMovesComplex()
- PA20008: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinSingle(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); IntegratedTestsWithArcherTests.testCaptureAllWin(); GameTieBreakerTests.testMoveAndDeadlock(); KnightTests.testGetAvailableMovesSimple(), KnightTests.testGetAvailableMovesCapture(), KnightTests.testGetAvailableMovesOccupied(), KnightTests.testGetAvailableMovesBlocked(); ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testCaptureAllWin(); FirstNMovesProtectionTests.testWithThenWithoutProtection(), FirstNMovesProtectionTests.testNoProtection()
- PA20009: GameMockPlayerIntegratedTests.testCaptureAllWinSingle(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(), GameMockPlayerIntegratedTests.testCaptureAllWin(); GameUnitTests.testWinByCaptureAll(); IntegratedTestsWithArcherTests.testCaptureAllWin(); GameTieBreakerTests.testMoveAndDeadlock(); RandomPlayerTests.testNextMove(); ArcherTests.testGetAvailableMovesSimple(), ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testCaptureAllWin()
- PA20010: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); GameUnitTests.testNotWinByArcherLeavingCentralPlace(); IntegratedTestsWithArcherTests.testCaptureAllWin(); ArcherTests.testGetAvailableMovesSimple(), ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testCaptureAllWin()
- PA20011: GameMockPlayerIntegratedTests.testCaptureAllWinSingle(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); GameUnitTests.testNotWinByArcherLeavingCentralPlace(); IntegratedTestsWithArcherTests.testCaptureAllWin(); GameTieBreakerTests.testMoveAndDeadlock(); KnightTests.testGetAvailableMovesSimple(), KnightTests.testGetAvailableMovesCapture(), KnightTests.testGetAvailableMovesOccupied(); ArcherTests.testGetAvailableMovesSimple(), ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(), IntegratedTestsWithoutArcherTests.testCaptureAllWin(); FirstNMovesProtectionTests.testWithProtection(), FirstNMovesProtectionTests.testWithThenWithoutProtection(), FirstNMovesProtectionTests.testNoProtection()
- PA20012: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); IntegratedTestsWithArcherTests.testTieBreak(), IntegratedTestsWithArcherTests.testCaptureAllWin(); GameTieBreakerTests.testMoveAndDeadlock(); KnightTests.testGetAvailableMovesCapture(); ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(), IntegratedTestsWithoutArcherTests.testCaptureAllWin(); FirstNMovesProtectionTests.testNoProtection()
- PA20013: IntegratedTestsWithArcherTests.testTieBreak(); KnightTests.testGetAvailableMovesCapture(); ArcherTests.testGetAvailableMovesComplex(); FirstNMovesProtectionTests.testNoProtection()
- PA20014: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinSingle(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); GameUnitTests.testNotWinByArcherLeavingCentralPlace(), GameUnitTests.testUpdateScoreBasic1(); IntegratedTestsWithArcherTests.testTieBreak(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(), IntegratedTestsWithoutArcherTests.testCaptureAllWin(); FirstNMovesProtectionTests.testWithProtection(), FirstNMovesProtectionTests.testWithThenWithoutProtection(), FirstNMovesProtectionTests.testNoProtection(); KnightTests.testGetAvailableMovesCapture(), KnightTests.testGetAvailableMovesOccupied(), KnightTests.testGetAvailableMovesBlocked(); ArcherTests.testGetAvailableMovesSimple(), ArcherTests.testGetAvailableMovesComplex(); RandomPlayerTests.testNextMove(); ConsolePlayerTests.testNextMoveInvalidInput0()
- PA20015: IntegratedTestsWithArcherTests.testTieBreak(); KnightTests.testGetAvailableMovesCapture(); ArcherTests.testGetAvailableMovesComplex(); FirstNMovesProtectionTests.testNoProtection()
- PA20016: GameMockPlayerIntegratedTests.testCentralPlaceWin(); GameUnitTests.testWinByLeaveCentralPlace(); IntegratedTestsWithArcherTests.testTieBreak(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin()
- PA20017: IntegratedTestsWithArcherTests.testTieBreak(); KnightTests.testGetAvailableMovesCapture(); ArcherTests.testGetAvailableMovesComplex(); FirstNMovesProtectionTests.testNoProtection()
- PA20018: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(); GameUnitTests.testWinByLeaveCentralPlace(); IntegratedTestsWithArcherTests.testTieBreak(); ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin()
- PA20019: ArcherTests.testGetAvailableMovesComplex()
- PA20020: GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); IntegratedTestsWithArcherTests.testTieBreak(), IntegratedTestsWithArcherTests.testCaptureAllWin(); KnightTests.testGetAvailableMovesCapture(); ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testCaptureAllWin(); ConsolePlayerTests.testNextMoveInvalidInput0(), ConsolePlayerTests.testNextMoveInvalidInput1(), ConsolePlayerTests.testNextMoveInvalidInput2(), ConsolePlayerTests.testNextMoveInvalidInput3(), ConsolePlayerTests.testNextMoveInvalidInput4(), ConsolePlayerTests.testNextMoveInvalidInput5(), ConsolePlayerTests.testNextMoveInvalidInput6(), ConsolePlayerTests.testNextMoveInvalidInput7(); FirstNMovesProtectionTests.testWithProtection(), FirstNMovesProtectionTests.testNoProtection()
- PA20021: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinSingle(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); GameUnitTests.testWinByCaptureAll(), GameUnitTests.testCannotWinWithinProtection(), GameUnitTests.testMovePieceCapture(), GameUnitTests.testDefaultValues(), GameUnitTests.testUpdateScoreZero(), GameUnitTests.testGetAvailableMovesNoPiece(), GameUnitTests.testMovePieceNormal()
- PA20022: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinSingle(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAllWinMultiple(); GameUnitTests.testNotWinByArcherLeavingCentralPlace(); IntegratedTestsWithArcherTests.testTieBreak(), IntegratedTestsWithArcherTests.testCaptureAllWin(); GameTieBreakerTests.testMoveAndDeadlock(); KnightTests.testGetAvailableMovesOccupied(), KnightTests.testGetAvailableMovesBlocked(); ArcherTests.testGetAvailableMovesSimple(), ArcherTests.testGetAvailableMovesComplex(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(), IntegratedTestsWithoutArcherTests.testCaptureAllWin(); ConsolePlayerTests.testNextMoveInvalidInput0(), ConsolePlayerTests.testNextMoveInvalidInput1(), ConsolePlayerTests.testNextMoveInvalidInput2(), ConsolePlayerTests.testNextMoveInvalidInput3(), ConsolePlayerTests.testNextMoveInvalidInput4(), ConsolePlayerTests.testNextMoveInvalidInput5(), ConsolePlayerTests.testNextMoveInvalidInput6(), ConsolePlayerTests.testNextMoveInvalidInput7(); FirstNMovesProtectionTests.testNoProtection()
- PA20023: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(); GameUnitTests.testWinByLeaveCentralPlace(); IntegratedTestsWithArcherTests.testTieBreak(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(); ConsolePlayerTests.testNextMoveInvalidInput0(), ConsolePlayerTests.testNextMoveInvalidInput1(), ConsolePlayerTests.testNextMoveInvalidInput2(), ConsolePlayerTests.testNextMoveInvalidInput3(), ConsolePlayerTests.testNextMoveInvalidInput4(), ConsolePlayerTests.testNextMoveInvalidInput5(), ConsolePlayerTests.testNextMoveInvalidInput6(), ConsolePlayerTests.testNextMoveInvalidInput7(), ConsolePlayerTests.testNextMove(); FirstNMovesProtectionTests.testWithThenWithoutProtection(), FirstNMovesProtectionTests.testNoProtection()
- PA20024: GameMockPlayerIntegratedTests.testCentralPlaceWin(), GameMockPlayerIntegratedTests.testCaptureAndCentralPlaceWin(); GameUnitTests.testWinByLeaveCentralPlace(); KnightTests.testGetAvailableMovesOccupied(); IntegratedTestsWithArcherTests.testTieBreak(); GameTieBreakerTests.testMoveAndDeadlock(); IntegratedTestsWithoutArcherTests.testLeaveCentralPlaceWin(); ConsolePlayerTests.testNextMoveInvalidInput7(); FirstNMovesProtectionTests.testWithProtection()
- PA21007: GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT)
- PA21008: GameBoardControllerTest.testMakeValidMoveToMine()
- PA21009: GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT)
- PA21010: GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMoveToMine()
- PA21011: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21012: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21013: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21014: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21015: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21016: GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMoveToMine()
- PA21017: GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT)
- PA21018: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21019: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21020: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21021: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall()
- PA21022: GameBoardControllerTest.testUndoMoveWithPickups(), GameBoardControllerTest.testMakeValidMovePassingGem(), GameBoardControllerTest.testUndoMoveTrivial(), GameBoardControllerTest.testMakeValidMovePassingExtraLives(), GameBoardControllerTest.testMakeValidMoveToStopCell(), GameBoardControllerTest.testMakeValidMoveToMine(), GameBoardControllerTest.testMakeValidMoveToWall(), GameControllerTest.testUndoMoveRestoresExtraLife(boolean)[invocation:#2], GameControllerTest.testUndoMoveRestoresEntities(boolean)[invocation:#2], GameControllerTest.testMakeValidMoveToStopCell(boolean)[invocation:#1,#2], GameControllerTest.testMakeValidMoveToMineCrossingOtherPickUps(boolean)[invocation:#1,#2], GameBoardTest.testGetEntityCellWithIntsInvalid()
- PA21023: MoveStackTest.testPop(), MoveStackTest.testPush(), MoveStackTest.testInitialState(), GameBoardControllerTest.testMakeMoveToBorder(UP), GameBoardControllerTest.testMakeMoveToBorder(DOWN), GameBoardControllerTest.testMakeMoveToBorder(LEFT), GameBoardControllerTest.testMakeMoveToBorder(RIGHT), GameControllerTest.testMakeMoveToBorderLimitedLives(UP), GameControllerTest.testMakeMoveToBorderLimitedLives(DOWN), GameControllerTest.testMakeMoveToBorderLimitedLives(LEFT), GameControllerTest.testMakeMoveToBorderLimitedLives(RIGHT), GameControllerTest.testMakeValidMoveToMineAndLosing(), GameControllerTest.testUndoMoveRestoresExtraLife(boolean)[#1,#2], GameControllerTest.testUndoMoveUndoesOne(boolean)[#1,#2], GameControllerTest.testUndoMoveRestoresEntities(boolean)[#1,#2], GameControllerTest.testUndoMoveTrivial(boolean)[#1,#2], GameControllerTest.testMakeValidMovePassingExtraLives(boolean)[#1,#2], GameControllerTest.testMakeValidMoveToStopCell(boolean)[#1,#2], GameControllerTest.testMakeValidMovePassingEntities(boolean)[#1,#2], GameControllerTest.testMakeMoveToAdjacentWallUnlimitedLives(UP/DOWN/LEFT/RIGHT), GameControllerTest.testMakeValidMovePassingGems(boolean)[#1,#2], GameControllerTest.testMakeValidMoveToMine(boolean)[#1,#2], GameControllerTest.testMakeValidMoveToWall(boolean)[#1,#2], GameControllerTest.testUndoMoveRestoresGems(boolean)[#1,#2], GameControllerTest.testUndoFromEmptyStack(boolean)[#1,#2], (total 51 test failures)
- PA22001: AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22002: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testCheckpointWhenNotNeed(), TerminalSokobanGameTest.testGameLoop(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22003: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22004: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameExit(), TerminalSokobanGameTest.testGameLoop(), TerminalSokobanGameTest.testGameWin(), TerminalRenderingEngineTest.testRender(), TerminalInputEngineTest.testMove(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22005: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testCheckpointWhenNotNeed(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22006: AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22007: AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), GameStateTest.testWin(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22008: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), GameStateTest.testWin(), GameStateTest.testMove(), GameStateTest.testPushBox(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22009: AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22010: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameExit(), TerminalSokobanGameTest.testGameLoop(), TerminalSokobanGameTest.testGameWin(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22011: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testPushBoxAgainstWall(), AbstractSokobanGameTest.testHitAnotherPlayer(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testPushBox(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22012: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22013: AbstractSokobanGameTest.testMove(), AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testPushBoxAgainstWall(), AbstractSokobanGameTest.testCheckpointWhenNotNeed(), AbstractSokobanGameTest.testHitAnotherPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testWin(), GameStateTest.testMove(), GameStateTest.testUndoWhenThereIsMoveButNoCheckpoint(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22014: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22015: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testCheckpointWhenNeed(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22016: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testCheckpointWhenNotNeed(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22017: AbstractSokobanGameTest.testExceedingUndoQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22018: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testPushBoxAgainstWall(), AbstractSokobanGameTest.testCheckpointWhenNotNeed(), AbstractSokobanGameTest.testHitAnotherPlayer(), AbstractSokobanGameTest.testHitWall(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testWin(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22019: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testCheckpointWhenNeed(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testWin(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22020: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testWin(), GameStateTest.testPushBox(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22021: AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22022: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testCheckpointWhenNotNeed(), TerminalSokobanGameTest.testGameLoop(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22023: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22024: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalSokobanGameTest.testGameWin(), TerminalRenderingEngineTest.testRender(), TerminalInputEngineTest.testMove(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22025: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testCheckpointWhenNotNeed(), TerminalSokobanGameTest.testGameLoop(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22006: AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22007: AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), TerminalSokobanGameTest.testGameLoop(), GameStateTest.testWin(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22008: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), GameStateTest.testWin(), GameStateTest.testMove(), GameStateTest.testPushBox(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22009: AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22010: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameExit(), TerminalSokobanGameTest.testGameLoop(), TerminalSokobanGameTest.testGameWin(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22011: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testMoveNonExistingPlayer(), AbstractSokobanGameTest.testPushBoxAgainstWall(), AbstractSokobanGameTest.testHitAnotherPlayer(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testPushBox(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22012: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22013: AbstractSokobanGameTest.testMove(), AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testPushOtherPlayerBox(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testPushBoxAgainstWall(), AbstractSokobanGameTest.testCheckpointWhenNotNeed(), AbstractSokobanGameTest.testHitAnotherPlayer(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testWin(), GameStateTest.testMove(), GameStateTest.testUndoWhenThereIsMoveButNoCheckpoint(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22014: AbstractSokobanGameTest.testUndoUnlimited(), AbstractSokobanGameTest.testUndoWithinQuota(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()
- PA22015: AbstractSokobanGameTest.testExceedingUndoQuota(), AbstractSokobanGameTest.testPushBox(), AbstractSokobanGameTest.testCheckpointWhenNeed(), TerminalSokobanGameTest.testGameLoop(), TerminalRenderingEngineTest.testRender(), GameStateTest.testUndoWhenThereIsCheckpoint()

## 4. Test Failure Analysis Section

### PA19003
- Sample: `/PA19/deepseek-v4-flash/sample3`
- Summary: Test expects an empty FillableCell (no pipe) to return `'.'` from `toSingleChar()`, but the actual implementation returned `' '` (space). Violates requirement.txt: *"FillableCell holds an optional pipe and renders pipe or '.'"*
- Test Cases: `FillableCellTest.givenCell_assertSingleCharRepresentation()`
- Functional Code: `/PA19/deepseek-v4-flash/sample3/pa19/FillableCell.java` lines 128-134
- Deep Analysis: FillableCell is a pipe-placeable cell; when there is no pipe, it should render as `'.'`, and when there is a pipe, it renders as the pipe's character. The test creates FillableCell -> sets coordinate -> does not set pipe -> calls `toSingleChar()` -> asserts returns `'.'`. In `FillableCell.toSingleChar()`, when `getPipe() == null`, it returns `' '` (space), but the requirement explicitly requires `'.'`. The LLM incorrectly implemented the empty FillableCell's render character as a space.
- Error Type: Return value error

### PA19004
- Sample: `/PA19/deepseek-v4-flash/sample4`
- Summary: (1) CellStack test expects `pop()` to increment `undoCount` by 1 after successful pop, but the implementation does not modify `count` in `pop()`, violating requirement.txt: *"undoCount counts the number of successful undo actions (i.e., successful pop operations)"*; (2) Map test expects `fillTiles()` to propagate water flow from source using BFS, but `fillBeginTile()` does not add the source coordinate to the `filledTiles` list, causing BFS to have no seed node, violating requirement.txt: *"fillBeginTile() marks the source cell as filled. fillTiles(distance) fills pipes within the specified distance from source using BFS-style expansion."*
- Test Cases: `CellStackTest.givenStack_whenPop_incUndoCount()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/deepseek-v4-flash/sample4/pa19/CellStack.java` lines 181-210; `/PA19/deepseek-v4-flash/sample4/pa19/Map.java` lines 527-624
- Deep Analysis:
  - **CellStack Issue**: `CellStack.pop()` only executes `getStack().remove(size-1)` without modifying the `count` field; `getUndoCount()` returns `count` (always 0). The LLM faithfully followed the model-doc's incorrect description of "Do NOT modify count," while requirement.txt explicitly requires undoCount to count successful pop operations.
  - **Map Issue**: `fillBeginTile()` only calls `sourceCell.setFilled(true)` but does not add the source coordinate to the `filledTiles` list. `fillTiles(2)` iterates from `getFilledTiles()` for BFS, but the list is empty (`originalSize=0`), the loop does not execute, and no pipes are filled. The root cause is `fillBeginTile()` missing `getFilledTiles().add(sourceCell.getCoord())`.
- Error Type: CellStack -> object state update error; Map -> implementation error (BFS seed node missing) - complex algorithm implementation error

### PA19005
- Sample: `/PA19/deepseek-v4-flash/sample5`
- Summary: (1) Five methods in Map (`tryPlacePipe()`, `undo()`, `fillTiles()`, `hasLost()`, `checkPath()`) are all TODO stubs (throw UOE), causing all pipe placement/BFS filling/win-loss determination to fail; (2) Game's `undoStep()` calls `map.undo(coord)` (TODO stub throws UOE) and under EMF semantics, pipe reference is lost; (3) `fillBeginTile()` only marks source as filled but does not add source coordinate to `filledTiles` list, BFS lacks seed node.
- Test Cases: `GameTest.givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange()`, `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`, `GameTest.givenGame_ifPipeCanBePlaced_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`
- Functional Code: `/PA19/deepseek-v4-flash/sample5/pa19/Game.java` lines 395-412(placePipe), 455-466(undoStep), 430-434(skipPipe); `/PA19/deepseek-v4-flash/sample5/pa19/Map.java` lines 473-480(tryPlacePipe), 500-507(undo), 515-521(fillBeginTile), 548-555(fillTiles), 566-571(checkPath), 589-596(hasLost)
- Deep Analysis:
  - **Map Issue**: `tryPlacePipe(int row, int col, Pipe pipe)` (lines 473-480) is a TODO stub that directly throws `UnsupportedOperationException()`, without implementing row/column boundary checks, cell type checks, pipe field setting logic, etc. `placePipe()` calling it inevitably throws UOE instead of returning false/true. `fillTiles(int distance)` (lines 548-555), `hasLost()` (lines 589-596), `checkPath()` (lines 566-571) are also TODO stubs; all 4 MapTest cases fail because they depend on `fillTiles()`. `fillBeginTile()` (lines 515-521) only calls `sourceCell.setFilled(true)` but does not add source coordinate to `filledTiles` list; even if BFS were implemented, there would be no seed node to iterate from.
  - **Game Issue**: `placePipe()` (lines 395-412) calls `getMap().tryPlacePipe()` which throws UOE due to TODO stub, so `givenGame_ifPipeCanBePlaced_stepCountIncreases()` and `givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange()` both cannot pass. `undoStep()` (lines 455-466) calls `getMap().undo(coord)` (TODO stub throws UOE), and also has the EMF container chain inverse-remove issue (same as PA19006): `pop()` removes FillableCell from CellStack's containment EList -> if `map.undo()` were implemented, it would call `fillableCell.setPipe(null)` triggering EMF's `eInverseRemove` clearing the pipe -> subsequent `pipeQueue.undo(poppedCell.getPipe())` passes null, causing pipe loss (currently masked because `map.undo` throws UOE first). `undoStep()`'s `setNumOfSteps(getNumOfSteps() + 1)` has semantic ambiguity: model-doc requires incrementing step count after undo and returning true, but requirement does not explicitly state whether undo should "increase" the step count; here it follows model-doc.
- Error Type: Map -> extensive TODO not implemented (5 method stubs); Game -> missing code (cascade failure from tryPlacePipe stub) + EMF container chain inverse-remove semantic error (undoStep pipe reference loss, masked by TODO stub)

### PA19006
- Sample: `/PA19/gemini-3.1-flash-lite/sample1`
- Summary: (1) Map test expects `fillTiles()` to propagate water flow filling pipes via BFS, but the method only has a return implementation, violating requirement.txt: *"fillTiles(distance) fills pipes within the specified distance from source using BFS-style expansion"*; (2) Game test expects `undoStep()` to correctly restore pipe to queue and increment step count, but EMF container chain inverse-remove semantics cause pipe reference loss, violating requirement.txt: *"undoStep: Pops the last placed cell from CellStack, restores the pipe to the queue head, clears the cell on the map, and increments the step count."*
- Test Cases: `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`
- Functional Code: `/PA19/gemini-3.1-flash-lite/sample1/pa19/Map.java` lines 545-552; `/PA19/gemini-3.1-flash-lite/sample1/pa19/Game.java` lines 461-471
- Deep Analysis:
  - **Map Issue**: `fillTiles(int distance)` is a stub implementation (only `if (distance < 0) return;` + comment), with no BFS logic. Also `fillBeginTile()` does not add source coordinate to `filledTiles`. LLM did not implement the method.
  - **Game Issue**: `undoStep()` has a fatal EMF container management issue in its execution order. Call chain: `pop()` -> `map.undo(coord)` -> `pipeQueue.undo(cell.getPipe())`. When `pop()` removes FillableCell from CellStack's containment EList, the subsequent `map.undo()` call to `fillableCell.setPipe(null)` triggers EMF container's `eInverseRemove` mechanism to clear the container chain, setting FillableCell's `pipe` field to null. After that, `pipeQueue.undo(cell.getPipe())` passes null, causing pipe loss. The correct approach is to save `Pipe pipe = cell.getPipe()` reference after `pop()` before executing subsequent operations.
- Error Type: Map -> complex algorithm implementation error; Game -> algorithm error attributed to EMF // unable to understand EMF code knowledge (EMF container chain inverse-remove semantics)

### PA19007
- Sample: `/PA19/gemini-3.1-flash-lite/sample2`
- Summary: Map test expects `fillTiles()` to propagate water flow filling pipes via BFS, but the method has no actual BFS logic, and `fillBeginTile()` does not add source coordinate to `filledTiles` list. Violates requirement.txt: *"fillBeginTile() marks the source cell as filled. fillTiles(distance) fills pipes within the specified distance from source using BFS-style expansion."*
- Test Cases: `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/gemini-3.1-flash-lite/sample2/pa19/Map.java` lines 513-545
- Deep Analysis:
  - **fillTiles()**: Implementation only has skeleton code `int newlyFilled = 0; // ... BFS logic here ... this.prevFilledTiles = newlyFilled;`, BFS logic is a comment placeholder, with no actual pipe traversal or filling operations. LLM generated method signature and framework but did not fill in the core algorithm.
- Error Type: Implementation error (BFS core algorithm not implemented; it assumed another class existed with this queue but could not find it)

### PA19008
- Sample: `/PA19/gemini-3.1-flash-lite/sample3`
- Summary: (1) CellStack test expects `pop()` to return null on empty stack, but implementation throws `IllegalStateException`, violating requirement.txt: *"CellStack: push/pop last placement"* (pop should safely handle empty stack); (2) Game test `undoStep()` causes pipe reference loss due to EMF container chain inverse-remove (same as PA19006), violating requirement.txt; (3) Map test `fillTiles()` BFS logic incomplete, violating requirement.txt.
- Test Cases: `CellStackTest.givenEmptyStack_ifPop_returnNull()`, `CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange()`; `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/gemini-3.1-flash-lite/sample3/pa19/CellStack.java` lines 181-190; `/PA19/gemini-3.1-flash-lite/sample3/pa19/Game.java` lines 458-469; `/PA19/gemini-3.1-flash-lite/sample3/pa19/Map.java` lines 514-563
- Deep Analysis:
  - **CellStack Issue**: `pop()` executes `throw new IllegalStateException("Stack is empty")` on empty stack, while test `givenEmptyStack_ifPop_returnNull` expects returning null, `givenEmptyStack_whenPop_undoCountDoesNotChange` expects no exception and unchanged undoCount after empty stack pop. LLM implemented empty stack handling as throwing exception instead of returning null. Note: on non-empty stack, `pop()` correctly increments count (line 188 `setCount(getCount() + 1)`), `getUndoCount()` returns `getCount()`, this part is correct.
  - **Game Issue**: Exactly the same EMF container chain inverse-remove issue as PA19006. `undoStep()` call chain: `pop()` -> `map.undo(coord)` -> `pipeQueue.undo(cell.getPipe())`. `map.undo()` calls `fillableCell.setPipe(null)` triggering EMF container inverse-remove mechanism, clearing poppedCell's pipe field, causing `pipeQueue.undo(null)` to lose pipe.
  - **Map Issue**: `fillTiles()` has BFS framework (using Queue + while loop), but neighbor traversal logic is `// ...` comment placeholder, without actually checking pipe connection directions or adding neighbors to the queue. Also `fillBeginTile()` does not add source coordinate to `filledTiles`.
- Error Type: CellStack -> return value error (empty stack should return null but throws exception); Game -> unable to understand EMF code knowledge (EMF container chain inverse-remove semantics); Map -> implementation error (BFS neighbor traversal not implemented)

### PA19009
- Sample: `/PA19/gemini-3.1-flash-lite/sample4`
- Summary: (1) Map test expects `fillTiles()` to propagate water flow filling pipes via BFS, but the method only has BFS framework code without actual neighbor traversal logic, and `fillBeginTile()` does not add source coordinate to `filledTiles`, violating requirement.txt; (2) Game test `undoStep()` causes pipe reference loss due to EMF container chain inverse-remove (same as PA19006), violating requirement.txt.
- Test Cases: `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`
- Functional Code: `/PA19/gemini-3.1-flash-lite/sample4/pa19/Map.java` lines 516-555; `/PA19/gemini-3.1-flash-lite/sample4/pa19/Game.java` lines 446-456
- Deep Analysis:
  - **Map Issue**: `fillTiles()` uses `Queue<Coordinate>` and double-layer `for` loop to build BFS framework, but inner loop body only has comment `// Perform neighbor checks and update filled state`, with no pipe connection direction checks or neighbor enqueue operations. Also `fillBeginTile()` does not add source coordinate to `filledTiles` list, same as PA19004/PA19007.
  - **Game Issue**: Exactly the same EMF container chain inverse-remove issue as PA19006. `undoStep()` call chain: `pop()` -> `map.undo(coord)` -> `pipeQueue.undo(cell.getPipe())`. `map.undo()` calls `setPipe(null)` triggering EMF container inverse-remove, clearing poppedCell's pipe field, causing `pipeQueue.undo(null)` to lose pipe. CellStack's `pop()` implementation is correct (empty stack returns null, non-empty increments count), not the issue.
- Error Type: Map -> implementation error (BFS neighbor traversal only has comment placeholder); Game -> unable to understand EMF code knowledge (EMF container chain inverse-remove semantics)

### PA19010
- Sample: `/PA19/gemini-3.1-flash-lite/sample5`
- Summary: (1) Game test fails due to `placePipe()` column index formula error (`col - 'A'` missing `+ 1`), causing pipe placement position offset, all GameTests fail, violating requirement.txt: *"placePipe: This method should convert the row and column into Coordinate (using row as-is, and col mapped by col - 'A' + 1)"*; (2) Map test expects `fillTiles()` to fill pipes via BFS, but the method is only a comment placeholder, violating requirement.txt.
- Test Cases: `GameTest.givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange()`, `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`, `GameTest.givenGame_ifPipeCanBePlaced_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/gemini-3.1-flash-lite/sample5/pa19/Game.java` lines 391-459; `/PA19/gemini-3.1-flash-lite/sample5/pa19/Map.java` lines 519-551
- Deep Analysis:
  - **Game.placePipe() column index error**: Line 393 `int colIndex = col - 'A';` is missing `+ 1`, should be `col - 'A' + 1`. This causes column index to be offset by 1: `placePipe(1, 'A')` gives colIndex=0 (hits Wall instead of FillableCell), placement fails returning false, but test expects true. `givenGame_ifUndoPipe_stepCountIncreases` fails because placePipe fails causing cellStack to be empty, undoStep's pop() returns null also returning false, but test expects true. `givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange` where placePipe(1,'B') gives colIndex=1 which happens to hit TerminationCell (not FillableCell) returning false, this test may pass.
  - **Game.undoStep() abnormal coordinate retrieval**: Line 455 uses complex EMF reflection `((EObject)cell).eContainer() instanceof Coordinate ? ...` to get coordinates, instead of the correct `cell.getCoord()`. Even if pipe is saved early (line 449 `Pipe pipe = cell.getPipe()` before map.undo), the coordinate retrieval method is also wrong — `eContainer()` returns the EMF container object (CellStack or Map), not a Coordinate.
  - **Map.fillTiles()**: Only `this.prevFilledTiles = 0; // ... BFS traversal logic ...`, BFS logic is a comment placeholder, with no actual implementation. `fillBeginTile()` also does not add source coordinate to `filledTiles`.
- Error Type: Game -> algorithm error due to requirement decomposition (col - 'A' missing +1, violating requirement.txt's explicit formula); implementation error (EMF knowledge: undoStep uses wrong eContainer method to get coordinates); Map -> implementation error (BFS is only a comment placeholder)

### PA19011
- Sample: `/PA19/minimax-m3/sample1`
- Summary: Map test expects `fillTiles()` to propagate water flow filling pipes via BFS, but `fillBeginTile()` does not add source coordinate to `filledTiles` list, causing BFS to have no seed node, violating requirement.txt.
- Test Cases: `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/minimax-m3/sample1/pa19/Map.java` lines 540-640
- Deep Analysis:
  - **fillBeginTile()** only calls `sourceCell.setFilled(true)` but does not add source coordinate to `filledTiles` list, same as PA19004/PA19007.
  - **fillTiles()** is fully implemented (using BFS Queue + visited Set + pipe connection direction checks), but depends on `filledTiles` as BFS seed nodes. Since `filledTiles` is empty, `queue` is also empty, BFS loop does not execute, and no pipes are filled.
- Error Type: Implementation error (fillBeginTile did not add source coordinate to filledTiles, causing BFS seed node missing)

### PA19012
- Sample: `/PA19/minimax-m3/sample4`
- Summary: Game test expects `undoStep()` to correctly restore pipe to queue and increment step count, but EMF container chain inverse-remove semantics cause pipe reference loss, violating requirement.txt.
- Test Cases: `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`
- Functional Code: `/PA19/minimax-m3/sample4/pa19/Game.java` lines 482-495; `/PA19/minimax-m3/sample4/pa19/CellStack.java` lines 194-203
- Deep Analysis:
  - **CellStack.pop()** is correctly implemented: empty stack returns null, non-empty removes last element and increments count. `placePipe()` and `skipPipe()` are also correct.
  - **Game.undoStep()** code logic appears correct: first saves `coord = cell.getCoord()`, then `map.undo(coord)`, then `pipe = cell.getPipe()`, finally `pipeQueue.undo(pipe)`. But there is the same EMF container chain inverse-remove issue as PA19006: when `map.undo()` calls `fillableCell.setPipe(null)`, EMF container's inverse-remove mechanism may clear the popped cell's pipe field through FillableCell's container relationship chain in CellStack containment EList. After that `cell.getPipe()` returns null, causing `pipeQueue.undo(null)` to lose pipe. Although pipe is read after map.undo, EMF container chain's cascade clearing effect can still cause pipe reference loss.
- Error Type: Unable to understand EMF code knowledge (EMF container chain inverse-remove semantics)

### PA19013
- Sample: `/PA19/minimax-m3/sample5`
- Summary: (1) Game test `undoStep()` uses `setNumOfSteps(getNumOfSteps() - 1)` to decrement step count instead of incrementing, while `map.undo()` is a TODO stub throwing exception, violating requirement.txt.
- Test Cases: `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`
- Functional Code: `/PA19/minimax-m3/sample5/pa19/Game.java` lines 461-472; `/PA19/minimax-m3/sample5/pa19/Map.java` lines 528-598
- Deep Analysis:
  - **Game.undoStep() step count decrement error**: Line 470 `setNumOfSteps(getNumOfSteps() - 1)` uses `- 1` to decrement, while requirement.txt explicitly requires "increments the step count" (increment). Should be `+ 1`. Also `map.undo()` is a TODO stub (line 533 `throw new UnsupportedOperationException()`), calling it throws exception.
- Error Type: Game -> object state update (step count decrement instead of increment) + implementation error (map.undo is TODO stub); Map -> implementation error (fillTiles is TODO stub)

### PA19014
- Sample: `/PA19/gpt-5.4-mini/sample1`
- Summary: (1) CellStack test expects `pop()` to return null on empty stack, but implementation does not check for empty stack and directly calls `remove()` causing IndexOutOfBoundsException; `getUndoCount()` always returns 0, does not track pop count, violating requirement.txt; (2) Map test `fillTiles()` BFS is more complete but depends on `filledTiles` seed node, while `fillBeginTile()` does not add source to `filledTiles`, violating requirement.txt.
- Test Cases: `CellStackTest.givenEmptyStack_ifPop_returnNull()`, `CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange()`, `CellStackTest.givenStack_whenPop_incUndoCount()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/gpt-5.4-mini/sample1/pa19/CellStack.java` lines 170-248; `/PA19/gpt-5.4-mini/sample1/pa19/Map.java` lines 549-678
- Deep Analysis:
  - **CellStack.pop()** line 211 directly executes `stack.remove(stack.size() - 1)` without empty stack check, throws `IndexOutOfBoundsException` on empty stack. Both tests fail due to exception.
  - **CellStack.getUndoCount()** line 246 always `return 0;`, does not track pop count. `pop()` also does not increment any undo counter (only sets `count = stack.size()`). Test `givenStack_whenPop_incUndoCount` expects getUndoCount() = 1 after pop, but actual is always 0.
  - **Map.fillTiles()** has more complete BFS implementation (using frontier expansion + direction matching checks), but depends on `filledTiles` as initial frontier. `fillBeginTile()` does not add source coordinate to `filledTiles`, causing BFS seed node missing.
- Error Type: CellStack -> return value error; Map -> implementation error (fillBeginTile did not add source seed)

### PA19015
- Sample: `/PA19/gpt-5.4-mini/sample2`
- Summary: Map test expects `fillTiles()` to propagate water flow filling pipes via BFS, but `fillBeginTile()` does not add source coordinate to `filledTiles`, and BFS implementation does not add newly filled coordinates to `filledTiles` causing subsequent iterations to have no seed, violating requirement.txt.
- Test Cases: `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/gpt-5.4-mini/sample2/pa19/Map.java` lines 555-730
- Deep Analysis:
  - **fillBeginTile()** only calls `sourceCell.setFilled(true)` but does not add source coordinate to `filledTiles`, same as PA19004/PA19007/PA19011/PA19014.
  - **fillTiles()** has more complete BFS implementation: starts seed from source's pointingTo direction neighbor, expands through direction matching checks. But during BFS, newly filled coordinates are only added to `newFilled` and `next` (frontier) lists, not added to `filledTiles`. `filledTiles` is never updated, causing multi-round BFS to have no seed, and tests checking `filledTiles` state cannot observe filled pipes.
- Error Type: Implementation error (fillBeginTile did not add source seed + fillTiles did not update filledTiles)

### PA19016
- Sample: `/PA19/gpt-5.4-mini/sample3`
- Summary: (1) CellStack test expects `pop()` to return null on empty stack, but implementation throws `IllegalStateException`; `getUndoCount()` always returns 0, does not track pop count, violating requirement.txt; (2) Map test `fillTiles()` BFS incorrectly clears `filledTiles` and uses hardcoded coordinate (0,0) as seed instead of source's real coordinates, violating requirement.txt.
- Test Cases: `CellStackTest.givenEmptyStack_ifPop_returnNull()`, `CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange()`, `CellStackTest.givenStack_whenPop_incUndoCount()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/gpt-5.4-mini/sample3/pa19/CellStack.java` lines 172-257; `/PA19/gpt-5.4-mini/sample3/pa19/Map.java` lines 565-650
- Deep Analysis:
  - **CellStack.pop()** line 218 throws `IllegalStateException("stack is empty")` instead of returning null on empty stack, causing both tests to fail.
  - **CellStack.getUndoCount()** line 256 always `return 0;`, `pop()` also does not increment any undo counter (only decrements count = stack size), causing `givenStack_whenPop_incUndoCount` to fail.
  - **Map.fillTiles()** line 615 `getFilledTiles().clear()` clears all filled coordinates, then uses hardcoded coordinate (0,0) as BFS seed (lines 626-629 `new Coordinate() {{ setRow(0); setCol(0); }}`), instead of starting BFS from sourceCell's real coordinates, causing water to propagate from wrong position.
- Error Type: CellStack -> return value error (pop throws exception instead of returning null + getUndoCount always returns 0); Map -> implementation error (fillTiles clears filledTiles and uses wrong seed coordinate)

### PA19017
- Sample: `/PA19/gpt-5.4-mini/sample4`
- Summary: CellStack `count` field is used simultaneously as stack size counter and undo counter, `push()` increments count causing `getUndoCount()` to misreport, violating requirement.txt: *"CellStack: undoCount counts the number of successful undo actions (i.e., successful pop operations). Push does not affect undoCount."*
- Test Cases: `CellStackTest.givenEmptyStack_whenPush_undoCountDoesNotChange()`, `CellStackTest.givenStack_whenPop_incUndoCount()`
- Functional Code: `/PA19/gpt-5.4-mini/sample4/pa19/CellStack.java` lines 172-257
- Deep Analysis:
  - **push()** line 179 `setCount(getCount() + 1)` increments count, but **getUndoCount()** line 256 returns `getCount()`. Test `givenEmptyStack_whenPush_undoCountDoesNotChange` expects undoCount to remain 0 after push, but actual `getUndoCount()` = `count` = 1 -> FAIL.
  - **pop()** line 222 `setCount(getCount() - 1)` decrements count, after push+pop count returns to 0, `getUndoCount()` = 0. But test `givenStack_whenPop_incUndoCount` expects undoCount = 1 after pop -> FAIL. Root cause is count being reused as stack size tracker instead of undo counter, lacking independent undo counter.
- Error Type: Object state update (count field reused as stack size and undo counter, causing semantic conflict)

### PA19018
- Sample: `/PA19/gpt-5.4-mini/sample5`
- Summary: (1) CellStack `pop()` has no empty stack check, throws IndexOutOfBoundsException, `getUndoCount()` returns count (stack size) instead of undo count, violating requirement.txt; (2) Map `fillTiles()` only calls `fillBeginTile()` and sets prevFilled variable, no actual BFS implementation, violating requirement.txt; (3) PipeQueue test `assertNewPipesAreGenerated` fails, related to pipe generation logic.
- Test Cases: `CellStackTest.givenEmptyStack_whenPush_undoCountDoesNotChange()`, `CellStackTest.givenEmptyStack_ifPop_returnNull()`, `CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange()`, `CellStackTest.givenStack_whenPop_incUndoCount()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`; `PipeQueueTest.givenQueue_afterGivenPipes_assertNewPipesAreGenerated()`
- Functional Code: `/PA19/gpt-5.4-mini/sample5/pa19/CellStack.java` lines 172-249; `/PA19/gpt-5.4-mini/sample5/pa19/Map.java` lines 558-607; `/PA19/gpt-5.4-mini/sample5/pa19/PipeQueue.java` lines 214-230
- Deep Analysis:
  - **CellStack.pop()** line 213 directly `stack.remove(stack.size() - 1)` without empty stack check, throws IndexOutOfBoundsException on empty stack.
  - **CellStack.getUndoCount()** line 248 returns `getCount()`, while push/pop sets count to `stack.size()` (lines 179/214), so getUndoCount returns current stack size instead of undo count. Test `givenStack_whenPop_incUndoCount` expects undoCount = 1 after pop, but actual `getCount()` = 0 (stack is empty after pop) -> FAIL.
  - **Map.fillTiles()** only `fillBeginTile(); prevFilledDistance = distance; prevFilledTiles = 0;` (lines 601-606), no BFS implementation.
  - **PipeQueue.generateNewPipe()** creates `Pa19Package.eINSTANCE.getPa19Factory().createPipe()` generating a Pipe object without shape. Test `givenQueue_afterGivenPipes_assertNewPipesAreGenerated` verifies queue length increase after generation, may fail due to generated Pipe object having incorrect state.
- Error Type: CellStack -> implementation error (pop no empty stack check + getUndoCount returns stack size instead of undo count); Map -> implementation error (fillTiles no BFS implementation); object state error (generateNewPipe creates Pipe without shape)

### PA19019
- Sample: `/PA19/qwen3.6-flash/sample1`
- Summary: (1) Game `undoStep()` first calls `pipeQueue.undo(cell.getPipe())` then calls `map.undo(cell.getCoord())`, but EMF container chain inverse-remove semantics cause pipe reference loss, violating requirement.txt; (2) Map `fillTiles()` cannot propagate because `filledTiles` is empty and BFS depends on filledTiles seed, violating requirement.txt; (3) PipeQueue `generateNewPipe()` generates Pipe object without shape, violating requirement.txt.
- Test Cases: `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`; `PipeQueueTest.givenQueue_afterGivenPipes_assertNewPipesAreGenerated()`
- Functional Code: `/PA19/qwen3.6-flash/sample1/pa19/Game.java` lines 471-481; `/PA19/qwen3.6-flash/sample1/pa19/Map.java` lines 570-717; `/PA19/qwen3.6-flash/sample1/pa19/PipeQueue.java` lines 219-224
- Deep Analysis:
  - **Game.undoStep()** line 477 first `pipeQueue.undo(cell.getPipe())` then line 478 `map.undo(cell.getCoord())`. Same EMF container chain inverse-remove issue as PA19006: in map.undo(), `setPipe(null)` clears popped cell's pipe field through EMF container chain.
  - **Map.fillTiles()** has more complete BFS implementation, but depends on `filledTiles` as initial frontier (line 643). `fillBeginTile()` does not add source coordinate to `filledTiles`, causing BFS to have no seed.
  - **PipeQueue.generateNewPipe()** lines 221-223 `Pa19Package.eINSTANCE.getPa19Factory().createPipe()` generates a Pipe object without shape (PipeShape not set), test verifies generated pipe state is incorrect.
- Error Type: Game -> unable to understand EMF code knowledge (EMF inverse-remove); Map -> implementation error (fillBeginTile did not add source seed); object state error (generateNewPipe creates Pipe without shape)

### PA19020
- Sample: `/PA19/qwen3.6-flash/sample3`
- Summary: (1) CellStack `pop()` throws `IllegalStateException("Stack underflow")` instead of returning null on empty stack, violating requirement.txt; (2) Map `fillTiles()` BFS starts from sourceCell coordinates but source has no pipe, BFS cannot propagate pipes, and `fillBeginTile()` does not add source to `filledTiles`, violating requirement.txt.
- Test Cases: `CellStackTest.givenEmptyStack_ifPop_returnNull()`, `CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`
- Functional Code: `/PA19/qwen3.6-flash/sample3/pa19/CellStack.java` lines 186-217; `/PA19/qwen3.6-flash/sample3/pa19/Map.java` lines 528-580
- Deep Analysis:
  - **CellStack.pop()** lines 188-189 throws `IllegalStateException("Stack underflow")` instead of returning null on empty stack. Both tests fail due to exception.
  - **Map.fillTiles()** starts BFS from sourceCell coordinates (lines 573-576), but sourceCell is a TerminationCell (no pipe), BFS attempts to get current cell's pipe when there is none and cannot continue expansion. Also `fillBeginTile()` does not add source to `filledTiles`.
- Error Type: CellStack -> implementation error (pop throws exception instead of returning null); Map -> implementation error (fillBeginTile did not add source seed + BFS starts from TerminationCell and cannot propagate)

### PA19021
- Sample: `/PA19/qwen3.6-flash/sample4`
- Summary: (1) CellStack `pop()` has no empty stack check causing IndexOutOfBoundsException, violating requirement.txt; (2) Game `placePipe()` creates new FillableCell object instead of using the actual cell in map array, causing cell in cellStack to be a different object from map, all Game tests fail; (3) Map `fillTiles()` starts from sourceCell but `fillBeginTile()` does not add source to filledTiles; (4) PipeQueue `consume()` calls `generateNewPipe()` generating random shape pipes, while test expects specific pipes, violating requirement.txt.
- Test Cases: `CellStackTest.givenEmptyStack_ifPop_returnNull()`, `CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange()`; `GameTest.givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange()`, `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`, `GameTest.givenGame_ifSkipPipe_stepCountIncreases()`, `GameTest.givenGame_ifPipeCanBePlaced_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`; `PipeQueueTest.givenQueue_ifQueueConsumeIsMutable_thenSucceed()`, `PipeQueueTest.givenQueue_ifQueueContainsGivenPipes_thenSucceed()`, `PipeQueueTest.givenQueue_afterGivenPipes_assertNewPipesAreGenerated()`
- Functional Code: `/PA19/qwen3.6-flash/sample4/pa19/CellStack.java` lines 185-213; `/PA19/qwen3.6-flash/sample4/pa19/Game.java` lines 406-486; `/PA19/qwen3.6-flash/sample4/pa19/Map.java` lines 529-613; `/PA19/qwen3.6-flash/sample4/pa19/PipeQueue.java` lines 165-222
- Deep Analysis:
  - **CellStack.pop()** lines 187-188 directly `getStack().remove(size - 1)` without empty stack check, throws IndexOutOfBoundsException on empty stack.
  - **Game.placePipe()** line 416 creates new FillableCell object `Pa19Factory.eINSTANCE.createPa19Helper().createFillableCell(coord, pipe)`, instead of using `getMap().getCell()[row][colIdx]` the actual map cell. This causes the cell in cellStack to be a different object from the cell in the map array, placePipe returns true but cell in cellStack is not in map, causing subsequent undoStep operations to be invalid. Also all GameTests may fail due to placePipe/skipPipe logic not matching test expectations.
  - **Map.fillTiles()** BFS starts from sourceCell coordinates, but `fillBeginTile()` does not add source to filledTiles, causing BFS to have no seed when depending on filledTiles.
  - **PipeQueue.consume()** lines 168-171 calls `generateNewPipe()` automatically generating random shape pipes to fill queue, causing tests `ifQueueConsumeIsMutable` and `ifQueueContainsGivenPipes` to fail due to queue being polluted by random pipes.
- Error Type: CellStack -> return value error; Game -> algorithm implementation error (placePipe creates new cell instead of using map cell); Map -> implementation error (fillBeginTile did not add source seed); object state error (consume auto-fills random pipes)

### PA19022
- Sample: `/PA19/qwen3.6-flash/sample5`
- Summary: (1) CellStack `pop()` throws IndexOutOfBoundsException on empty stack, violating requirement.txt; (2) Pipe `getConnections()` only handles HORIZONTAL/VERTICAL, other shapes (CROSS/TOP_LEFT etc.) throw IllegalStateException, causing BFS and PipeTest to fail, violating requirement.txt; (3) Game `undoStep()` first calls `map.undo()` then `pipeQueue.undo(cell.getPipe())`, EMF inverse-remove semantics cause pipe reference loss; (4) Map `fillTiles()` depends on `filledTiles` seed but fillBeginTile does not add source.
- Test Cases: `CellStackTest.givenEmptyStack_ifPop_returnNull()`, `CellStackTest.givenEmptyStack_whenPop_undoCountDoesNotChange()`; `PipeTest.connection()`; `GameTest.givenGame_ifUndoPipe_stepCountIncreases()`; `MapTest.givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`, `MapTest.givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess()`, `MapTest.givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail()`
- Functional Code: `/PA19/qwen3.6-flash/sample5/pa19/CellStack.java` lines 181-212; `/PA19/qwen3.6-flash/sample5/pa19/Pipe.java` lines 179-200; `/PA19/qwen3.6-flash/sample5/pa19/Game.java` lines 449-460; `/PA19/qwen3.6-flash/sample5/pa19/Map.java` lines 514-633
- Deep Analysis:
  - **CellStack.pop()** lines 183-184 throws `IndexOutOfBoundsException()` instead of returning null on empty stack.
  - **Pipe.getConnections()** lines 181-199 switch only handles HORIZONTAL (LEFT, RIGHT) and VERTICAL (UP, DOWN), other PipeShapes (CROSS, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT) enter default branch throwing `IllegalStateException("Unknown shape")`. This causes `PipeTest.connection()` to fail directly, and `fillTiles()` BFS to crash when encountering non-H/V pipes.
  - **Game.undoStep()** line 456 first `map.undo(coord)` clears cell's pipe, line 457 then `pipeQueue.undo(cell.getPipe())`. Same EMF container chain inverse-remove issue as PA19006/PA19012/PA19019: map.undo()'s setPipe(null) clears popped cell's pipe field through EMF container chain, causing pipeQueue.undo(null).
  - **Map.fillTiles()** line 540 `currentFrontier = ne`

### PA20002
- Sample: `/PA20/deepseek-v4-flash/sample1`
- Summary: JesonMor `start()` increments `numMoves` after `movePiece` but before `getWinner`, causing the protection period check to see one fewer move than actual. Knight/Archer `getAvailableMoves()` does not apply FirstNMovesProtectionRule.
- Test Cases: `GameTieBreakerTests.testMoveAndDeadlock()`; `ArcherTests.testGetAvailableMovesComplex()`
- Functional Code: `/PA20/deepseek-v4-flash/sample1/pa20/JesonMor.java` lines 75-120; `/PA20/deepseek-v4-flash/sample1/pa20/Archer.java` lines 80-115
- Deep Analysis:
  - **JesonMor.start() numMoves timing**: `start()` line 93 increments `numMoves` after `movePiece()` but before `getWinner()`. This means the protection period check in `getWinner()` sees one fewer move than actually made.
  - **Knight/Archer getAvailableMoves()**: Both do not apply FirstNMovesProtectionRule, allowing captures during protection period.
- Error Type: JesonMor -> implementation error (numMoves timing); Knight/Archer -> implementation error (missing FirstNMovesProtectionRule)

### PA20003
- Sample: `/PA20/deepseek-v4-flash/sample2`
- Summary: JesonMor `start()` increments `numMoves` inside `movePiece()`. Knight/Archer `getAvailableMoves()` applies rules but FirstNMovesProtectionRule uses EMF default value 5 instead of reading from Configuration.
- Test Cases: Multiple integration tests
- Functional Code: `/PA20/deepseek-v4-flash/sample2/pa20/JesonMor.java`; `/PA20/deepseek-v4-flash/sample2/pa20/Knight.java`; `/PA20/deepseek-v4-flash/sample2/pa20/Archer.java`
- Deep Analysis:
  - **JesonMor.start() numMoves in movePiece**: `movePiece()` internally calls `setNumMoves(getNumMoves() + 1)`, `start()` also calls `setNumMoves(getNumMoves() + 1)` after `movePiece`, causing double increment.
  - **FirstNMovesProtectionRule default value**: Both Knight and Archer create FirstNMovesProtectionRule but do not call `setNumProtectedMoves(getConfiguration().getNumMovesProtection())`, using EMF default value 5. When tests set numMovesProtection=0, captures are still blocked during first 5 moves.
- Error Type: JesonMor -> implementation error (double increment); Knight/Archer -> implementation error (FirstNMovesProtectionRule not configured)

### PA21007
- Sample: `/PA21/deepseek-v4-flash/sample1`
- Summary: makeMove returns Invalid at boundary/Wall without setting newPosition field; DOWN/RIGHT direction returns Alive instead of Invalid after boundary sliding; Dead result missing origPosition.
- Test Cases: testMakeMoveToBorder(UP/DOWN/LEFT/RIGHT)
- Functional Code: `/PA21/deepseek-v4-flash/sample1/pa21/GameBoardController.java`
- Deep Analysis:
  - **Invalid missing newPosition (confirmed)**: `makeMove()` returns `createInvalid()` directly when first step crosses boundary or hits Wall, without setting `newPosition = currentPos`. All four `testMakeMoveToBorder(UP/DOWN/LEFT/RIGHT)` return Invalid, but `Invalid.getNewPosition()` is null, test NPEs when reading row/col.
  - **Dead path prematurely modifies board (confirmed)**: During movement, when encountering Gem/ExtraLife, immediately `entityCell.setEntity(null)`; if subsequently hits Mine and returns Dead, code does not rollback these modifications. Requirement explicitly states board unchanged on Dead move, and Gems/ExtraLives passed before Mine should not be collected or removed.
  - **model-doc defect (can be preserved)**: `makeMove()`'s model-doc does not completely describe field state when first step returns Invalid with no movement; does not explicitly state Invalid should carry current position as `newPosition`.
- Error Type: Invalid -> code defect; Dead -> model-doc defect

### PA21008
- Sample: `/PA21/deepseek-v4-flash/sample2`
- Summary: makeMove returns Dead when encountering Mine but does not set origPosition field.
- Test Cases: testMakeValidMoveToMine()
- Functional Code: `/PA21/deepseek-v4-flash/sample2/pa21/GameBoardController.java`
- Deep Analysis:
  - **Dead missing origPosition (code defect)**: Lines 236-241, after detecting Mine creates Dead object, sets `newPosition=currentPos` and `minePosition=nextPos`, but **does not call `dead.setOrigPosition(originalPos)`**. Test line 402 `deadResult.getOrigPosition().getRow()` NPEs because origPosition is null.
  - **model-doc for Dead (model-doc defect)**: model-doc line 136 "If entity is Mine: return Dead with newPosition = currentPos, minePosition = nextPos". Only describes newPosition and minePosition, omits origPosition.
- Error Type: Object state

### PA21009
- Sample: `/PA21/deepseek-v4-flash/sample4`
- Summary: makeMove returns Dead when encountering Mine but does not set origPosition field.
- Test Cases: testMakeMoveToBorder(UP), testMakeMoveToBorder(DOWN), testMakeMoveToBorder(LEFT)
- Functional Code: `/PA21/deepseek-v4-flash/sample4/pa21/GameBoardController.java`
- Deep Analysis:
  - **Invalid newPosition uses originalPosition reference**: sample4 line 231 `invalid.setNewPosition(originalPosition)` — originalPosition is `gameBoard.getPlayer().getOwner().getPosition()` return value (line 161). In EMF, getPosition() returns Position reference. If subsequent code modifies this Position object, Invalid's newPosition would also be affected.
- Error Type: testMakeMoveToBorder -> code defect (to be further confirmed); Dead -> model-doc defect

### PA21010
- Sample: `/PA21/deepseek-v4-flash/sample5`
- Summary: makeMove returns Invalid at boundary without setting newPosition; Dead result missing origPosition and newPosition.
- Test Cases: testMakeMoveToBorder(UP/DOWN/LEFT/RIGHT), testMakeValidMoveToMine()
- Functional Code: `/PA21/deepseek-v4-flash/sample5/pa21/GameBoardController.java`
- Deep Analysis:
  - **Invalid missing newPosition (code defect)**: Lines 188-196, when first step crosses boundary or hits Wall, directly `return Pa21Factory.eINSTANCE.createInvalid()` without calling `invalid.setNewPosition(...)`. All four directions fail for this reason. Test NPEs because newPosition is null.
  - **Dead missing origPosition and newPosition (code defect)**: Lines 229-233, after detecting Mine creates Dead object, only sets `dead.setMinePosition(nextPos)`. **Does not set `dead.setOrigPosition(origPos)` and `dead.setNewPosition(origPos)`**.
  - **model-doc for Dead (model-doc defect)**: model-doc line 133 only describes minePosition. Does not specify Dead needs newPosition and origPosition.
  - **model-doc for Invalid (model-doc defect)**: model-doc line 126 "return Invalid (no movement)" does not specify Invalid needs newPosition = current position.
- Error Type: Invalid/Dead -> model-doc defect + code defect

### PA21011
- Sample: `/PA21/gpt-5.4-mini/sample2`
- Summary: makeMove stops one cell before StopCell (model-doc correct but code error); Alive/Dead do not set origPosition (model-doc defect); undoMove uses player.getOwner().getPosition() instead of alive.getOrigPosition().
- Test Cases: testUndoMoveWithPickups(), testMakeValidMovePassingGem(), testUndoMoveTrivial(), testMakeValidMovePassingExtraLives(), testMakeValidMoveToStopCell(), testMakeValidMoveToMine(), testMakeValidMoveToWall()
- Functional Code: `/PA21/gpt-5.4-mini/sample2/pa21/GameBoardController.java` lines 169-293(makeMove), 325-350(undoMove)
- Deep Analysis:
  - **StopCell stops one cell before (code error - model-doc correct)**: model-doc line 137 "StopCell is reached: stop exactly on the StopCell". Code lines 242-256 in loop first `current = next` (line 241), then checks if farther is StopCell (line 258). If StopCell, breaks without updating current to StopCell position.
  - **Alive missing origPosition (model-doc defect)**: model-doc line 138 only says "return Alive with: newPosition set to the final player position", **does not require setting origPosition**.
  - **Dead missing origPosition (model-doc defect)**: model-doc line 129 only describes Dead.newPosition, does not mention Dead.origPosition.
  - **undoMove reads wrong position (code error)**: model-doc line 304 "Restore the player entity to the original position recorded in the Alive move". Code line 337 `Position origPosition = player.getOwner().getPosition()` reads from Player's current owner cell (i.e., new position after move), not from `alive.getOrigPosition()`.
- Error Type: StopCell -> code error; Alive/Dead origPosition -> model-doc defect; undoMove -> code error

### PA21012
- Sample: `/PA21/gpt-5.4-mini/sample3`
- Summary: makeMove returns Invalid without setting newPosition; Alive does not set origPosition; StopCell stops one cell before; Dead does not set origPosition.
- Test Cases: Same as PA21011
- Functional Code: `/PA21/gpt-5.4-mini/sample3/pa21/GameBoardController.java` lines 145-248(makeMove), 271-303(undoMove)
- Deep Analysis:
  - **Invalid missing newPosition (model-doc defect)**: model-doc line 124 does not specify Invalid needs newPosition.
  - **Alive missing origPosition (model-doc defect)**: model-doc line 138 does not require setting origPosition.
  - **Dead missing origPosition (model-doc defect)**: model-doc line 125 does not mention origPosition.
  - **StopCell stops before (code error)**: Code line 221 `entity instanceof Entity` always true (any non-null entity triggers break).
  - **undoMove did not move Player (EMF parent container broken chain)**: Line 282 `player.setOwner(null)` breaks player-board association, but never puts player back to origPosition's cell.
- Error Type: Invalid/Alive/Dead -> model-doc defect; StopCell -> code error; undoMove -> code error

### PA21013
- Sample: `/PA21/gpt-5.4-mini/sample4`
- Summary: makeMove returns Invalid/Alive/Dead all missing critical position fields; undoMove completely fails to implement player position restoration.
- Test Cases: Same as PA21011
- Functional Code: `/PA21/gpt-5.4-mini/sample4/pa21/GameBoardController.java` lines 183-266(makeMove), 297-344(undoMove)
- Deep Analysis:
  - **Invalid missing newPosition (model-doc defect)**: model-doc does not specify newPosition.
  - **Alive missing origPosition (model-doc defect)**: model-doc does not mention origPosition.
  - **Dead missing origPosition (model-doc defect)**: model-doc does not mention origPosition.
  - **undoMove did not move Player (code error)**: Code only checks if orig exists, then calls `board.setPlayer(board.getPlayer())` (no-op, does not change player position).
  - **makeMove did not update Player Cell (code error)**: Code creates Alive and removes collected items, but never moves player from start to current position.
- Error Type: Invalid/Alive/Dead -> model-doc defect; undoMove -> code error; makeMove -> code error

### PA21014
- Sample: `/PA21/gpt-5.4-mini/sample5`
- Summary: makeMove returns Invalid without setting newPosition; Alive sets origPosition but undoMove has error; Player Cell not properly updated.
- Test Cases: Same as PA21011
- Functional Code: `/PA21/gpt-5.4-mini/sample5/pa21/GameBoardController.java` lines 158-277(makeMove), 308-339(undoMove)
- Deep Analysis:
  - **Invalid missing newPosition (model-doc defect)**: model-doc does not specify newPosition.
  - **Dead missing origPosition (code error)**: Code does not set `dead.setOrigPosition(originalPosition)`.
  - **undoMove only changes Position not Cell (code error - model-doc correct)**: model-doc line 287 "Restore the player's position to the origPosition". Code line 319 `player.getOwner().setPosition(alive.getOrigPosition())` directly modifies EntityCell's position attribute, instead of moving player from current cell to origPosition cell.
  - **makeMove Player Cell update**: Code line 275 `player.getOwner().setPosition(currentPosition)` modifies owner cell's position, but does not move player entity in board array.
- Error Type: Invalid -> model-doc defect; Dead -> code error; undoMove -> code error; makeMove -> code error

### PA21015
- Sample: `/PA21/minimax-m3/sample1`
- Summary: model-doc is empty; makeMove stops sliding at Mine/Gem/ExtraLife instead of continuing/returning correct result; Invalid missing newPosition; undoMove creates new EntityCell detached from board.
- Test Cases: Same as PA21011
- Functional Code: `/PA21/minimax-m3/sample1/pa21/GameBoardController.java` lines 125-259(makeMove), 270-324(undoMove)
- Deep Analysis:
  - **Invalid missing newPosition (code error)**: Lines 220-221 and 227-228 create Invalid without setting newPosition.
  - **Stops sliding at Mine/Gem/ExtraLife (code error - violates requirement)**: Code lines 185-207, when encountering Mine `break` (correct, should stop), but when encountering Gem also `break` (line 198) and sets `collectedGem=true`, when encountering ExtraLife also `break` (line 206). Requirement: "any Gem or ExtraLife passed through is collected, removed from the board, and recorded in the Alive result" — should continue sliding.
  - **Dead returns wrong newPosition (code error)**: Code lines 240-244, Dead sets `dead.setNewPosition(current)` where current is Mine position, not original position.
  - **undoMove creates new EntityCell detached from board (code error - EMF knowledge issue)**: Code lines 286-289 create new EntityCell and directly `board.getBoard()[row][col] = origEntityCell`. Line 322 clears newPosition cell to null.
- Error Type: Invalid -> code error; Gem/ExtraLife stop sliding -> code error; Dead newPosition -> code error; undoMove -> code error + EMF knowledge error

### PA21016
- Sample: `/PA21/minimax-m3/sample2`
- Summary: Invalid missing newPosition; in Dead path, player has already been moved from origCell but not restored.
- Test Cases: testMakeMoveToBorder(UP/DOWN/LEFT/RIGHT), testMakeValidMoveToMine()
- Functional Code: `/PA21/minimax-m3/sample2/pa21/GameBoardController.java` lines 195-299(makeMove), 336-361(undoMove)
- Deep Analysis:
  - **Invalid missing newPosition (model-doc defect)**: model-doc line 178 does not specify newPosition.
  - **Player not restored after Dead (code error)**: Code lines 264-268 in sliding first step executes `origCell.setentity(null)` removing player, then moves player to next cell. When subsequently encountering Mine, code lines 272-287 only restores collected items, **does not move player from current position back to origCell**.
- Error Type: Invalid -> model-doc defect; Dead player restoration -> code error

### PA21017
- Sample: `/PA21/minimax-m3/sample5`
- Summary: Only Invalid missing newPosition, all other logic is correct.
- Test Cases: testMakeMoveToBorder(UP/DOWN/LEFT/RIGHT)
- Functional Code: `/PA21/minimax-m3/sample5/pa21/GameBoardController.java` lines 200-318(makeMove), 384+(undoMove)
- Deep Analysis:
  - **Invalid missing newPosition (model-doc defect)**: model-doc line 179 does not specify newPosition. Code lines 308-309 `Invalid invalid = factory.createInvalid(); return invalid;` without setting newPosition.
- Error Type: Invalid -> model-doc defect

### PA21018
- Sample: `/PA21/gemini-3.1-flash-lite/sample1`
- Summary: Invalid missing newPosition; Alive missing origPosition; undoMove does not clear player from new position.
- Test Cases: 11 (all GameBoardControllerTest)
- Functional Code: `/PA21/gemini-3.1-flash-lite/sample1/pa21/GameBoardController.java` lines 141-219(makeMove), 241+(undoMove)
- Deep Analysis:
  - **Invalid missing newPosition (model-doc defect)**: model-doc line 123 does not specify newPosition.
  - **Alive missing origPosition (model-doc defect)**: model-doc does not mention origPosition.
  - **Dead missing origPosition (code error)**: Lines 172-174 `dead.setNewPosition(currentPos)` uses currentPos (should be origPos) and does not set origPosition.
  - **undoMove does not clear player from new position (code error)**: Causes `expected: <null> but was: <Player>`.
- Error Type: Invalid -> model-doc defect; Alive origPosition -> model-doc defect; undoMove -> code error

### PA21019
- Sample: `/PA21/gemini-3.1-flash-lite/sample2`
- Summary: Invalid missing newPosition; first step check logic error (non-StopCell EntityCell treated as Wall); Gem not removed from board; Alive missing origPosition.
- Test Cases: 11 (all GameBoardControllerTest)
- Functional Code: `/PA21/gemini-3.1-flash-lite/sample2/pa21/GameBoardController.java` lines 139-211(makeMove), 234+(undoMove)
- Deep Analysis:
  - **Invalid missing newPosition (model-doc defect)**.
  - **First step check logic error (code error)**: Line 158 `if (nextCell instanceof Wall || (nextCell instanceof EntityCell && !(nextCell instanceof StopCell)))` treats non-StopCell EntityCell as "wall".
  - **Gem not removed from board (code error)**.
  - **undoMove does not clear player (code error)**.
- Error Type: Invalid -> model-doc defect; first step check -> code error; Gem removal -> code error; undoMove -> code error

### PA21020
- Sample: `/PA21/gemini-3.1-flash-lite/sample4`
- Summary: Invalid missing newPosition; Alive missing origPosition; undoMove does not clear player. Same defect pattern as PA21018.
- Test Cases: 11 (all GameBoardControllerTest)
- Functional Code: `/PA21/gemini-3.1-flash-lite/sample4/pa21/GameBoardController.java` lines 137-218(makeMove), 241+(undoMove)
- Error Type: Invalid -> model-doc defect; Alive/Dead origPosition -> model-doc defect; undoMove -> code error

### PA21021
- Sample: `/PA21/gemini-3.1-flash-lite/sample5`
- Summary: Exactly same defect pattern as PA21018/PA21020.
- Test Cases: 11 (all GameBoardControllerTest)
- Functional Code: `/PA21/gemini-3.1-flash-lite/sample5/pa21/GameBoardController.java` lines 140-208(makeMove), 231+(undoMove)
- Error Type: Invalid -> model-doc defect; Alive/Dead origPosition -> model-doc defect; undoMove -> code error

### PA21022
- Sample: `/PA21/qwen3.6-flash/sample2`
- Summary: GameBoardController layer: undoMove does not clear player, StopCell stops before, Dead missing newPosition. GameController layer: cascade failure.
- Test Cases: 7 GameBoardControllerTest + 7 GameControllerTest
- Functional Code: `/PA21/qwen3.6-flash/sample2/pa21/GameBoardController.java` lines 132-199(makeMove), 217+(undoMove)
- Deep Analysis:
  - **GameBoardController failures**: undoMove does not clear player; StopCell stops before; testMakeValidMoveToStopCell fails; Dead missing newPosition; testMakeValidMoveToWall fails.
  - **GameController failures (cascade)**: testUndoMoveRestoresExtraLife, testUndoMoveRestoresEntities fail because undoMove does not correctly restore state.
  - **model-doc analysis**: model-doc lines 120-128 are extremely minimal, almost no algorithm description.
- Error Type: undoMove -> code error; StopCell -> code error; Dead -> code error; GameController -> cascade failure; model-doc -> defect

### PA21023
- Sample: `/PA21/qwen3.6-flash/sample5`
- Summary: MoveStack.moves not initialized (3 MoveStackTest failures); Invalid missing newPosition (4 GameBoardControllerTest failures); GameController complete failure (44 parameterized tests).
- Test Cases: 3 MoveStackTest + 4 GameBoardControllerTest + 44 GameControllerTest (total 51)
- Functional Code: `/PA21/qwen3.6-flash/sample5/pa21/GameBoardController.java` lines 166-223(makeMove), 253+(undoMove); `/PA21/qwen3.6-flash/sample5/pa21/MoveStack.java`
- Deep Analysis:
  - **MoveStack.moves not initialized (code error - EMF knowledge issue)**: MoveStackTest NPE `"this.moves" is null`. MoveStack's moves field is EList type, should be initialized in constructor or initialization, but code does not initialize.
  - **Invalid missing newPosition (model-doc defect)**: Code creates Invalid without setting newPosition.
  - **GameController complete failure (cascade)**: MoveStack push/pop failures + makeMove Invalid failures -> all dependent GameControllerTests fail.
  - **Alive/Dead already set origPosition (code correct)**: Lines 219-220 and 196-197 correctly set origPosition.
- Error Type: MoveStack -> code error (EMF collection not initialized); Invalid -> model-doc defect; GameController -> cascade failure

### PA22021
- Sample: `/PA22/deepseek-v4-flash/sample1`
- Summary: processAction does not perform defensive check for null player; gameLoop does not call getUndoQuota; render character error; undo does not restore position.
- Test Cases: testMoveNonExistingPlayer(), testGameLoop(), testRender(), testUndoWhenThereIsCheckpoint()
- Functional Code: `/PA22/deepseek-v4-flash/sample1/pa22/AbstractSokobanGame.java` line 240+(processAction)
- Deep Analysis:
  - **testMoveNonExistingPlayer NPE (model-doc defect)**: NPE `Position.getX() because parameter1 is null` at `Down.nextPosition(Down.java:61)` from `AbstractSokobanGame.processAction(250)`. processAction directly accesses player's position when processing Move, without checking if player is on board. model-doc does not specify defensive check for non-existent player.
  - **testGameLoop Mockito failure (code error)**: `Wanted but not invoked: gameState.getUndoQuota()`. Test expects gameLoop to call getUndoQuota() in loop to check undo quota, but code does not call it in loop.
  - **testRender render character error (code error)**: `expected: <a> but was: <$>`. Render engine uses wrong character mapping for an entity ($ instead of a).
  - **testUndoWhenThereIsCheckpoint position error (code error)**: `expected: <Position@400 (x:2,y:1)> but was: <null>`. After undo, player's position is null, indicating undo mechanism does not correctly restore player's position.
- Error Type: NPE -> model-doc defect; gameLoop -> code error; render -> code error; undo -> code error

### PA22022
- Sample: `/PA22/deepseek-v4-flash/sample2`
- Summary: checkpoint called on every move instead of only on box push; undoQuota not checked; NPE null player; pushOtherPlayerBox logic error.
- Test Cases: testExceedingUndoQuota(), testPushOtherPlayerBox(), testMoveNonExistingPlayer(), testCheckpointWhenNotNeed(), testGameLoop(), testUndoWhenThereIsCheckpoint()
- Functional Code: `/PA22/deepseek-v4-flash/sample2/pa22/AbstractSokobanGame.java` line 198+(processAction)
- Deep Analysis:
  - **testCheckpointWhenNotNeed (code error - model-doc correct)**: Mockito `NeverWantedButInvoked: gameState.checkpoint()` at processAction(232). Code calls checkpoint() on every move, while requirement explicitly says "a checkpoint is recorded when a box is successfully pushed".
  - **testExceedingUndoQuota (code error)**: `expected: <true> but was: <false>`. Undo quota exceeded not correctly rejected due to excessive checkpoint recording + wrong undoQuota check logic.
  - **testPushOtherPlayerBox (code error)**: `expected: <true> but was: <false>`. Pushing other player's box not correctly rejected.
  - **testMoveNonExistingPlayer NPE (model-doc defect)**: Same as PA22021.
  - **testGameLoop (code error)**: Same as PA22021.
  - **testUndoWhenThereIsCheckpoint (code error)**: position error, same as PA22021.
- Error Type: checkpoint -> code error; undoQuota -> code error; NPE -> model-doc defect; pushOtherPlayerBox -> code error

### PA22023
- Sample: `/PA22/deepseek-v4-flash/sample3`
- Summary: checkpoint called every move; NPE null player; render char error; undo position error.
- Test Cases: testExceedingUndoQuota(), testMoveNonExistingPlayer(), testGameLoop(), testRender(), testUndoWhenThereIsCheckpoint()
- Functional Code: `/PA22/deepseek-v4-flash/sample3/pa22/AbstractSokobanGame.java`
- Deep Analysis:
  - **testExceedingUndoQuota (code error)**: Same as PA22022.
  - **testMoveNonExistingPlayer NPE (model-doc defect)**: Same as PA22021/PA22022.
  - **testGameLoop (code error)**: Same as PA22021.
  - **testRender render char error (code error)**: `expected: < > but was: <.>`. Space should display as ' ' but displays as '.'. Different from PA22021's `$` error, indicating inconsistent render mapping.
  - **testUndoWhenThereIsCheckpoint (code error)**: `expected: <Position@400 (x:2,y:1)> but was: <Position@43e (x:4,y:1)>`. After undo, player position is wrong value (4,1 instead of 2,1), indicating undo mechanism does not correctly restore to checkpoint position.
- Error Type: undoQuota -> code error; NPE -> model-doc defect; render -> code error; undo position -> code error

### PA22024
- Sample: `/PA22/deepseek-v4-flash/sample4`
- Summary: checkpoint called every move; Player1 input parsing error; gameLoop missing getUndoQuota; render line count error.
- Test Cases: testExceedingUndoQuota(), testMoveNonExistingPlayer(), testGameLoop(), testGameWin(), testRender(), TerminalInputEngineTest.testMove() [#5,#6,#7], testUndoWhenThereIsCheckpoint()
- Functional Code: `/PA22/deepseek-v4-flash/sample4/pa22/AbstractSokobanGame.java` line 232+(processAction)
- Deep Analysis:
  - **testCheckpointWhenNotNeed (code error)**: Same as PA22022.
  - **TerminalInputEngineTest.testMove [#5 H,Left,1] [#6 J,Down,1] [#7 K,Up,1] (code error)**: `expected: <true> but was: <false>`. Player 1's input keys (H/J/K/L) not correctly parsed to directions.
  - **testGameLoop (code error)**: Same as PA22021.
  - **testGameWin (code error)**: Mockito verification failure.
  - **testRender (code error)**: `expected: <7> but was: <8>`. Render line count error.
  - **testMoveNonExistingPlayer NPE (model-doc defect)**: Same as PA22021.
  - **testUndoWhenThereIsCheckpoint (code error)**: Same as PA22022/PA22023.
- Error Type: checkpoint -> code error; Player1 input -> code error; gameLoop -> code error; render -> code error; NPE -> model-doc defect

### PA22025
- Sample: `/PA22/deepseek-v4-flash/sample5`
- Summary: checkpoint called every move; undoQuota not checked; NPE null player; pushOtherPlayerBox logic error.
- Test Cases: testExceedingUndoQuota(), testPushOtherPlayerBox(), testMoveNonExistingPlayer(), testCheckpointWhenNotNeed(), testGameLoop(), testUndoWhenThereIsCheckpoint()
- Functional Code: `/PA22/deepseek-v4-flash/sample5/pa22/AbstractSokobanGame.java`
- Error Type: checkpoint -> code error; pushOtherPlayerBox -> code error; NPE -> model-doc defect; gameLoop -> code error; undo position -> code error

### PA22006
- Sample: `/PA22/gemini-3.1-flash-lite/sample1`
- Summary: Exactly same 4 failure patterns as PA22021 (deepseek-v4-flash/sample1).
- Test Cases: testMoveNonExistingPlayer(), testGameLoop(), testRender(), testUndoWhenThereIsCheckpoint()
- Error Type: NPE -> model-doc defect; gameLoop -> code error; render -> code error; undo -> code error

### PA22007
- Sample: `/PA22/gemini-3.1-flash-lite/sample2`
- Summary: NPE null player; gameLoop missing getUndoQuota; pushBox insufficient calls; isWin judgment error.
- Test Cases: testPushBox(), testMoveNonExistingPlayer(), testGameLoop(), testWin(), testUndoWhenThereIsCheckpoint()
- Functional Code: `/PA22/gemini-3.1-flash-lite/sample2/pa22/AbstractSokobanGame.java` line 216+(processAction)
- Deep Analysis:
  - **testPushBox (code error)**: Mockito `TooFewActualInvocations`. Code does not correctly call gameState.move() to execute box push.
  - **testMoveNonExistingPlayer NPE (model-doc defect)**: Same as PA22006.
  - **testGameLoop (code error)**: Same as PA22006.
  - **testWin (code error)**: `expected: <true> but was: <false>`. isWin() judgment error, possibly box-destination matching logic incorrect.
  - **testUndoWhenThereIsCheckpoint (code error)**: `expected: <Position@400 (x:2,y:1)> but was: <Position@41f (x:3,y:1)>`. Undo restores to wrong position.
- Error Type: pushBox -> code error; NPE -> model-doc defect; gameLoop -> code error; isWin -> code error; undo position -> code error

### PA22008
- Sample: `/PA22/minimax-m3/sample1`
- Summary: GameState.history Stack not initialized (EMF knowledge error) causing undo-related test NPE; pushBox direction error; NPE null player.
- Test Cases: testUndoUnlimited(), testPushBox(), testMoveNonExistingPlayer(), testUndoWithinQuota(), testGameLoop(), testWin(), testMove(), testPushBox(GameStateTest), testUndoWhenThereIsCheckpoint()
- Functional Code: `/PA22/minimax-m3/sample1/pa22/AbstractSokobanGame.java` line 271+(processAction); `/PA22/minimax-m3/sample1/pa22/GameState.java`
- Deep Analysis:
  - **GameState.history Stack not initialized (EMF knowledge error)**: GameState's `history` field declared as `protected Stack<GameStateTransition> history;` (line 141), no initialization. model-doc also does not specify need for initialization. EMF generated code does not automatically initialize collection fields. Causes testUndoUnlimited/testUndoWithinQuota NPE.
  - **testMoveNonExistingPlayer NPE (model-doc defect)**: Same as PA22006/PA22007.
  - **testPushBox (code error)**: `expected: <Position@400 (x:2,y:1)> but was: <Position@3e1 (x:1,y:1)>`. After push, box position error (x=1 instead of x=2), indicating push direction calculation error.
  - **testGameLoop (code error)**: Same as PA22006.
  - **testWin (code error)**: Same as PA22007.
  - **testMove/testPushBox(GameStateTest) (code error)**: After move, position error.
  - **testUndoWhenThereIsCheckpoint (code error)**: Position restores to wrong value.
- Error Type: history Stack -> EMF knowledge error; NPE -> model-doc defect; pushBox/move -> code error; gameLoop -> code error; isWin -> code error

### PA22009
- Sample: `/PA22/minimax-m3/sample2`
- Summary: history Stack not initialized; gameLoop missing getUndoQuota; render line count error; undo position error.
- Test Cases: testUndoWithinQuota(), testGameLoop(), testRender(), testUndoWhenThereIsCheckpoint()
- Error Type: history Stack -> EMF knowledge error; gameLoop -> code error; render -> code error; undo position -> code error

### PA22010
- Sample: `/PA22/minimax-m3/sample3`
- Summary: history Stack not initialized causing multiple undo test failures; gameLoop/gameExit missing calls; undo position error.
- Test Cases: testUndoUnlimited(), testUndoWithinQuota(), testGameExit(), testGameLoop(), testGameWin(), testUndoWhenThereIsCheckpoint()
- Error Type: history Stack -> EMF knowledge error; gameLoop/gameExit/gameWin -> code error; undo position -> code error

### PA22011
- Sample: `/PA22/minimax-m3/sample4`
- Summary: GameStateTransition.moves Map not initialized (EMF knowledge error) causing checkpoint-related test NPE; pushBox insufficient calls; NPE null player; gameLoop missing getUndoQuota.
- Test Cases: 11 (see test list)
- Functional Code: `/PA22/minimax-m3/sample4/pa22/AbstractSokobanGame.java`; `/PA22/minimax-m3/sample4/pa22/GameStateTransition.java`
- Deep Analysis:
  - **GameStateTransition.moves Map not initialized (EMF knowledge error)**: GameStateTransition's `moves` field declared as `protected Map<Position, Position> moves;`, no initialization. EMF generated code does not automatically initialize Map fields. When checkpoint() calls transition.getMoves().isEmpty() triggers NPE.
  - **testMoveNonExistingPlayer NPE (model-doc defect)**: Same as PA22006/PA22007.
  - **testPushBox Mockito (code error)**: `TooFewActualInvocations`.
  - **testHitAnotherPlayer (code error)**: `expected: <true> but was: <false>`.
  - **testUndoWithinQuota (code error)**: Mockito `Wanted but not invoked: gameState.undo()`.
  - **testGameLoop (code error)**: Same as other samples.
  - **testRender (code error)**: `expected: <7> but was: <9>`.
  - **GameStateTest.testPushBox (code error)**: Box position error.
  - **GameStateTest.testUndoWhenThereIsCheckpoint (code error)**: Undo does not correctly restore state.
- Error Type: Transition.moves -> EMF knowledge error; NPE -> model-doc defect; pushBox/hitAnotherPlayer -> code error; undoWithinQuota -> code error; gameLoop -> code error; render -> code error

### PA22012
- Sample: `/PA22/minimax-m3/sample5`
- Summary: history Stack not initialized (same as PA22008-PA22010); gameLoop missing getUndoQuota; render char error; undo position null.
- Test Cases: 6 (see test list)
- Error Type: history Stack -> EMF knowledge error; gameLoop -> code error; render -> code error; undo position -> code error

### PA22013
- Sample: `/PA22/gpt-5.4-mini/sample1`
- Summary: GameStateTransition.moves Map not initialized (EMF knowledge error) causing large number of NPE; pushBox/hitAnotherPlayer logic errors; gameLoop missing getUndoQuota. Most failures (13).
- Test Cases: 13 (see test list)
- Error Type: Transition.moves -> EMF knowledge error; pushBox/hitAnotherPlayer -> code error; gameLoop -> code error; render -> code error; isWin/move/undo -> code error

### PA22014
- Sample: `/PA22/gpt-5.4-mini/sample2`
- Summary: gameLoop missing getUndoQuota; render char error (B instead of a); undo position null.
- Test Cases: 5 (see test list)
- Error Type: gameLoop -> code error; render -> code error; undo -> code error

### PA22015
- Sample: `/PA22/gpt-5.4-mini/sample3`
- Summary: checkpoint called every move instead of only on box push; gameLoop/gameExit/gameWin missing calls; render line count error; undo position null.
- Test Cases: 6 (see test list)
- Error Type: checkpoint -> code error; pushBox -> code error; gameLoop -> code error; render -> code error; undo position -> code error

### PA22016
- Sample: `/PA22/gpt-5.4-mini/sample4`
- Summary: history Stack not initialized (EMF knowledge error) causing undo test NPE; checkpoint called every move; render char error (B vs a); gameLoop missing getUndoQuota.
- Test Cases: testExceedingUndoQuota(), testUndoUnlimited(), testCheckpointWhenNotNeed(), testUndoWithinQuota(), testGameLoop(), testRender(), testUndoWhenThereIsCheckpoint()
- Error Type: history Stack -> EMF knowledge error; checkpoint -> code error; gameLoop -> code error; render -> code error; undo position -> code error

### PA22017
- Sample: `/PA22/gpt-5.4-mini/sample5`
- Summary: testExceedingUndoQuota logic error; gameLoop missing getUndoQuota; render char error (B vs a); undo position null. This sample has no history Stack NPE (history is initialized).
- Test Cases: testExceedingUndoQuota(), testGameLoop(), testRender(), testUndoWhenThereIsCheckpoint()
- Error Type: undoQuota -> code error; gameLoop -> code error; render -> code error; undo position -> code error

### PA22018
- Sample: `/PA22/qwen3.6-flash/sample1`
- Summary: checkpoint called every move; pushBox insufficient calls; hitWall/hitAnotherPlayer logic errors; gameLoop missing getUndoQuota; render char error. Most failures (11).
- Test Cases: testUndoUnlimited(), testPushOtherPlayerBox(), testPushBox(), testPushBoxAgainstWall(), testCheckpointWhenNotNeed(), testHitAnotherPlayer(), testHitWall(), testGameLoop(), testRender(), testWin(), testMove()
- Error Type: checkpoint -> code error; pushBox/hitWall/hitAnotherPlayer -> code error; gameLoop -> code error; render -> code error; isWin/move -> code error

### PA22019
- Sample: `/PA22/qwen3.6-flash/sample2`
- Summary: history Stack not initialized; pushBox excessive calls; NPE null player; gameLoop missing getUndoQuota; render char error (. vs space).
- Test Cases: testUndoUnlimited(), testPushBox(), testMoveNonExistingPlayer(), testCheckpointWhenNeed(), testUndoWithinQuota(), testGameLoop(), testRender(), testWin(), testUndoWhenThereIsCheckpoint()
- Error Type: history Stack -> EMF knowledge error; pushBox -> code error (excessive calls); NPE -> model-doc defect; gameLoop -> code error; render -> code error

### PA22020
- Sample: `/PA22/qwen3.6-flash/sample5`
- Summary: history Stack not initialized; NPE null player; GameState.move incorrectly casts Box to Player (ClassCastException); gameLoop missing getUndoQuota; render char error.
- Test Cases: testUndoUnlimited(), testPushBox(), testMoveNonExistingPlayer(), testUndoWithinQuota(), testGameLoop(), testRender(), testWin(), testPushBox(GameStateTest), testUndoWhenThereIsCheckpoint()
- Deep Analysis:
  - **testUndoUnlimited/testUndoWithinQuota (EMF knowledge error)**: NPE, same as other minimax/qwen samples.
  - **testPushBox (code error)**: Mockito `TooFewActualInvocations`.
  - **testMoveNonExistingPlayer NPE (model-doc defect)**: Same as other samples.
  - **GameStateTest.testWin/testPushBox (code error - EMF type knowledge issue)**: `ClassCastException: class edu.pa22.Box cannot be cast to class edu.pa22.Player` at GameState.move(432). GameState.move() incorrectly casts Box to Player when moving entity.
  - **testGameLoop (code error)**: Same as other samples.
  - **testRender (code error)**: `expected: <a> but was: <$>`.
  - **testUndoWhenThereIsCheckpoint (code error)**: `expected: <Position> but was: <null>`.
- Error Type: history Stack -> EMF knowledge error; NPE -> model-doc defect; Box->Player cast -> code error (EMF type knowledge issue); gameLoop -> code error; render -> code error; undo position -> code error

