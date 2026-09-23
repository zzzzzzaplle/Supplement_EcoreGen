import java.util.Stack;

/**
 * Stack tracking placed fillable cells for undo.
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
        if (cellStack.isEmpty()) {
            return null;
        }
        FillableCell cell = cellStack.pop();
        count++;
        return cell;
    }

    public int getUndoCount() {
        return count;
    }

    public boolean isEmpty() {
        return cellStack.isEmpty();
    }
}
