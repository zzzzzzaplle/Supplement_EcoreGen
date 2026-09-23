import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class StopCellTest {

    private Position position;
    private StopCell cell;

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Constructors")
    void testConstructors() {
        final var clazz = StopCell.class;
        final var ctors = ReflectionUtils.getPublicConstructors(clazz);

        assertEquals(2, ctors.length);

        assertDoesNotThrow(() -> clazz.getConstructor(Position.class));
        assertDoesNotThrow(() ->
            clazz.getConstructor(Position.class, Entity.class)
        );
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = StopCell.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(
            clazz
        );

        assertEquals(4, publicMethods.length);

        assertDoesNotThrow(() ->
            clazz.getDeclaredMethod("setentity", Entity.class)
        );
        assertDoesNotThrow(() ->
            clazz.getDeclaredMethod("setPlayer", Player.class)
        );
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toUnicodeChar"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toASCIIChar"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = StopCell.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @AfterEach
    void tearDown() {
        cell = null;
        position = null;
    }
}
