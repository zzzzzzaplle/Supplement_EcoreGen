import java.util.Stack;

/**
 * Stack of placed FillableCells to support undo operations.
 */
public class CellStack {

    private Stack<FillableCell> cellStack;
    private int count;

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

    public int getUndoCount() {
        return count;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        if (cellStack.isEmpty()) {
            return null;
        }
        FillableCell cell = cellStack.pop();
        count++;
        return cell;
    }

    public boolean isEmpty() {
        return cellStack.isEmpty();
    }

    public int size() {
        return cellStack.size();
    }

    public FillableCell peek() {
        return cellStack.isEmpty() ? null : cellStack.peek();
    }
}
