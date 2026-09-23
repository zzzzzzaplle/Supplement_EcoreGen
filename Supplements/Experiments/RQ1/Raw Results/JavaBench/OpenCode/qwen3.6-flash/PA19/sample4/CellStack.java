import java.util.Stack;

public class CellStack {
    private final Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
        this.count = 0;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
        count++;
    }

    public FillableCell pop() {
        if (!cellStack.isEmpty()) {
            count++;
            return cellStack.pop();
        }
        return null;
    }

    public int getUndoCount() {
        return count;
    }

    public boolean isEmpty() {
        return cellStack.isEmpty();
    }
}
