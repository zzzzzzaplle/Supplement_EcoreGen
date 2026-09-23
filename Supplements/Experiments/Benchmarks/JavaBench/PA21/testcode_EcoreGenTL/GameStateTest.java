package edu.pa21;

import edu.pa21.util.GameBoardHelper;
import edu.pa21.util.GameBoardUtils;
import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class GameStateTest {

    private GameBoard gameBoard = null;
    private GameState gameState = null;

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

    // 辅助函数：创建MoveStack对象
    private static MoveStack createMoveStack() {
        return Pa21Factory.eINSTANCE.createMoveStack();
    }

    // 辅助函数：创建GameState（无限生命）
    private static GameState createGameState(GameBoard board) {
        GameState state = Pa21Factory.eINSTANCE.createGameState();
        state.setGameBoard(board);
        state.setMoveStack(createMoveStack());
        state.setInitialNumOfGems(GameBoardHelper.getNumGems(board));
        state.setNumLives(-1); // 无限生命
        return state;
    }

    // 辅助函数：创建GameState（有限生命）
    private static GameState createGameState(GameBoard board, int numLives) {
        GameState state = Pa21Factory.eINSTANCE.createGameState();
        state.setGameBoard(board);
        state.setMoveStack(createMoveStack());
        state.setInitialNumOfGems(GameBoardHelper.getNumGems(board));
        state.setNumLives(numLives);
        return state;
    }


    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = GameState.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertTrue(publicMethods.length >= 17);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("hasWon"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("hasLost"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("increaseNumLives", int.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("decreaseNumLives", int.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("decrementNumLives"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("incrementNumMoves"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("incrementNumDeaths"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getNumDeaths"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getNumMoves"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("hasUnlimitedLives"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getNumLives"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getNumGems"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getScore"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getGameBoardController"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getGameBoardView"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getGameBoard"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getMoveStack"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = GameState.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Unlimited Lives using One-Arg constructor")
    void testCreationUnlimitedLives() {
        gameBoard = GameBoardUtils.createGameBoard(2, 2, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard);

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(0, gameState.getNumMoves());
        assertTrue(gameState.hasUnlimitedLives());
        assertEquals(Integer.MAX_VALUE, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());
        assertEquals(4, gameState.getScore());
        assertSame(gameBoard, gameState.getGameBoard());
    }

    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Limited Lives")
    void testCreationLimitedLives() {
        gameBoard = GameBoardUtils.createGameBoard(2, 2, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard, 3);

        assertFalse(gameState.hasWon());
        assertFalse(gameState.hasLost());
        assertEquals(0, gameState.getNumDeaths());
        assertEquals(0, gameState.getNumMoves());
        assertFalse(gameState.hasUnlimitedLives());
        assertEquals(3, gameState.getnumLives());
        assertEquals(1, gameState.getNumGems());
        assertEquals(4, gameState.getScore());
        assertSame(gameBoard, gameState.getGameBoard());
    }

    @Test
    @Tag("provided")
    @DisplayName("Test Win Condition")
    void testWinsWhenNoGems() {
        gameBoard = GameBoardUtils.createGameBoard(2, 2, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard);

        gameBoard.getEntityCell2(0, 1).setEntity(null);

        assertEquals(0, gameState.getNumGems());
        assertTrue(gameState.hasWon());
    }

    @Test
    @Tag("provided")
    @DisplayName("Test Lose Condition")
    void testLosesWhenNoLives() {
        gameBoard = GameBoardUtils.createGameBoard(2, 2, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard, 0);

        assertEquals(0, gameState.getnumLives());
        assertTrue(gameState.hasLost());
    }

    @Test
    @Tag("provided")
    @DisplayName("Lives Increase - Limited Lives")
    void testIncreaseNumLivesWhenNotUnlimitedLives() {
        gameBoard = GameBoardUtils.createGameBoard(2, 2, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard, 3);

        assumeFalse(gameState.hasUnlimitedLives());

        final var newNumLives = gameState.increaseNumLives(1);
        assertEquals(4, newNumLives);
        assertEquals(4, gameState.getnumLives());
    }

    @Test
    @Tag("provided")
    @DisplayName("Lives Decrease - Limited Lives")
    void testDecreaseNumLivesWhenNotUnlimitedLives() {
        gameBoard = GameBoardUtils.createGameBoard(2, 2, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard, 3);

        assumeFalse(gameState.hasUnlimitedLives());

        final var newNumLives = gameState.decreaseNumLives(1);
        assertEquals(2, newNumLives);
        assertEquals(2, gameState.getnumLives());
    }

    @Test
    @Tag("provided")
    @DisplayName("Get Score - Initial Always Zero")
    void testInitialScoreAlwaysZero() {
        gameBoard = GameBoardUtils.createGameBoard(2, 2, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard);

        assertEquals(4, gameState.getScore());

        gameBoard = GameBoardUtils.createGameBoard(10, 10, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard);

        assertEquals(100, gameState.getScore());
    }

    @Test
    @Tag("provided")
    @DisplayName("Get Score - Score Increases When Gem Picked Up")
    void testScoreScalesWhenGemPickedUp() {
        gameBoard = GameBoardUtils.createGameBoard(2, 2, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else if (pos.getRow() == 1 && pos.getCol() == 0) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard);

        assumeTrue(gameState.getScore() == 4);

        gameBoard.getEntityCell1(createPosition(1, 0)).setEntity(null);

        assertEquals(14, gameState.getScore());

        gameBoard = GameBoardUtils.createGameBoard(10, 10, (pos) -> {
            final Entity entity;
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                entity = createPlayer();
            } else if (pos.getRow() == 0 && pos.getCol() == 1) {
                entity = createGem();
            } else if (pos.getRow() == 0 && pos.getCol() == 2) {
                entity = createGem();
            } else if (pos.getRow() == 0 && pos.getCol() == 3) {
                entity = createGem();
            } else if (pos.getRow() == 0 && pos.getCol() == 4) {
                entity = createGem();
            } else {
                entity = null;
            }

            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            if (entity != null) {
                cell.setEntity(entity);
            }
            return cell;
        });

        gameState = createGameState(gameBoard);

        assumeTrue(gameState.getScore() == 100);

        gameBoard.getEntityCell1(createPosition(0, 2)).setEntity(null);

        assertEquals(110, gameState.getScore());
    }

    @AfterEach
    void tearDown() {
        gameState = null;
        gameBoard = null;
    }
}
