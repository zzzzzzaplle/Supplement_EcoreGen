import java.util.Stack;

/**
 * Stack for tracking placed cells for undo operations.
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
     * @param cell the cell to push
     */
    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    /**
     * Pops the last placed cell from the stack.
     *
     * @return the popped cell, or null if stack is empty
     */
    public FillableCell pop() {
        if (cellStack.isEmpty()) {
            return null;
        }
        count++;
        return cellStack.pop();
    }

    /**
     * Returns the number of successful undo operations.
     *
     * @return the undo count
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
