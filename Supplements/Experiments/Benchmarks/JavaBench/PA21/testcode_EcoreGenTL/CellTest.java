package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

//    @SuppressWarnings({"Since15", "preview"})
//    @Test
//    @Tag("sanity")
//    @DisplayName("Sanity Test - Class is Sealed")
//    void testClassIsSealed() {
//        assertTrue(Cell.class.isSealed());
//    }

 

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = Cell.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // getPosition, setPosition, toUnicodeChar, toASCIIChar
        assertTrue(publicMethods.length >= 1);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getPosition"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = Cell.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }
}
