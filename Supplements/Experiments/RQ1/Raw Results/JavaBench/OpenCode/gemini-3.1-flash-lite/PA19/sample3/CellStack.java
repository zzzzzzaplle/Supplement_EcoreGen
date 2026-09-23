import java.util.Stack;

public class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new Stack<>();
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }
    public FillableCell pop() {
        return cellStack.pop();
    }
    public int getUndoCount() { return count; }
    public void setUndoCount(int count) { this.count = count; }
}
