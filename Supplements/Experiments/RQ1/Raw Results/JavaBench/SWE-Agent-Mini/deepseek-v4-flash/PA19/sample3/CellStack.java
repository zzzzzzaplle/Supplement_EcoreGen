import java.util.Stack;

/**
 * Stack for tracking pipe placements (for undo functionality).
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
        cellStack.push(cell);
    }

    /**
     * Pops the last placed cell from the stack.
     */
    public FillableCell pop() {
        if (cellStack.isEmpty()) {
            return null;
        }
        FillableCell cell = cellStack.pop();
        count++;
        return cell;
    }

    /**
     * Returns the number of successful undo actions.
     */
    public int getUndoCount() {
        return count;
    }

    public Stack<FillableCell> getCellStack() {
        return cellStack;
    }

    public void setCellStack(Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
