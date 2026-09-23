package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityCellTest {

    private Position position;
    private EntityCell cell;

    // 辅助函数：创建Position对象
    private static Position createPosition(int row, int col) {
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        return pos;
    }

    // 辅助函数：创建EntityCell对象
    private static EntityCell createEntityCell(Position pos) {
        EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
        cell.setPosition(pos);
        return cell;
    }

    // 辅助函数：创建EntityCell对象（带Entity）
    private static EntityCell createEntityCell(Position pos, Entity entity) {
        EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
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



    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = EntityCell.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Position Only")
    void testInstanceCreationWithPos() {
        position = createPosition(0, 0);
        cell = createEntityCell(position);

        assertEquals(0, cell.getPosition().getRow());
        assertEquals(0, cell.getPosition().getCol());
        assertNull(cell.getEntity());
    }

    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Position and Null Entity")
    void testInstanceCreationWithPosAndNullEntity() {
        position = createPosition(0, 0);
        cell = createEntityCell(position, null);

        assertEquals(0, cell.getPosition().getRow());
        assertEquals(0, cell.getPosition().getCol());
        assertNull(cell.getEntity());
    }

    @Test
    @Tag("provided")
    @DisplayName("Instance Creation - Position and Not-Null Entity")
    void testInstanceCreationWithPosAndNotNullEntity() {
        final var player = createPlayer();
        position = createPosition(0, 0);
        cell = createEntityCell(position, player);

        assertEquals(0, cell.getPosition().getRow());
        assertEquals(0, cell.getPosition().getCol());
        assertSame(player, cell.getEntity());
    }

    @Test
    @Tag("provided")
    @DisplayName("Get Entity - Null Entity")
    void testGetEntityNull() {
        position = createPosition(0, 0);
        cell = createEntityCell(position);

        assertNull(cell.getEntity());
    }

    @Test
    @Tag("provided")
    @DisplayName("Get Entity - Not-Null Entity")
    void testGetEntityNotNull() {
        final var player = createPlayer();
        position = createPosition(0, 0);
        cell = createEntityCell(position, player);

        assertNotNull(cell.getEntity());
    }

    @Test
    @Tag("provided")
    @DisplayName("Set Entity - Null to Not-Null")
    void testSetEntityFromNotNullToNotNull() {
        final var origPlayer = createPlayer();
        position = createPosition(0, 0);
        cell = createEntityCell(position, origPlayer);

        final var newPlayer = createPlayer();
        // EMF的setEntity返回void
        cell.setEntity(newPlayer);

        assertSame(newPlayer, cell.getEntity());
        assertSame(cell, newPlayer.getOwner());
        assertNull(origPlayer.getOwner());
    }

    @AfterEach
    void tearDown() {
        cell = null;
        position = null;
    }
}
