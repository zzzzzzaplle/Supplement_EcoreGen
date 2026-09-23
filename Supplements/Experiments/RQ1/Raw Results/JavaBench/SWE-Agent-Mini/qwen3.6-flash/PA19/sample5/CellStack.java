import java.util.Stack;

/**
 * Stack for tracking placed FillableCells for undo functionality.
 * undoCount counts the number of successful undo actions, not the current stack size.
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
     */
    public void push(FillableCell cell) {
        if (cell != null) {
            cellStack.push(cell);
        }
    }

    /**
     * Pops the last placed cell from the stack.
     */
    public FillableCell pop() {
        if (!cellStack.isEmpty()) {
            count++;
            return cellStack.pop();
        }
        return null;
    }

    /**
     * Returns the number of successful undo actions (successful pop operations).
     */
    public int getUndoCount() {
        return count;
    }
}
