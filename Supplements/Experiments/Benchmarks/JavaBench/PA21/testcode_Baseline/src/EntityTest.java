import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

public class EntityTest {

    private Entity entity = null;

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

    private static Player createPlayer(EntityCell owner) {
        Player p = new Player();
        p.setowner(owner);
        return p;
    }

    @BeforeEach
    void setUp() {
        entity = new Player();
    }

   

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = Entity.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(
            clazz
        );

        assertEquals(3, publicMethods.length);

        assertDoesNotThrow(() ->
            clazz.getDeclaredMethod("setOwner", EntityCell.class)
        );
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getOwner"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = Entity.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("provided")
    @DisplayName("Get Owner - Test Initially Null")
    void testGetOwnerNull() {
        assertNull(entity.getOwner());
    }

    @Test
    @Tag("provided")
    @DisplayName("Get Owner - Test Not-Null after creation with cell")
    void testGetOwnerNotNull() {
        final var entityCell = createEntityCell(createPosition(0, 0));

        entity = createPlayer(entityCell);

        assertEquals(entityCell, entity.getOwner());
    }

    @Test
    @Tag("actual")
    @DisplayName("Set Owner - Not-Null to Null")
    void testSetOwnerNotNullToNull() {
        final var entityCell = createEntityCell(createPosition(0, 0));

        entity = createPlayer(entityCell);

        final var prevOwner = entity.setowner(null);

        assertNull(entity.getOwner());
        assertEquals(entityCell, prevOwner);
    }

    @Test
    @Tag("provided")
    @DisplayName("Set Owner - Not-Null to Not-Null")
    void testSetOwnerNotNullToNotNull() {
        final var prevEntityCell = createEntityCell(createPosition(0, 0));
        final var newEntityCell = createEntityCell(createPosition(1, 0));

        entity = createPlayer(prevEntityCell);

        final var prevOwner = entity.setowner(newEntityCell);
        assertEquals(newEntityCell, entity.getOwner());
        assertEquals(prevEntityCell, prevOwner);
    }

    @AfterEach
    void tearDown() {
        entity = null;
    }
}
