import java.util.Stack;

/**
 * Stack holding the most recently placed fillable cells, used to support undo.
 * The count field tracks how many successful undo actions have been performed, not the stack size.
 */
public class CellStack {

    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    public void push(FillableCell cell) {
        this.cellStack.push(cell);
    }

    public FillableCell pop() {
        FillableCell cell = this.cellStack.pop();
        this.count++;
        return cell;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
