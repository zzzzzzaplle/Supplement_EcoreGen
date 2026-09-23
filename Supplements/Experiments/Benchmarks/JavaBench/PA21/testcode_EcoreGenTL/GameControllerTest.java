package edu.pa21;

import edu.pa21.util.GameBoardHelper;
import edu.pa21.util.GameBoardUtils;
import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class GameControllerTest {

    private GameBoard gameBoard = null;
    private GameState gameState = null;
    private GameController controller = null;

    // 辅助函数：创建Position对象
    private static Position createPosition(int row, int col) {
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        return pos;
    }

    // 辅助函数：创建Player对象
    private static Player createPlayer() {
        return Pa21Factory.eINSTANCE.createPlayer();
    }

    // 辅助函数：创建Gem对象
    private static Gem createGem() {
        return Pa21Factory.eINSTANCE.createGem();
    }

    // 辅助函数：创建Mine对象
    private static Mine createMine() {
        return Pa21Factory.eINSTANCE.createMine();
    }

    // 辅助函数：创建ExtraLife对象
    private static ExtraLife createExtraLife() {
        return Pa21Factory.eINSTANCE.createExtraLife();
    }

    // 辅助函数：创建EntityCell对象
    private static EntityCell createEntityCell(Position pos, Entity entity) {
        EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
        cell.setPosition(pos);
        if (entity != null) {
            cell.setEntity(entity);
        }
        return cell;
    }

    // 辅助函数：创建StopCell对象
    private static StopCell createStopCell(Position pos, Entity entity) {
        StopCell cell = Pa21Factory.eINSTANCE.createStopCell();
        cell.setPosition(pos);
        if (entity != null) {
            cell.setEntity(entity);
        }
        return cell;
    }

    // 辅助函数：创建Wall对象
    private static Wall createWall(Position pos) {
        Wall wall = Pa21Factory.eINSTANCE.createWall();
        wall.setPosition(pos);
        return wall;
    }

    // 辅助函数：创建MoveStack对象
    private static MoveStack createMoveStack() {
        return Pa21Factory.eINSTANCE.createMoveStack();
    }

    // 辅助函数：创建GameState
    private static GameState createGameState(GameBoard board, Integer numLives) {
        GameState state = Pa21Factory.eINSTANCE.createGameState();
        state.setGameBoard(board);
        state.setMoveStack(createMoveStack());
        
        state.setInitialNumOfGems(countGems(board));
        if (numLives == null) {
            state.setNumLives(-1); // 无限生命
        } else {
            state.setNumLives(numLives);
        }
        return state;
    }

    // 辅助函数：创建GameController
    private static GameController createController(GameState state) {
        GameController controller = Pa21Factory.eINSTANCE.createGameController();
        controller.setGameState(state);
        return controller;
    }

    // 辅助函数：统计宝石数量
    private static int countGems(GameBoard board) {
        int gems = 0;
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumCols(); c++) {
                Cell cell = board.getCell1(r, c);
                if (cell instanceof EntityCell) {
                    if (((EntityCell) cell).getEntity() instanceof Gem) {
                        gems++;
                    }
                }
            }
        }
        return gems;
    }

    // 辅助函数：创建Alive移动结果
    private static Alive createAliveMove(Position newPosition, Position origPosition,
                                          java.util.List<Position> collectedGems,
                                          java.util.List<Position> collectedExtraLives) {
        Alive alive = Pa21Factory.eINSTANCE.createAlive();
        alive.setNewPosition(newPosition);
        alive.setOrigPosition(origPosition);
        if (collectedGems != null) {
            alive.getCollectedGems().addAll(collectedGems);
        }
        if (collectedExtraLives != null) {
            alive.getCollectedExtraLives().addAll(collectedExtraLives);
        }
        return alive;
    }


    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = GameController.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertTrue(publicMethods.length >= 2);
        assertDoesNotThrow(() -> clazz.getMethod("processMove", Direction.class));
        assertDoesNotThrow(() -> clazz.getMethod("processUndo"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = GameController.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    // For UP and LEFT:
    // P.*
    // ...
    // ...
    //
    // For DOWN and RIGHT:
    // ..*
    // ...
    // ..P
    @ParameterizedTest
    @Tag("provided")
    @EnumSource(value = Direction.class)
    @DisplayName("Make Move - Move to Adjacent Border, Unlimited Lives")
    void testMakeMoveToBorderUnlimitedLives(final Direction direction) {
        final Position expectedPos;
        if (direction == Direction.UP || direction == Direction.LEFT) {
            expectedPos = createPosition(0, 0);
        } else {
            expectedPos = createPosition(2, 2);
        }

        gameBoard = GameBoardUtils.createGameBoard(3, 3, (pos) -> {
            if (pos.getRow() == expectedPos.getRow() && pos.getCol() == expectedPos.getCol()) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, null);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives());
        assumeTrue(gameState.getnumLives() == Integer.MAX_VALUE);
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(direction);

        assumeTrue(moveResult instanceof Invalid);

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(0, gameState.getNumMoves());
        assertEquals(Integer.MAX_VALUE, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());
        assertTrue(gameState.getMoveStack().isEmpty());
    }

    // Each parameterized test will test one of four positions for W
    // *W.
    // WPW
    // .W.
    @ParameterizedTest
    @Tag("provided")
    @EnumSource(value = Direction.class)
    @DisplayName("Make Move - Move to Adjacent Wall, Unlimited Lives")
    void testMakeMoveToAdjacentWallUnlimitedLives(final Direction direction) {
        // 获取方向偏移
        int dRow = getRowOffset(direction);
        int dCol = getColOffset(direction);
        final var wallPos = createPosition(1 + dRow, 1 + dCol);

        gameBoard = GameBoardUtils.createGameBoard(3, 3, (pos) -> {
            if (pos.getRow() == 1 && pos.getCol() == 1) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == wallPos.getRow() && pos.getCol() == wallPos.getCol()) {
                return createWall(pos);
            } else if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, null);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives());
        assumeTrue(gameState.getnumLives() == Integer.MAX_VALUE);
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(direction);

        assumeTrue(moveResult instanceof Invalid);

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(0, gameState.getNumMoves());
        assertEquals(Integer.MAX_VALUE, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());
        assertTrue(gameState.getMoveStack().isEmpty());
    }

    // P.W*
    // ....
    @ParameterizedTest
    @Tag("provided")
    @ValueSource(booleans = {true, false})
    @DisplayName("Make Move - Move to Wall")
    void testMakeValidMoveToWall(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createWall(pos);
            } else if (pos.getRow() == 0 && pos.getCol() == 3) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(Direction.RIGHT);

        assumeTrue(moveResult instanceof Alive);

        // Mutation Assertions
        assertEquals(moveResult, gameState.getMoveStack().peek());
        assertEquals(1, gameState.getNumMoves());

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(1, gameState.getNumGems());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
    }

    // P.#.
    // ...*
    @ParameterizedTest
    @Tag("provided")
    @ValueSource(booleans = {true, false})
    @DisplayName("Make Move - Move to Stop Cell")
    void testMakeValidMoveToStopCell(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createStopCell(pos, null);
            } else if (pos.getRow() == 1 && pos.getCol() == 3) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        // Mutation Assertions
        assertEquals(moveResult, gameState.getMoveStack().peek());
        assertEquals(1, gameState.getNumMoves());

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(1, gameState.getNumGems());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
    }

    // P**#
    // ...*
    @ParameterizedTest
    @Tag("provided")
    @ValueSource(booleans = {true, false})
    @DisplayName("Make Move - Move passing Gems")
    void testMakeValidMovePassingGems(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if ((pos.getRow() == 0 && pos.getCol() == 1) || (pos.getRow() == 0 && pos.getCol() == 2)) {
                return createEntityCell(pos, createGem());
            } else if (pos.getRow() == 0 && pos.getCol() == 3) {
                return createStopCell(pos, null);
            } else if (pos.getRow() == 1 && pos.getCol() == 3) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 3);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(Direction.RIGHT);

        assumeTrue(moveResult instanceof Alive);

        // Mutation Assertions
        assertEquals(moveResult, gameState.getMoveStack().peek());
        assertEquals(1, gameState.getNumMoves());
        assertEquals(1, gameState.getNumGems());

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
    }

    // PLL#
    // ...*
    @ParameterizedTest
    @Tag("provided")
    @ValueSource(booleans = {true, false})
    @DisplayName("Make Move - Move passing ExtraLife")
    void testMakeValidMovePassingExtraLives(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if ((pos.getRow() == 0 && pos.getCol() == 1) || (pos.getRow() == 0 && pos.getCol() == 2)) {
                return createEntityCell(pos, createExtraLife());
            } else if (pos.getRow() == 0 && pos.getCol() == 3) {
                return createStopCell(pos, null);
            } else if (pos.getRow() == 1 && pos.getCol() == 3) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(Direction.RIGHT);

        assumeTrue(moveResult instanceof Alive);

        // Mutation Assertions
        assertEquals(moveResult, gameState.getMoveStack().peek());
        assertEquals(1, gameState.getNumMoves());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 3, gameState.getnumLives());

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(1, gameState.getNumGems());
    }

    // P.M
    // ..*
    @ParameterizedTest
    @Tag("provided")
    @ValueSource(booleans = {true, false})
    @DisplayName("Make Move - Move hitting Mine")
    void testMakeValidMoveToMine(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createEntityCell(pos, createMine());
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 3);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 3));
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(Direction.RIGHT);

        assumeTrue(moveResult instanceof Dead);

        // Mutation Assertions
        assertEquals(1, gameState.getNumMoves());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 2, gameState.getnumLives());
        assertEquals(1, gameState.getNumDeaths());

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertTrue(gameState.getMoveStack().isEmpty());
        assertEquals(1, gameState.getNumGems());
    }

    // P.M
    // ..*
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move hitting Mine and Losing")
    void testMakeValidMoveToMineAndLosing() {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createEntityCell(pos, createMine());
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeFalse(gameState.hasUnlimitedLives());
        assumeTrue(gameState.getnumLives() == 1);
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(Direction.RIGHT);

        assumeTrue(moveResult instanceof Dead);

        // Mutation Assertions
        assertEquals(1, gameState.getNumMoves());
        assertEquals(0, gameState.getnumLives());
        assertEquals(1, gameState.getNumDeaths());
        assertTrue(gameState.hasLost());

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertTrue(gameState.getMoveStack().isEmpty());
        assertEquals(1, gameState.getNumGems());
    }

    @ParameterizedTest
    @Tag("actual")
    @EnumSource(value = Direction.class)
    @DisplayName("Make Move - Move to Adjacent Border, Limited Lives")
    void testMakeMoveToBorderLimitedLives(final Direction direction) {
        final Position expectedPos;
        if (direction == Direction.UP || direction == Direction.LEFT) {
            expectedPos = createPosition(0, 0);
        } else {
            expectedPos = createPosition(2, 2);
        }

        gameBoard = GameBoardUtils.createGameBoard(3, 3, (pos) -> {
            if (pos.getRow() == expectedPos.getRow() && pos.getCol() == expectedPos.getCol()) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeFalse(gameState.hasUnlimitedLives());
        assumeTrue(gameState.getnumLives() == 1);
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(direction);

        assumeTrue(moveResult instanceof Invalid);

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(0, gameState.getNumMoves());
        assertEquals(1, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());
        assertTrue(gameState.getMoveStack().isEmpty());
    }

    @ParameterizedTest
    @Tag("actual")
    @EnumSource(value = Direction.class)
    @DisplayName("Make Move - Move to Adjacent Wall, Limited Lives")
    void testMakeMoveToAdjacentWallLimitedLives(final Direction direction) {
        int dRow = getRowOffset(direction);
        int dCol = getColOffset(direction);
        final var wallPos = createPosition(1 + dRow, 1 + dCol);

        gameBoard = GameBoardUtils.createGameBoard(3, 3, (pos) -> {
            if (pos.getRow() == 1 && pos.getCol() == 1) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == wallPos.getRow() && pos.getCol() == wallPos.getCol()) {
                return createWall(pos);
            } else if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeFalse(gameState.hasUnlimitedLives());
        assumeTrue(gameState.getnumLives() == 1);
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(direction);

        assumeTrue(moveResult instanceof Invalid);

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(0, gameState.getNumMoves());
        assertEquals(1, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());
        assertTrue(gameState.getMoveStack().isEmpty());
    }

    @ParameterizedTest
    @Tag("actual")
    @ValueSource(booleans = {true, false})
    @DisplayName("Make Move - Move passing different entities")
    void testMakeValidMovePassingEntities(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 7, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createEntityCell(pos, createExtraLife());
            } else if (pos.getRow() == 0 && pos.getCol() == 4) {
                return createEntityCell(pos, createGem());
            } else if (pos.getRow() == 0 && pos.getCol() == 6) {
                return createStopCell(pos, null);
            } else if (pos.getRow() == 1 && pos.getCol() == 6) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 2);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(Direction.RIGHT);

        assumeTrue(moveResult instanceof Alive);

        assertEquals(moveResult, gameState.getMoveStack().peek());
        assertEquals(1, gameState.getNumMoves());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 2, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
    }

    @ParameterizedTest
    @Tag("actual")
    @ValueSource(booleans = {true, false})
    @DisplayName("Make Move - Move hitting Mine while picking up entities")
    void testMakeValidMoveToMineCrossingOtherPickUps(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                return createEntityCell(pos, createExtraLife());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else if (pos.getRow() == 0 && pos.getCol() == 3) {
                return createEntityCell(pos, createMine());
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createStopCell(pos, null);
            } else if (pos.getRow() == 1 && pos.getCol() == 3) {
                return createEntityCell(pos, createMine());
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 3);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 3));
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var moveResult = controller.processMove(Direction.RIGHT);

        assumeTrue(moveResult instanceof Dead);

        assertEquals(1, gameState.getNumMoves());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 2, gameState.getnumLives());
        assertEquals(1, gameState.getNumDeaths());

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertTrue(gameState.getMoveStack().isEmpty());
        assertEquals(1, gameState.getNumGems());
    }

    @ParameterizedTest
    @Tag("actual")
    @ValueSource(booleans = {true, false})
    @DisplayName("Undo Move - No moves")
    void testUndoFromEmptyStack(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                return createEntityCell(pos, createGem());
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createEntityCell(pos, createMine());
            } else if (pos.getRow() == 1 && pos.getCol() == 1) {
                return createStopCell(pos, null);
            } else {
                return createEntityCell(pos, null);
            }
        });
        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 0);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 1);
        assumeTrue(gameState.getMoveStack().isEmpty());

        final var result = assertDoesNotThrow(() -> controller.processUndo());
        assertFalse(result);

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumMoves());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(1, gameState.getNumGems());
        assertTrue(gameState.getMoveStack().isEmpty());
    }

    @ParameterizedTest
    @Tag("actual")
    @ValueSource(booleans = {true, false})
    @DisplayName("Undo Move - Restores Gems")
    void testUndoMoveRestoresGems(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });

        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        gameState.incrementNumMoves();
        final var moveToUndo = createAliveMove(
                createPosition(0, 2),
                createPosition(0, 0),
                Collections.singletonList(createPosition(0, 1)),
                Collections.emptyList()
        );
        gameState.getMoveStack().push(moveToUndo);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 1);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 1);
        assumeFalse(gameState.getMoveStack().isEmpty());

        assertTrue(controller.processUndo());

        assertEquals(2, gameState.getNumGems());
        assertTrue(gameState.getMoveStack().isEmpty());

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
        assertEquals(1, gameState.getNumMoves());
        assertEquals(0, gameState.getNumDeaths());
    }

    @ParameterizedTest
    @Tag("actual")
    @ValueSource(booleans = {true, false})
    @DisplayName("Undo Move - Restores ExtraLife")
    void testUndoMoveRestoresExtraLife(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });

        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 2);
        controller = createController(gameState);

        gameState.incrementNumMoves();
        final var moveToUndo = createAliveMove(
                createPosition(0, 2),
                createPosition(0, 0),
                Collections.emptyList(),
                Collections.singletonList(createPosition(0, 1))
        );
        gameState.getMoveStack().push(moveToUndo);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 1);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 2));
        assumeTrue(gameState.getNumGems() == 1);
        assumeFalse(gameState.getMoveStack().isEmpty());

        assertTrue(controller.processUndo());

        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
        assertTrue(gameState.getMoveStack().isEmpty());

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(1, gameState.getNumGems());
        assertEquals(1, gameState.getNumMoves());
        assertEquals(0, gameState.getNumDeaths());
    }

    @ParameterizedTest
    @Tag("actual")
    @ValueSource(booleans = {true, false})
    @DisplayName("Undo Move - Restores all entity pickups")
    void testUndoMoveRestoresEntities(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 3) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 1 && pos.getCol() == 3) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });

        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 2);
        controller = createController(gameState);

        gameState.incrementNumMoves();
        final var moveToUndo = createAliveMove(
                createPosition(0, 3),
                createPosition(0, 0),
                Collections.singletonList(createPosition(0, 1)),
                Collections.singletonList(createPosition(0, 2))
        );
        gameState.getMoveStack().push(moveToUndo);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 1);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 2));
        assumeTrue(gameState.getNumGems() == 1);
        assumeFalse(gameState.getMoveStack().isEmpty());

        assertTrue(controller.processUndo());

        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
        assertEquals(2, gameState.getNumGems());
        assertTrue(gameState.getMoveStack().isEmpty());

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(1, gameState.getNumMoves());
        assertEquals(0, gameState.getNumDeaths());
    }

    @ParameterizedTest
    @Tag("actual")
    @ValueSource(booleans = {true, false})
    @DisplayName("Undo Move - Undoes only one move")
    void testUndoMoveUndoesOne(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(3, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });

        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        gameState.incrementNumMoves();
        final var move1 = createAliveMove(createPosition(2, 0), createPosition(0, 0), Collections.emptyList(), Collections.emptyList());
        gameState.getMoveStack().push(move1);

        gameState.incrementNumMoves();
        final var move2 = createAliveMove(createPosition(2, 2), createPosition(2, 0), Collections.emptyList(), Collections.emptyList());
        gameState.getMoveStack().push(move2);

        gameState.incrementNumMoves();
        final var move3 = createAliveMove(createPosition(0, 2), createPosition(2, 2), Collections.emptyList(), Collections.emptyList());
        gameState.getMoveStack().push(move3);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 3);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 1);
        assumeFalse(gameState.getMoveStack().isEmpty());
        assumeTrue(gameState.getMoveStack().peek().equals(move3));

        assertTrue(controller.processUndo());

        assertEquals(move2, gameState.getMoveStack().peek());

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());
        assertEquals(3, gameState.getNumMoves());
        assertEquals(0, gameState.getNumDeaths());
    }

    // Undoes the following move:
    // P.# -> ..P
    // ..*    ..*
    @ParameterizedTest
    @Tag("provided")
    @ValueSource(booleans = {true, false})
    @DisplayName("Undo Move - Single Move")
    void testUndoMoveTrivial(final boolean hasUnlimitedLives) {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });

        gameState = createGameState(gameBoard, hasUnlimitedLives ? null : 1);
        controller = createController(gameState);

        gameState.incrementNumMoves();
        final var moveToUndo = createAliveMove(
                createPosition(0, 2),
                createPosition(0, 0),
                Collections.emptyList(),
                Collections.emptyList()
        );
        gameState.getMoveStack().push(moveToUndo);

        assumeFalse(gameState.hasWon());
        assumeFalse(gameState.hasLost());
        assumeTrue(gameState.getNumDeaths() == 0);
        assumeTrue(gameState.getNumMoves() == 1);
        assumeTrue(gameState.hasUnlimitedLives() == hasUnlimitedLives);
        assumeTrue(gameState.getnumLives() == (hasUnlimitedLives ? Integer.MAX_VALUE : 1));
        assumeTrue(gameState.getNumGems() == 1);
        assumeFalse(gameState.getMoveStack().isEmpty());

        assertTrue(controller.processUndo());

        // Mutation Assertions
        assertTrue(gameState.getMoveStack().isEmpty());

        // Non-Mutation Assertions
        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(hasUnlimitedLives ? Integer.MAX_VALUE : 1, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());
        assertEquals(1, gameState.getNumMoves());
        assertEquals(0, gameState.getNumDeaths());
    }

    @AfterEach
    void tearDown() {
        controller = null;
        gameState = null;
        gameBoard = null;
    }

    // 辅助方法：获取方向的行偏移
    private static int getRowOffset(Direction direction) {
        if (direction == Direction.UP) return -1;
        if (direction == Direction.DOWN) return 1;
        return 0;
    }

    // 辅助方法：获取方向的列偏移
    private static int getColOffset(Direction direction) {
        if (direction == Direction.LEFT) return -1;
        if (direction == Direction.RIGHT) return 1;
        return 0;
    }
}
