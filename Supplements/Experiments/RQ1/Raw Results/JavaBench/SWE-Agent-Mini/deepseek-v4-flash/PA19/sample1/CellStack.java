import java.util.Stack;

/**
 * A stack that tracks placed fillable cells for undo operations.
 */
public class CellStack {

    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    /**
     * Pushes a fillable cell onto the stack.
     *
     * @param cell the cell to push
     */
    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    /**
     * Pops the most recently placed cell from the stack.
     *
     * @return the popped cell
     */
    public FillableCell pop() {
        FillableCell cell = cellStack.pop();
        count++;
        return cell;
    }

    /**
     * Returns the number of successful undo actions.
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
