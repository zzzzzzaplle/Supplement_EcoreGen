import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.*;

public class MoveStackTest {

    private MoveStack moveStack;

    private static Position createPosition(int row, int col) {
        Position p = new Position();
        p.setRow(row);
        p.setCol(col);
        return p;
    }

    private static Alive createAlive(Position newPos, Position origPos, List<Position> gems, List<Position> lives) {
        Alive a = new Alive();
        a.setNewPosition(newPos);
        a.setOrigPosition(origPos);
        a.setCollectedGems(gems);
        a.setCollectedExtraLives(lives);
        return a;
    }

    @BeforeEach
    void setUp() {
        moveStack = new MoveStack();
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = MoveStack.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(
            clazz
        );

        assertEquals(5, publicMethods.length);

        assertDoesNotThrow(() ->
            clazz.getDeclaredMethod("push", MoveResult.class)
        );
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("isEmpty"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("pop"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("getPopCount"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("peek"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = MoveStack.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("provided")
    @DisplayName("Initial State")
    void testInitialState() {
        assertEquals(0, moveStack.getPopCount());
        assertTrue(moveStack.isEmpty());
    }

    @Test
    @Tag("provided")
    @DisplayName("Push Move")
    void testPush() {
        final var move = createAlive(
            createPosition(1, 0),
            createPosition(0, 0),
            Collections.emptyList(),
            Collections.emptyList()
        );

        moveStack.push(move);

        assertFalse(moveStack.isEmpty());
        assertEquals(moveStack.peek(), move);
    }

    @Test
    @Tag("provided")
    @DisplayName("Pop Move")
    void testPop() {
        final var move = createAlive(
            createPosition(1, 0),
            createPosition(0, 0),
            Collections.emptyList(),
            Collections.emptyList()
        );

        moveStack.push(move);

        final var poppedElem = assertDoesNotThrow(() -> moveStack.pop());

        assertTrue(moveStack.isEmpty());
        assertEquals(move, poppedElem);
        assertEquals(1, moveStack.getPopCount());
    }

    @Test
    @Tag("actual")
    @DisplayName("Push-Pop Moves in LIFO Order")
    void testPushPopInLIFOOrder() {
        final var move1 = createAlive(
            createPosition(1, 0),
            createPosition(0, 0),
            Collections.emptyList(),
            Collections.emptyList()
        );
        final var move2 = createAlive(
            createPosition(2, 0),
            createPosition(1, 0),
            Collections.emptyList(),
            Collections.emptyList()
        );
        final var move3 = createAlive(
            createPosition(3, 0),
            createPosition(2, 0),
            Collections.emptyList(),
            Collections.emptyList()
        );

        moveStack.push(move1);
        moveStack.push(move2);
        moveStack.push(move3);

        assertEquals(move3, moveStack.peek());

        assertEquals(move3, moveStack.pop());
        assertEquals(1, moveStack.getPopCount());

        assertEquals(move2, moveStack.pop());
        assertEquals(2, moveStack.getPopCount());

        assertEquals(move1, moveStack.pop());
        assertEquals(3, moveStack.getPopCount());

        assertTrue(moveStack.isEmpty());
    }

    @AfterEach
    void tearDown() {
        moveStack = null;
    }
}
