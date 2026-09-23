import java.util.Stack;

/**
 * Stack of FillableCell for undo functionality.
 */
public class CellStack {

    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        cellStack = new Stack<>();
        count = 0;
    }

    public Stack<FillableCell> getCellStack() {
        return cellStack;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        count++;
        return cellStack.pop();
    }

    public int getUndoCount() {
        return count;
    }
}
