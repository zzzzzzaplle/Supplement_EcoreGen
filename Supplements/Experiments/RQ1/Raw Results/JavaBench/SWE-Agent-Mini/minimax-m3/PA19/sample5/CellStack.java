import java.util.Stack;

/**
 * Stack of placed fillable cells, tracks undo count of successful pops.
 */
public class CellStack {

    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        FillableCell c = cellStack.pop();
        count++;
        return c;
    }

    public int getUndoCount() {
        return count;
    }

    public Stack<FillableCell> getCellStack() {
        return cellStack;
    }

    public void setCellStack(Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
