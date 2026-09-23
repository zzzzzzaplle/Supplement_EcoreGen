package edu.pa21;

import edu.pa21.util.GameBoardHelper;
import edu.pa21.util.GameBoardUtils;
import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateSerializerTest {

    private BufferedReader reader = null;
    private BufferedWriter writer = null;

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
        final var clazz = GameStateSerializer.class;
        final var instanceMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertEquals(0, instanceMethods.length);

        final var staticMethods = ReflectionUtils.getPublicStaticMethods(clazz);
        assertTrue(staticMethods.length >= 2);

        assertDoesNotThrow(() -> clazz.getMethod("loadFrom", Path.class));
        assertDoesNotThrow(() -> clazz.getMethod("writeTo", GameState.class, BufferedWriter.class));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = GameStateSerializer.class;

        assertEquals(0, ReflectionUtils.getPublicInstanceFields(clazz).length);
        assertEquals(0, ReflectionUtils.getPublicStaticFields(clazz).length);
    }

    // P..
    // XWL
    // .*#
    @Test
    @Tag("provided")
    @DisplayName("Serialization Test - Unlimited Lives")
    void testSerializeUnlimitedLives() {
        final var cells = GameBoardUtils.createEmptyCellArray(3, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 1 && pos.getCol() == 0) {
                return createEntityCell(pos, createMine());
            } else if (pos.getRow() == 1 && pos.getCol() == 1) {
                return createWall(pos);
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createEntityCell(pos, createExtraLife());
            } else if (pos.getRow() == 2 && pos.getCol() == 1) {
                return createEntityCell(pos, createGem());
            } else if (pos.getRow() == 2 && pos.getCol() == 2) {
                return createStopCell(pos, null);
            } else {
                return createEntityCell(pos, null);
            }
        });

        final var gameBoard = GameBoardHelper.createGameBoard(3, 3, cells);
        final var gameState = createGameState(gameBoard);

        final var strWriter = new StringWriter();
        try (final var writer = new BufferedWriter(strWriter)) {
            GameStateSerializer.writeTo(gameState, writer);
        } catch (final IOException e) {
            fail(e);
        }

        final var expected = String.join(System.lineSeparator(), "3", "3", "", "P..", "MWL", ".GS")
                + System.lineSeparator();

        assertEquals(expected, strWriter.toString());
    }

    @Test
    @Tag("provided")
    @DisplayName("Serialization Test - Limited Lives")
    void testSerializeLimitedLives() {
        final var cells = GameBoardUtils.createEmptyCellArray(3, 3, (pos) -> {
            if (pos.getRow() == 0 && pos.getCol() == 0) {
                return createStopCell(pos, createPlayer());
            } else if (pos.getRow() == 1 && pos.getCol() == 0) {
                return createEntityCell(pos, createMine());
            } else if (pos.getRow() == 1 && pos.getCol() == 1) {
                return createWall(pos);
            } else if (pos.getRow() == 1 && pos.getCol() == 2) {
                return createEntityCell(pos, createExtraLife());
            } else if (pos.getRow() == 2 && pos.getCol() == 1) {
                return createEntityCell(pos, createGem());
            } else if (pos.getRow() == 2 && pos.getCol() == 2) {
                return createStopCell(pos, null);
            } else {
                return createEntityCell(pos, null);
            }
        });

        final var gameBoard = GameBoardHelper.createGameBoard(3, 3, cells);
        final var gameState = createGameState(gameBoard, 10);

        final var strWriter = new StringWriter();
        try (final var writer = new BufferedWriter(strWriter)) {
            GameStateSerializer.writeTo(gameState, writer);
        } catch (final IOException e) {
            fail(e);
        }

        final var expected = String.join(System.lineSeparator(), "3", "3", "10", "P..", "MWL", ".GS")
                + System.lineSeparator();

        assertEquals(expected, strWriter.toString());
    }

    @Test
    @Tag("provided")
    @DisplayName("Deserialization Test - Unlimited Lives")
    void testDeserializeUnlimitedLives() {
        final var source = String.join(System.lineSeparator(), "3", "3", "", "P..", "MWL", ".GS");

        final GameState gameState;
        try (final var reader = new BufferedReader(new StringReader(source))) {
            gameState = GameStateSerializer.loadFrom(reader);
        } catch (IOException e) {
            fail(e);
            throw new AssertionError();
        }

        assertTrue(gameState.hasUnlimitedLives());

        final var gameBoard = gameState.getGameBoard();
        assertEquals(3, gameBoard.getNumRows());
        assertEquals(3, gameBoard.getNumCols());

        assertTrue(gameBoard.getCell1(0, 0) instanceof StopCell);
        assertTrue(((StopCell) gameBoard.getCell1(0, 0)).getEntity() instanceof Player);

        assertTrue(gameBoard.getCell1(0, 1) instanceof EntityCell);
        assertNull(((EntityCell) gameBoard.getCell1(0, 1)).getEntity());

        assertTrue(gameBoard.getCell1(0, 2) instanceof EntityCell);
        assertNull(((EntityCell) gameBoard.getCell1(0, 2)).getEntity());

        assertTrue(gameBoard.getCell1(1, 0) instanceof EntityCell);
        assertTrue(((EntityCell) gameBoard.getCell1(1, 0)).getEntity() instanceof Mine);

        assertTrue(gameBoard.getCell1(1, 1) instanceof Wall);

        assertTrue(gameBoard.getCell1(1, 2) instanceof EntityCell);
        assertTrue(((EntityCell) gameBoard.getCell1(1, 2)).getEntity() instanceof ExtraLife);

        assertTrue(gameBoard.getCell1(2, 0) instanceof EntityCell);
        assertNull(((EntityCell) gameBoard.getCell1(2, 0)).getEntity());

        assertTrue(gameBoard.getCell1(2, 1) instanceof EntityCell);
        assertTrue(((EntityCell) gameBoard.getCell1(2, 1)).getEntity() instanceof Gem);

        assertTrue(gameBoard.getCell1(2, 2) instanceof StopCell);
        assertNull(((StopCell) gameBoard.getCell1(2, 2)).getEntity());
    }

    @Test
    @Tag("provided")
    @DisplayName("Deserialization Test - Limited Lives")
    void testDeserializeLimitedLives() {
        final var source = String.join(System.lineSeparator(), "3", "3", "10", "P..", "MWL", ".GS");

        final GameState gameState;
        try (final var reader = new BufferedReader(new StringReader(source))) {
            gameState = GameStateSerializer.loadFrom(reader);
        } catch (IOException e) {
            fail(e);
            throw new AssertionError();
        }

        assertEquals(10, gameState.getNumLives());

        final var gameBoard = gameState.getGameBoard();
        assertEquals(3, gameBoard.getNumRows());
        assertEquals(3, gameBoard.getNumCols());

        assertTrue(gameBoard.getCell1(0, 0) instanceof StopCell);
        assertTrue(((StopCell) gameBoard.getCell1(0, 0)).getEntity() instanceof Player);

        assertTrue(gameBoard.getCell1(0, 1) instanceof EntityCell);
        assertNull(((EntityCell) gameBoard.getCell1(0, 1)).getEntity());

        assertTrue(gameBoard.getCell1(0, 2) instanceof EntityCell);
        assertNull(((EntityCell) gameBoard.getCell1(0, 2)).getEntity());

        assertTrue(gameBoard.getCell1(1, 0) instanceof EntityCell);
        assertTrue(((EntityCell) gameBoard.getCell1(1, 0)).getEntity() instanceof Mine);

        assertTrue(gameBoard.getCell1(1, 1) instanceof Wall);

        assertTrue(gameBoard.getCell1(1, 2) instanceof EntityCell);
        assertTrue(((EntityCell) gameBoard.getCell1(1, 2)).getEntity() instanceof ExtraLife);

        assertTrue(gameBoard.getCell1(2, 0) instanceof EntityCell);
        assertNull(((EntityCell) gameBoard.getCell1(2, 0)).getEntity());

        assertTrue(gameBoard.getCell1(2, 1) instanceof EntityCell);
        assertTrue(((EntityCell) gameBoard.getCell1(2, 1)).getEntity() instanceof Gem);

        assertTrue(gameBoard.getCell1(2, 2) instanceof StopCell);
        assertNull(((StopCell) gameBoard.getCell1(2, 2)).getEntity());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }
}
