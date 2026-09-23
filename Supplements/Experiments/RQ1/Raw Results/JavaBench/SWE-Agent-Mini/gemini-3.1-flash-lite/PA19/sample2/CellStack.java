import java.util.Stack;

public class CellStack {
    private Stack<FillableCell> cellStack = new Stack<>();
    private int count = 0;

    public CellStack() {}

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        if (cellStack.isEmpty()) return null;
        count++;
        return cellStack.pop();
    }

    public int getUndoCount() { return count; }
    public void setUndoCount(int count) { this.count = count; }
}
