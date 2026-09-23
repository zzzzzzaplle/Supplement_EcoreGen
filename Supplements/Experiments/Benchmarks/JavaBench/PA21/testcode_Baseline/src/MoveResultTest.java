import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MoveResultTest {

    private static Position createPosition(int row, int col) {
        Position p = new Position();
        p.setRow(row);
        p.setCol(col);
        return p;
    }

    private static Invalid createInvalid(Position pos) {
        Invalid i = new Invalid();
        i.setNewPosition(pos);
        return i;
    }

    private static Alive createAlive(Position newPos, Position origPos, List<Position> gems, List<Position> lives) {
        Alive a = new Alive();
        a.setNewPosition(newPos);
        a.setOrigPosition(origPos);
        a.setCollectedGems(gems);
        a.setCollectedExtraLives(lives);
        return a;
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = MoveResult.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertEquals(0, publicMethods.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = MoveResult.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(1, publicFields.length);

        assertDoesNotThrow(() -> clazz.getDeclaredField("newPosition"));
    }

    
    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods for Invalid")
    void testInvalidPublicMethods() {
        final var clazz = Invalid.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertEquals(0, publicMethods.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields for Invalid")
    void testInvalidPublicFields() {
        final var clazz = Invalid.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods for Valid")
    void testValidPublicMethods() {
        final var clazz = Valid.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertEquals(0, publicMethods.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields for Valid")
    void testValidPublicFields() {
        final var clazz = Valid.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(1, publicFields.length);

        assertDoesNotThrow(() -> clazz.getDeclaredField("origPosition"));
    }


    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods for Alive")
    void testValidAlivePublicMethods() {
        final var clazz = Alive.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertEquals(0, publicMethods.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields for Alive")
    void testValidAlivePublicFields() {
        final var clazz = Alive.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(2, publicFields.length);

        assertDoesNotThrow(() -> clazz.getDeclaredField("collectedGems"));
        assertDoesNotThrow(() -> clazz.getDeclaredField("collectedExtraLives"));
    }
    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods for Dead")
    void testValidDeadPublicMethods() {
        final var clazz = Dead.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertEquals(0, publicMethods.length);
    }
    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields for Dead")
    void testValidDeadPublicFields() {
        final var clazz = Dead.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(1, publicFields.length);

        assertDoesNotThrow(() -> clazz.getDeclaredField("minePosition"));
    }
    @Test
    @Tag("actual")
    @DisplayName("Test Invalid - newPosition field")
    void testInvalidNewPosition() {
        Position pos = createPosition(2, 3);
        Invalid invalid = createInvalid(pos);
        assertEquals(pos, invalid.getNewPosition());
    }

    @Test
    @Tag("actual")
    @DisplayName("Test Alive - fields (newPosition, origPosition, collectedGems, collectedExtraLives)")
    void testAliveFields() {
        Position newPos = createPosition(1, 1);
        Position origPos = createPosition(0, 0);
        List<Position> gems = List.of(createPosition(0, 1));
        List<Position> lives = List.of(createPosition(0, 2));

        Alive alive = createAlive(newPos, origPos, gems, lives);

        assertEquals(newPos, alive.getNewPosition());
        assertEquals(origPos, alive.getOrigPosition());
        assertIterableEquals(gems, alive.getCollectedGems());
        assertIterableEquals(lives, alive.getCollectedExtraLives());
    }
}
