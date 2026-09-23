package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    private Position position = null;

    // 辅助函数：创建Position对象
    private static Position createPosition(int row, int col) {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("row and col must be non-negative");
        }
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        return pos;
    }

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
        final var clazz = Position.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // EMF生成的方法名带数字后缀
        assertDoesNotThrow(() -> clazz.getMethod("offsetBy1", int.class, int.class));
        assertDoesNotThrow(() -> clazz.getMethod("offsetBy2", PositionOffset.class));
        assertDoesNotThrow(() -> clazz.getMethod("offsetByOrNull1", int.class, int.class, int.class, int.class));
        assertDoesNotThrow(() -> clazz.getMethod("offsetByOrNull2", PositionOffset.class, int.class, int.class));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = Position.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Creation with Valid Values")
    void testCreation() {
        assertDoesNotThrow(() -> position = createPosition(0, 0));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Creation with Invalid Values")
    void testCreationFailure() {
        assertThrows(IllegalArgumentException.class, () -> position = createPosition(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> position = createPosition(0, -1));
        assertThrows(IllegalArgumentException.class, () -> position = createPosition(-1, -1));
    }

    @Test
    @Tag("actual")
    @DisplayName("Get Offset - int-overload")
    void testOffsetByInts() {
        final var origPosition = createPosition(3, 5);
        position = origPosition.offsetBy1(1, -3);

        assertEquals(4, position.getRow());
        assertEquals(2, position.getCol());
    }

    @Test
    @Tag("actual")
    @DisplayName("Get Offset - PositionOffset-overload")
    void testOffsetByPosOffset() {
        final var origPosition = createPosition(3, 5);
        final var offset = createPositionOffset(1, -3);
        position = origPosition.offsetBy2(offset);

        assertEquals(4, position.getRow());
        assertEquals(2, position.getCol());
    }

    @AfterEach
    void tearDown() {
        position = null;
    }
}
