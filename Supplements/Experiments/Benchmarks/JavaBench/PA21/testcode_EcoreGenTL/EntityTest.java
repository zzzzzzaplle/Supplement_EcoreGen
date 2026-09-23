package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    private Entity entity = null;

    // 辅助函数：创建Player对象
    private static Player createPlayer() {
        return Pa21Factory.eINSTANCE.createPlayer();
    }

    // 辅助函数：创建EntityCell对象
    private static EntityCell createEntityCell(int row, int col) {
        EntityCell cell = Pa21Factory.eINSTANCE.createEntityCell();
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        cell.setPosition(pos);
        return cell;
    }

    // 辅助函数：创建Position对象
    private static Position createPosition(int row, int col) {
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        return pos;
    }

    @BeforeEach
    void setUp() {
        entity = createPlayer();
    }

//    @SuppressWarnings({"Since15", "preview"})
//    @Test
//    @Tag("sanity")
//    @DisplayName("Sanity Test - Class is Sealed")
//    void testClassIsSealed() {
//        assertTrue(Entity.class.isSealed());
//    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Class is Abstract")
    void testClassIsAbstract() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(Entity.class.getModifiers()));
    }

 

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = Entity.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // getOwner, setOwner, toUnicodeChar, toASCIIChar
        assertTrue(publicMethods.length >= 2);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("setOwner", EntityCell.class));
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
    @DisplayName("Get Owner - Test Not-Null after setOwner")
    void testGetOwnerNotNull() {
        final var entityCell = createEntityCell(0, 0);

        entity.setOwner(entityCell);

        assertEquals(entityCell, entity.getOwner());
    }

    @Test
    @Tag("provided")
    @DisplayName("Set Owner - Not-Null to Not-Null")
    void testSetOwnerNotNullToNotNull() {
        final var prevEntityCell = createEntityCell(0, 0);
        final var newEntityCell = createEntityCell(1, 0);

        entity.setOwner(prevEntityCell);

        // EMF的setOwner返回void，不能获取返回值
        entity.setOwner(newEntityCell);
        assertEquals(newEntityCell, entity.getOwner());
    }

    @AfterEach
    void tearDown() {
        entity = null;
    }
}
