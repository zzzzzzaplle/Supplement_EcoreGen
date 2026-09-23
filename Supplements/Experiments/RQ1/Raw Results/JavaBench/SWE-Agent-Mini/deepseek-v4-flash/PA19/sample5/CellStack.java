import java.util.Stack;

/**
 * A stack for tracking pipe placements, supporting undo operations.
 */
public class CellStack {

    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    /**
     * Pushes a cell onto the stack.
     *
     * @param cell the FillableCell to push
     */
    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    /**
     * Pops the last placed cell from the stack.
     *
     * @return the FillableCell that was popped
     */
    public FillableCell pop() {
        if (cellStack.isEmpty()) {
            return null;
        }
        count++;
        return cellStack.pop();
    }

    /**
     * Returns the number of successful undo actions (pop operations).
     *
     * @return the undo count
     */
    public int getUndoCount() {
        return count;
    }
}
