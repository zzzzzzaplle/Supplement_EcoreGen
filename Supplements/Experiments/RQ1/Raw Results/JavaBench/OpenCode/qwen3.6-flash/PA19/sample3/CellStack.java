import java.util.Optional;
import java.util.Stack;

/**
 * A stack-based structure for tracking placed FillableCells to support undo.
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
        count++;
        return cellStack.pop();
    }

    public int getUndoCount() {
        return count;
    }

    public boolean isEmpty() {
        return cellStack.isEmpty();
    }
}
