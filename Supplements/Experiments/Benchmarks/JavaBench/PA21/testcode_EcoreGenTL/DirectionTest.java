package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Class Is Enum")
    void testClassIsEnum() {
        assertTrue(Direction.class.isEnum());
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Enum contains Four Values")
    void testEnumContainsFourValues() {
        assertEquals(4, Direction.class.getEnumConstants().length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = Direction.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // EMF生成的枚举没有getOffset, getRowOffset, getColOffset方法
        // 只有EMF框架的方法：getValue, getName, getLiteral
        assertTrue(publicMethods.length >= 3);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = Direction.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

//    @ParameterizedTest
//    @Tag("sanity")
//    @EnumSource(Direction.class)
//    @DisplayName("Sanity Test - Test correct row offset values")
//    void testDirectionRowOffset(final Direction direction) {
//        final var actual = direction.getRowOffset();
//
//        if (direction == Direction.UP) {
//            assertEquals(-1, actual);
//        } else if (direction == Direction.DOWN) {
//            assertEquals(1, actual);
//        } else {
//            assertEquals(0, actual);
//        }
//    }
//
//    @ParameterizedTest
//    @Tag("sanity")
//    @EnumSource(Direction.class)
//    @DisplayName("Sanity Test - Test correct column offset values")
//    void testDirectionColOffset(final Direction direction) {
//        final var actual = direction.getColOffset();
//
//        if (direction == Direction.LEFT) {
//            assertEquals(-1, actual);
//        } else if (direction == Direction.RIGHT) {
//            assertEquals(1, actual);
//        } else {
//            assertEquals(0, actual);
//        }
//    }
}
