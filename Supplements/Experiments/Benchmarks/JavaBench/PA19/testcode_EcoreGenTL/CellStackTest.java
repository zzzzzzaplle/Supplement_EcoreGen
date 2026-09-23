import edu.pa19.CellStack;
import edu.pa19.Coordinate;
import edu.pa19.FillableCell;
import edu.pa19.Pa19Factory;
import edu.pa19.Pipe;
import edu.pa19.PipeShape;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CellStackTest {

    private CellStack stack;
    private Pa19Factory factory;

    @BeforeEach
    void setUp() {
        factory = Pa19Factory.eINSTANCE;
        stack = factory.createCellStack();
    }

    @AfterEach
    void tearDown() {
        stack = null;
        factory = null;
    }

    /**
     * Tests whether popping an empty stack throws.
     */
    @Test
    void givenEmptyStack_ifPop_returnNull() {
    	  FillableCell result = stack.pop();
    	        assertNull(result);
    }

    /**
     * Tests whether pushing into an empty stack does not throw.
     */
    @Test
    void givenEmptyStack_ifPush_noThrow() {
        assertDoesNotThrow(() -> {
            stack.push(makeCell(1, 1, PipeShape.HORIZONTAL));
            stack.push(makeCell(1, 2, PipeShape.VERTICAL));
            stack.push(makeCell(1, 3, PipeShape.CROSS));
        });
    }

    /**
     * Tests whether popping from a one-element stack returns the element.
     */
    @Test
    void givenStack_whenPop_returnElement() {
        final var cell = makeCell(1, 1, PipeShape.CROSS);
        final var coord = cell.getCoord();

        final var pipe = cell.getPipe();
        stack.push(cell);

        final var poppedCell = stack.pop();
        assertNotNull(poppedCell);
        assertEquals(coord.getRow(), poppedCell.getCoord().getRow());
        assertEquals(coord.getCol(), poppedCell.getCoord().getCol());
        assertEquals(pipe, poppedCell.getPipe());
    }

    /**
     * Tests whether a initially-constructed stack has an undo count of 0.
     */
    @Test
    void givenEmptyStack_assertUndoCountEqualsZero() {
        assertEquals(0, stack.getUndoCount());
    }

    /**
     * Tests whether pushing an element into the empty stack leaves the undo count unchanged.
     * <p>
     * Succeeds if the undo count remains unchanged after pushing.
     * </p>
     */
    @Test
    void givenEmptyStack_whenPush_undoCountDoesNotChange() {
        int originalCount = stack.getUndoCount();
        stack.push(makeCell(1, 1, PipeShape.HORIZONTAL));
        assertEquals(originalCount, stack.getUndoCount());
    }

    /**
     * Tests whether popping an element from a stack changes the undo count.
     * <p>
     * Succeeds if the undo count increments by 1 after popping.
     * </p>
     */
    @Test
    void givenStack_whenPop_incUndoCount() {
        int originalCount = stack.getUndoCount();

        stack.push(makeCell(1, 1, PipeShape.HORIZONTAL));
        stack.pop();

        assertEquals(originalCount + 1, stack.getUndoCount());
    }

    /**
     * Tests whether popping an element from an empty stack changes the undo count.
     * <p>
     * Succeeds if the undo count remains unchanged after a failed pop.
     * </p>
     */
    @Test
    void givenEmptyStack_whenPop_undoCountDoesNotChange() {
        int originalCount = stack.getUndoCount();

        assertNull(stack.pop());
        assertEquals(originalCount, stack.getUndoCount());
    }

    /**
     * Tests whether the push/pop operations has Last-In-First-Out behavior.
     * <p>
     * Succeeds if the elements returned by popping are the reverse order of which the elements are inserted.
     * </p>
     */
    @Test
    void givenEmptyStack_whenPushThenPop_assertLIFOBehavior() {
        final var MAX_COUNT = PipeShape.values().length;
        final var list = new ArrayList<FillableCell>();

        for (int i = 0; i < MAX_COUNT; ++i) {
            var cell = makeCell(i + 1, i + 1, PipeShape.values()[i]);
            stack.push(cell);
            list.add(cell);
        }

        while (!list.isEmpty()) {
            FillableCell c = stack.pop();
            final var expected = list.remove(list.size() - 1);
            assertEquals(expected.getCoord().getRow(), c.getCoord().getRow());
            assertEquals(expected.getCoord().getCol(), c.getCoord().getCol());
            assertEquals(expected.getPipe(), c.getPipe());
        }
    }

    private FillableCell makeCell(int row, int col, PipeShape shape) {
        Coordinate coord = factory.createCoordinate();
        coord.setRow(row);
        coord.setCol(col);

        Pipe pipe = factory.createPipe();
        pipe.setShape(shape);
        pipe.setFilled(false);

        FillableCell cell = factory.createFillableCell();
        cell.setCoord(coord);
        cell.setPipe(pipe);
        return cell;
    }
}
