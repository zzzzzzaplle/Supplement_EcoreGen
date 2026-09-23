import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class EntityCellTest {

    private Position position;
    private EntityCell cell;

    private static Position createPosition(int row, int col) {
        Position p = new Position();
        p.setRow(row);
        p.setCol(col);
        return p;
    }

    private static EntityCell createEntityCell(Position pos) {
        EntityCell c = new EntityCell();
        c.setPosition(pos);
        return c;
    }

    private static EntityCell createEntityCell(Position pos, Entity entity) {
        EntityCell c = createEntityCell(pos);
        if (entity != null) {
            c.setentity(entity);
        }
        return c;
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = EntityCell.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertEquals(5, publicMethods.length);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("setEntity", Entity.class));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getEntity"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toUnicodeChar"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toASCIIChar"));
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
        final var player = new Player();
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
        final var player = new Player();
        position = createPosition(0, 0);
        cell = createEntityCell(position, player);

        assertNotNull(cell.getEntity());
    }

    @Test
    @Tag("provided")
    @DisplayName("Set Entity - Null to Not-Null")
    void testSetEntityFromNotNullToNotNull() {
        final var origPlayer = new Player();
        position = createPosition(0, 0);
        cell = createEntityCell(position, origPlayer);

        final var newPlayer = new Player();
        final var prev = cell.setentity(newPlayer);

        assertSame(newPlayer, cell.getEntity());
        assertSame(cell, newPlayer.getOwner());
        assertNull(origPlayer.getOwner());
        assertSame(origPlayer, prev);
    }

    @AfterEach
    void tearDown() {
        cell = null;
        position = null;
    }
}
