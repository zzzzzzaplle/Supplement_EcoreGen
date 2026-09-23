import java.util.Stack;

public class CellStack {
    private Stack<FillableCell> cellStack = new Stack<>();
    private int count;

    public CellStack() {}

    public void push(FillableCell cell) { cellStack.push(cell); }
    public FillableCell pop() { return cellStack.pop(); }
    public int getUndoCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public Stack<FillableCell> getCellStack() { return cellStack; }
    public void setCellStack(Stack<FillableCell> cellStack) { this.cellStack = cellStack; }
}
