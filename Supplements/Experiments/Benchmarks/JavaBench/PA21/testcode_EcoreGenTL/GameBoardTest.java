package edu.pa21;

import edu.pa21.util.GameBoardHelper;
import edu.pa21.util.GameBoardUtils;
import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    private GameBoard gameBoard;

    // 辅助函数：创建Position对象
    private static Position createPosition(int row, int col) {
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        return pos;
    }

    // 辅助函数：创建EntityCell对象
    private static EntityCell createEntityCell(int row, int col) {
        EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
        cell.setPosition(createPosition(row, col));
        return cell;
    }

    // 辅助函数：创建Player对象
    private static Player createPlayer() {
        return Pa21Factory.eINSTANCE.createPlayer();
    }

    // 辅助函数：创建Gem对象
    private static Gem createGem() {
        return Pa21Factory.eINSTANCE.createGem();
    }

    // 辅助函数：创建Wall对象
    private static Wall createWall(int row, int col) {
        Wall wall = Pa21Factory.eINSTANCE.createWall();
        wall.setPosition(createPosition(row, col));
        return wall;
    }



    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = GameBoard.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // getRow, getCol, getCell1, getCell2, getEntityCell1, getEntityCell2, getNumRows, getNumCols, getPlayer, etc.
        assertTrue(publicMethods.length >= 8);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getRow", int.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getCol", int.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getCell1", int.class, int.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getCell2", Position.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getEntityCell2", int.class, int.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getNumRows"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getNumCols"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getPlayer"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = GameBoard.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    // P*.
    // ...
    // ...
    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Valid")
    void testBasicGameBoardCreation() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][0]).setEntity(createPlayer());
        ((EntityCell) cells[0][1]).setEntity(createGem());

        assertDoesNotThrow(() -> gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells));
        assertTrue(((EntityCell) gameBoard.getCell1(0, 0)).getEntity() instanceof Player);
        assertTrue(((EntityCell) gameBoard.getCell1(0, 1)).getEntity() instanceof Gem);
    }

    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Bad Row")
    void testGameBoardCreationBadRowCount() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(2, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][0]).setEntity(createPlayer());
        ((EntityCell) cells[0][1]).setEntity(createGem());

        assertThrows(IllegalArgumentException.class, () -> gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells));
    }

    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Bad Column")
    void testGameBoardCreationBadColCount() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, 2, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][0]).setEntity(createPlayer());
        ((EntityCell) cells[0][1]).setEntity(createGem());

        assertThrows(IllegalArgumentException.class, () -> gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells));
    }

    // .*.
    // ...
    // ...
    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Missing Player")
    void testGameBoardCreationMissingPlayer() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][1]).setEntity(createGem());

        assertThrows(IllegalArgumentException.class, () -> gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells));
    }

    // PP*
    // ...
    // ...
    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Too Many Players")
    void testGameBoardCreationMissingTooManyPlayers() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][0]).setEntity(createPlayer());
        ((EntityCell) cells[0][1]).setEntity(createPlayer());
        ((EntityCell) cells[0][2]).setEntity(createGem());

        assertThrows(IllegalArgumentException.class, () -> gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells));
    }

    // P..
    // ...
    // ...
    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - No Gems")
    void testGameBoardCreationMissingGem() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][0]).setEntity(createPlayer());

        assertThrows(IllegalArgumentException.class, () -> gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells));
    }

    // P....
    // WWWW.
    // .....
    // .WWWW
    // ...W*
    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Unreachable Gem in Linear Path")
    void testGameBoardCreationUnreachableGemLinear() {
        final var rows = 5;
        final var cols = 5;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][0]).setEntity(createPlayer());
        ((EntityCell) cells[4][4]).setEntity(createGem());

        for (int c = 0; c < cols - 1; ++c) {
            cells[1][c] = createWall(1, c);
        }
        for (int c = 1; c < cols; ++c) {
            cells[3][c] = createWall(3, c);
        }
        cells[4][3] = createWall(4, 3);

        assertThrows(IllegalArgumentException.class, () -> gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells));
    }

    @Test
    @Tag("provided")
    @DisplayName("Get Cell - int-overload")
    void testGetCellWithInts() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][0]).setEntity(createPlayer());
        ((EntityCell) cells[0][2]).setEntity(createGem());

        gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells);

        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                assertEquals(cells[r][c], gameBoard.getCell1(r, c), "Mismatch for r=" + r + " c=" + c);
            }
        }
    }

    @Test
    @Tag("provided")
    @DisplayName("Get Entity Cell - Invalid using int-overload")
    void testGetEntityCellWithIntsInvalid() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        ((EntityCell) cells[0][0]).setEntity(createPlayer());
        cells[0][1] = createWall(0, 1);
        ((EntityCell) cells[0][2]).setEntity(createGem());

        gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells);

        // EMF版本的getEntityCell2返回null而不是抛出异常
        assertNull(gameBoard.getEntityCell2(0,  1));
    }

    // P**
    // *.*
    // ***
    @Test
    @Tag("provided")
    @DisplayName("Get Number of Gems - Filled")
    void testGetNumGemsAll() {
        final var rows = 3;
        final var cols = 3;
        final var cells = GameBoardUtils.createEmptyCellArray(rows, cols, (pos) -> {
            EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
            cell.setPosition(pos);
            return cell;
        });

        for (final var row : cells) {
            for (final var c : row) {
                ((EntityCell) c).setEntity(createGem());
            }
        }
        ((EntityCell) cells[0][0]).setEntity(createPlayer());
        ((EntityCell) cells[1][1]).setEntity(null);

        gameBoard = GameBoardHelper.createGameBoard(rows, cols, cells);

        // 使用GameBoardHelper.getNumGems方法
        assertEquals(7, GameBoardHelper.getNumGems(gameBoard));
    }

    @AfterEach
    void tearDown() {
        gameBoard = null;
    }
}
