package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    
    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = Player.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // toUnicodeChar, toASCIIChar (继承自Entity的方法)
        assertTrue(publicMethods.length >= 2);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toUnicodeChar"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toASCIIChar"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = Player.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }
}
