import java.util.Stack;

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
        if (!cellStack.isEmpty()) {
            count++;
            return cellStack.pop();
        }
        return null;
    }

    public int getUndoCount() {
        return count;
    }

    public int size() {
        return cellStack.size();
    }

    public boolean isEmpty() {
        return cellStack.isEmpty();
    }
}
