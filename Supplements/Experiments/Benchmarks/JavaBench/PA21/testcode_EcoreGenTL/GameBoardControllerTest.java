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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class GameBoardControllerTest {
    private GameBoard gameBoard = null;
    private GameBoardController controller = null;

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

    // 辅助函数：创建ExtraLife对象@
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

    // 辅助函数：创建GameBoardController
    private static GameBoardController createController(GameBoard board) {
        GameBoardController controller = Pa21Factory.eINSTANCE.createGameBoardController();
        controller.setGameBoard(board);
        return controller;
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
        final var clazz = GameBoardController.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // makeMove, undoMove, getGameBoard, setGameBoard
        assertTrue(publicMethods.length >= 2);

        assertDoesNotThrow(() -> clazz.getMethod("makeMove", Direction.class));
        assertDoesNotThrow(() -> clazz.getMethod("undoMove", MoveResult.class));
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
    @DisplayName("Make Move - Move to Adjacent Border")
    void testMakeMoveToBorder(final Direction direction) {
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
        controller = createController(gameBoard);

        assumeTrue(gameBoard.getEntityCell1(expectedPos).getEntity() instanceof Player);
        assumeTrue(gameBoard.getPlayer().equals(gameBoard.getEntityCell1(expectedPos).getEntity()));
        assumeTrue(GameBoardHelper.getNumGems(gameBoard) == 1);
        assumeTrue(gameBoard.getEntityCell2(0, 2).getEntity() instanceof Gem);

        final var moveResult = controller.makeMove(direction);

        assertTrue(moveResult instanceof Invalid);

        final var invalidResult = (Invalid) moveResult;

        // Result Assertions
        assertEquals(expectedPos.getRow(), invalidResult.getNewPosition().getRow());
        assertEquals(expectedPos.getCol(), invalidResult.getNewPosition().getCol());

        // Non-Mutation Assertions
        assertEquals(gameBoard.getPlayer(), gameBoard.getEntityCell1(expectedPos).getEntity());
        assertEquals(1, GameBoardHelper.getNumGems(gameBoard));
        assertTrue(gameBoard.getEntityCell2(0, 2).getEntity() instanceof Gem);
    }

    // P.W*
    // ....
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move to Wall")
    void testMakeValidMoveToWall() {
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
        controller = createController(gameBoard);

        assumeTrue(gameBoard.getEntityCell2(0, 0).getEntity() instanceof Player);
        assumeTrue(gameBoard.getPlayer().equals(gameBoard.getEntityCell2(0, 0).getEntity()));
        assumeTrue(GameBoardHelper.getNumGems(gameBoard) == 1);
        assumeTrue(gameBoard.getEntityCell2(0, 3).getEntity() instanceof Gem);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        final var aliveResult = (Alive) moveResult;

        // Result Assertions
        assertEquals(0, aliveResult.getNewPosition().getRow());
        assertEquals(1, aliveResult.getNewPosition().getCol());
        assertEquals(0, aliveResult.getOrigPosition().getRow());
        assertEquals(0, aliveResult.getOrigPosition().getCol());
        assertTrue(aliveResult.getCollectedGems().isEmpty());
        assertTrue(aliveResult.getCollectedExtraLives().isEmpty());

        // GameBoard Mutation Assertions
        assertNull(gameBoard.getEntityCell2(0, 0).getEntity());
        assertEquals(gameBoard.getPlayer(), gameBoard.getEntityCell2(0, 1).getEntity());

        // Non-Mutation Assertions
        assertEquals(1, GameBoardHelper.getNumGems(gameBoard));
        assertTrue(gameBoard.getEntityCell2(0, 3).getEntity() instanceof Gem);
    }

    // P.#.
    // ...*
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move to StopCell")
    void testMakeValidMoveToStopCell() {
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
        controller = createController(gameBoard);

        assumeTrue(gameBoard.getEntityCell2(0, 0).getEntity() instanceof Player);
        assumeTrue(gameBoard.getPlayer().equals(gameBoard.getEntityCell2(0, 0).getEntity()));
        assumeTrue(GameBoardHelper.getNumGems(gameBoard) == 1);
        assumeTrue(gameBoard.getEntityCell2(1, 3).getEntity() instanceof Gem);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        final var aliveResult = (Alive) moveResult;

        // Result Assertions
        assertEquals(0, aliveResult.getNewPosition().getRow());
        assertEquals(2, aliveResult.getNewPosition().getCol());
        assertEquals(0, aliveResult.getOrigPosition().getRow());
        assertEquals(0, aliveResult.getOrigPosition().getCol());
        assertTrue(aliveResult.getCollectedGems().isEmpty());
        assertTrue(aliveResult.getCollectedExtraLives().isEmpty());

        // GameBoard Mutation Assertions
        assertNull(gameBoard.getEntityCell2(0, 0).getEntity());
        assertEquals(gameBoard.getPlayer(), gameBoard.getEntityCell2(0, 2).getEntity());

        // Non-Mutation Assertions
        assertEquals(1, GameBoardHelper.getNumGems(gameBoard));
        assertTrue(gameBoard.getEntityCell2(1, 3).getEntity() instanceof Gem);
    }

    // P**#
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move passes Gem")
    void testMakeValidMovePassingGem() {
        gameBoard = GameBoardUtils.createGameBoard(1, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && (pos.getCol() == 1 || pos.getCol() == 2)) {
                return createEntityCell(pos, createGem());
            } else if (pos.getRow() == 0 && pos.getCol() == 3) {
                return createStopCell(pos, null);
            } else {
                return createEntityCell(pos, null);
            }
        });
        controller = createController(gameBoard);

        assumeTrue(gameBoard.getEntityCell2(0, 0).getEntity() instanceof Player);
        assumeTrue(gameBoard.getPlayer().equals(gameBoard.getEntityCell2(0, 0).getEntity()));
        assumeTrue(GameBoardHelper.getNumGems(gameBoard) == 2);
        assumeTrue(gameBoard.getEntityCell2(0, 1).getEntity() instanceof Gem);
        assumeTrue(gameBoard.getEntityCell2(0, 2).getEntity() instanceof Gem);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        final var aliveResult = (Alive) moveResult;

        // Result Assertions
        assertEquals(0, aliveResult.getNewPosition().getRow());
        assertEquals(3, aliveResult.getNewPosition().getCol());
        assertEquals(0, aliveResult.getOrigPosition().getRow());
        assertEquals(0, aliveResult.getOrigPosition().getCol());
        assertEquals(2, aliveResult.getCollectedGems().size());
        assertTrue(aliveResult.getCollectedGems().stream().anyMatch(p -> p.getRow() == 0 && p.getCol() == 1));
        assertTrue(aliveResult.getCollectedGems().stream().anyMatch(p -> p.getRow() == 0 && p.getCol() == 2));
        assertTrue(aliveResult.getCollectedExtraLives().isEmpty());

        // GameBoard Mutation Assertions
        assertNull(gameBoard.getEntityCell2(0, 0).getEntity());
        assertNull(gameBoard.getEntityCell2(0, 1).getEntity());
        assertNull(gameBoard.getEntityCell2(0, 2).getEntity());
        assertEquals(gameBoard.getPlayer(), gameBoard.getEntityCell2(0, 3).getEntity());

        // Non-Mutation Assertions
        assertEquals(0, GameBoardHelper.getNumGems(gameBoard));
    }

    // PLL#
    // ...*
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move passes ExtraLives")
    void testMakeValidMovePassingExtraLives() {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createEntityCell(pos, createPlayer());
            } else if (pos.getRow() == 0 && (pos.getCol() == 1 || pos.getCol() == 2)) {
                return createEntityCell(pos, createExtraLife());
            } else if (pos.getRow() == 0 && pos.getCol() == 3) {
                return createStopCell(pos, null);
            } else if (pos.getRow() == 1 && pos.getCol() == 3) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        controller = createController(gameBoard);

        assumeTrue(gameBoard.getEntityCell2(0, 0).getEntity() instanceof Player);
        assumeTrue(gameBoard.getPlayer().equals(gameBoard.getEntityCell2(0, 0).getEntity()));
        assumeTrue(GameBoardHelper.getNumGems(gameBoard) == 1);
        assumeTrue(gameBoard.getEntityCell2(1, 3).getEntity() instanceof Gem);
        assumeTrue(gameBoard.getEntityCell2(0, 1).getEntity() instanceof ExtraLife);
        assumeTrue(gameBoard.getEntityCell2(0, 2).getEntity() instanceof ExtraLife);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        final var aliveResult = (Alive) moveResult;

        // Result Assertions
        assertEquals(0, aliveResult.getNewPosition().getRow());
        assertEquals(3, aliveResult.getNewPosition().getCol());
        assertEquals(0, aliveResult.getOrigPosition().getRow());
        assertEquals(0, aliveResult.getOrigPosition().getCol());
        assertTrue(aliveResult.getCollectedGems().isEmpty());
        assertEquals(2, aliveResult.getCollectedExtraLives().size());
        assertTrue(aliveResult.getCollectedExtraLives().stream().anyMatch(p -> p.getRow() == 0 && p.getCol() == 1));
        assertTrue(aliveResult.getCollectedExtraLives().stream().anyMatch(p -> p.getRow() == 0 && p.getCol() == 2));

        // GameBoard Mutation Assertions
        assertNull(gameBoard.getEntityCell2(0, 0).getEntity());
        assertNull(gameBoard.getEntityCell2(0, 1).getEntity());
        assertNull(gameBoard.getEntityCell2(0, 2).getEntity());
        assertEquals(gameBoard.getPlayer(), gameBoard.getEntityCell2(0, 3).getEntity());

        // Non-Mutation Assertions
        assertEquals(1, GameBoardHelper.getNumGems(gameBoard));
        assertTrue(gameBoard.getEntityCell2(1, 3).getEntity() instanceof Gem);
    }

    // P.M
    // ..*
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move hits Mine")
    void testMakeValidMoveToMine() {
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
        controller = createController(gameBoard);

        assumeTrue(gameBoard.getEntityCell2(0, 0).getEntity() instanceof Player);
        assumeTrue(gameBoard.getPlayer().equals(gameBoard.getEntityCell2(0, 0).getEntity()));
        assumeTrue(GameBoardHelper.getNumGems(gameBoard) == 1);
        assumeTrue(gameBoard.getEntityCell2(1, 2).getEntity() instanceof Gem);
        assumeTrue(gameBoard.getEntityCell2(0, 2).getEntity() instanceof Mine);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Dead);

        final var deadResult = (Dead) moveResult;

        // Result Assertions
        assertEquals(0, deadResult.getNewPosition().getRow());
        assertEquals(0, deadResult.getNewPosition().getCol());
        assertEquals(0, deadResult.getOrigPosition().getRow());
        assertEquals(0, deadResult.getOrigPosition().getCol());

        // Non-Mutation Assertions
        assertEquals(gameBoard.getPlayer(), gameBoard.getEntityCell2(0, 0).getEntity());
        assertTrue(gameBoard.getEntityCell2(0, 2).getEntity() instanceof Mine);
        assertEquals(1, GameBoardHelper.getNumGems(gameBoard));
        assertTrue(gameBoard.getEntityCell2(1, 2).getEntity() instanceof Gem);
    }

    // Undoes the following move:
    // P.# -> ..P
    // ..*    ..*
    @Test
    @Tag("provided")
    @DisplayName("Undo Move - Simple")
    void testUndoMoveTrivial() {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 2) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        controller = createController(gameBoard);

        assumeTrue(gameBoard.getEntityCell2(0, 2).getEntity() instanceof Player);
        assumeTrue(gameBoard.getPlayer().equals(gameBoard.getEntityCell2(0, 2).getEntity()));
        assumeTrue(GameBoardHelper.getNumGems(gameBoard) == 1);
        assumeTrue(gameBoard.getEntityCell2(1, 2).getEntity() instanceof Gem);

        final var moveToUndo = createAliveMove(
                createPosition(0, 2),
                createPosition(0, 0),
                Collections.emptyList(),
                Collections.emptyList()
        );

        controller.undoMove(moveToUndo);

        // Mutation Assertions
        assertNull(gameBoard.getEntityCell2(0, 2).getEntity());
        assertEquals(gameBoard.getPlayer(), gameBoard.getEntityCell2(0, 0).getEntity());

        // Non-Mutation Assertions
        assertTrue(gameBoard.getEntityCell2(0, 2) instanceof StopCell);
        assertTrue(gameBoard.getEntityCell2(1, 2).getEntity() instanceof Gem);
    }
    @Test
    @Tag("actual")
    @DisplayName("Undo Move - Restores Entities")
    void testUndoMoveWithPickups() {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 3) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 1 && pos.getCol() == 3) {
                return createEntityCell(pos, createGem());
            } else {
                return createEntityCell(pos, null);
            }
        });
        controller = createController(gameBoard);

        assumeTrue(gameBoard.getEntityCell2(0, 3).getEntity() instanceof Player);
        assumeTrue(gameBoard.getPlayer().equals(gameBoard.getEntityCell2(0, 3).getEntity()));
        assumeTrue(GameBoardHelper.getNumGems(gameBoard) == 1);
        assumeTrue(gameBoard.getEntityCell2(1, 3).getEntity() instanceof Gem);

        final var moveToUndo = createAliveMove(
                createPosition(0, 3),
                createPosition(0, 0),
                java.util.Collections.singletonList(createPosition(0, 1)),
                java.util.Collections.singletonList(createPosition(0, 2))
        );

        controller.undoMove(moveToUndo);

        // Mutation Assertions
        assertNull(gameBoard.getEntityCell2(0, 3).getEntity());
        assertTrue(gameBoard.getEntityCell2(0, 2).getEntity() instanceof ExtraLife);
        assertTrue(gameBoard.getEntityCell2(0, 1).getEntity() instanceof Gem);
        assertEquals(gameBoard.getPlayer(), gameBoard.getEntityCell2(0, 0).getEntity());

        // Non-Mutation Assertions
        assertTrue(gameBoard.getEntityCell2(0, 3) instanceof StopCell);
        assertTrue(gameBoard.getEntityCell2(1, 3).getEntity() instanceof Gem);
    }
    @AfterEach
    void tearDown() {
        controller = null;
        gameBoard = null;
    }
}
