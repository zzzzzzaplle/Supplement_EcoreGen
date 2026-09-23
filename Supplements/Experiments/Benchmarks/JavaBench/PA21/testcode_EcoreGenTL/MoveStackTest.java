package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MoveStackTest {

    private MoveStack moveStack;

    // 辅助函数：创建MoveStack对象
    private static MoveStack createMoveStack() {
        return Pa21Factory.eINSTANCE.createMoveStack();
    }

    // 辅助函数：创建Position对象
    private static Position createPosition(int row, int col) {
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        return pos;
    }

    // 辅助函数：创建Alive对象
    private static Alive createAlive(Position newPosition, Position origPosition) {
        Alive alive = Pa21Factory.eINSTANCE.createAlive();
        alive.setNewPosition(newPosition);
        alive.setOrigPosition(origPosition);
        return alive;
    }

    @BeforeEach
    void setUp() {
        moveStack = createMoveStack();
    }


    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = MoveStack.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // push, isEmpty, pop, getPopCount, peek, getMoves, setPopCount
        assertTrue(publicMethods.length >= 5);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("push", MoveResult.class));
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
                createPosition(0, 0)
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
                createPosition(0, 0)
        );

        moveStack.push(move);

        final var poppedElem = assertDoesNotThrow(() -> moveStack.pop());

        assertTrue(moveStack.isEmpty());
        assertEquals(move, poppedElem);
        assertEquals(1, moveStack.getPopCount());
    }

    @AfterEach
    void tearDown() {
        moveStack = null;
    }
}
