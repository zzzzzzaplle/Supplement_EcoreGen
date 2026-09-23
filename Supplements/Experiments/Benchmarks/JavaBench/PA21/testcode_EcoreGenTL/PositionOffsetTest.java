package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionOffsetTest {

    private PositionOffset posOffset = null;

    // 辅助函数：创建PositionOffset对象
    private static PositionOffset createPositionOffset(int dRow, int dCol) {
        PositionOffset offset = Pa21Factory.eINSTANCE.createPositionOffset();
        offset.setDRow(dRow);
        offset.setDCol(dCol);
        return offset;
    }



    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = PositionOffset.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // getDRow, getDCol, setDRow, setDCol
        assertTrue(publicMethods.length >= 2);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getDRow"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getDCol"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = PositionOffset.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Creation")
    void testCreation() {
        assertDoesNotThrow(() -> posOffset = createPositionOffset(0, 0));
        assertDoesNotThrow(() -> posOffset = createPositionOffset(1, 0));
        assertDoesNotThrow(() -> posOffset = createPositionOffset(1, -1));
    }

    @Test
    @Tag("actual")
    @DisplayName("Test Getters and Setters")
    void testGettersAndSetters() {
        posOffset = createPositionOffset(1, -1);

        assertEquals(1, posOffset.getDRow());
        assertEquals(-1, posOffset.getDCol());

        posOffset.setDRow(2);
        posOffset.setDCol(-2);

        assertEquals(2, posOffset.getDRow());
        assertEquals(-2, posOffset.getDCol());
    }
}
