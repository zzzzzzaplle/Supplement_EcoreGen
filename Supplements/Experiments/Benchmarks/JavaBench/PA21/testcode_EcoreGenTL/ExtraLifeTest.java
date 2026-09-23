package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExtraLifeTest {

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Constructors")
    void testConstructors() {
        final var clazz = ExtraLife.class;
        final var ctors = ReflectionUtils.getPublicConstructors(clazz);

        // EMF生成的类使用protected构造函数，通过工厂创建
        assertEquals(0, ctors.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = ExtraLife.class;
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
        final var clazz = ExtraLife.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }
}
