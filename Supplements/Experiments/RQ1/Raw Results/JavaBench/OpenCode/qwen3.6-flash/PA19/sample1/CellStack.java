import java.util.Stack;

/**
 * Stack used to track placed cells for undo operations.
 */
public class CellStack {

    private  Stack<FillableCell> cellStack;
    private  int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
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
        count++;
        return cellStack.pop();
    }

    /**
     * Counts the number of successful undo actions (i.e., successful pop
     * operations), not the current stack size.
     */
    public int getUndoCount() {
        return count;
    }
}
