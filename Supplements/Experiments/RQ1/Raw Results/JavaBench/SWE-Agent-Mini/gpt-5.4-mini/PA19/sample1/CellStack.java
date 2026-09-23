import java.util.Stack;

public class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        cellStack = new Stack<>();
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
        count++;
    }

    public FillableCell pop() {
        count--;
        return cellStack.pop();
    }

    public int getUndoCount() {
        return count;
    }

    public Stack<FillableCell> getCellStack() { return cellStack; }
    public void setCellStack(Stack<FillableCell> cellStack) { this.cellStack = cellStack; }
    public void setCount(int count) { this.count = count; }
}
