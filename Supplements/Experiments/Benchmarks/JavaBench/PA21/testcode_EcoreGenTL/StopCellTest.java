package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StopCellTest {

    private Position position;
    private StopCell cell;

    // 辅助函数：创建Position对象
    private static Position createPosition(int row, int col) {
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        return pos;
    }

    // 辅助函数：创建StopCell对象
    private static StopCell createStopCell(Position pos) {
        StopCell cell = Pa21Factory.eINSTANCE.createStopCell();
        cell.setPosition(pos);
        return cell;
    }

    // 辅助函数：创建StopCell对象（带Entity）
    private static StopCell createStopCell(Position pos, Entity entity) {
        StopCell cell = Pa21Factory.eINSTANCE.createStopCell();
        cell.setPosition(pos);
        if (entity != null) {
            cell.setEntity(entity);
        }
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



    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = StopCell.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // setPlayer, toUnicodeChar, toASCIIChar (继承的方法)
        assertTrue(publicMethods.length >= 3);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("setPlayer", Player.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toUnicodeChar"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toASCIIChar"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = StopCell.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }


    @Test
    @Tag("provided")
    @DisplayName("Set Player - Null to Not-Null")
    void testSetPlayerFromNotNullToNotNull() {
        final var origPlayer = createPlayer();
        position = createPosition(0, 0);
        cell = createStopCell(position, origPlayer);

        final var newPlayer = createPlayer();
    
        final var result = cell.setPlayer(newPlayer);

        assertSame(newPlayer, cell.getEntity());
        assertSame(cell, newPlayer.getOwner());
        assertNull(origPlayer.getOwner());
        assertSame(newPlayer, result);
    }

    @AfterEach
    void tearDown() {
        cell = null;
        position = null;
    }
}
