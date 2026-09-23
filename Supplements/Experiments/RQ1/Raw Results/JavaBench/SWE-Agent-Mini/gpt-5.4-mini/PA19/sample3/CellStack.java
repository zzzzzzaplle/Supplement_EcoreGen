import java.util.Stack;

public class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
    }

    public void push(FillableCell cell) { }
    public FillableCell pop() { return null; }
    public int getUndoCount() { return count; }
    public Stack<FillableCell> getCellStack() { return cellStack; }
    public void setCellStack(Stack<FillableCell> cellStack) { this.cellStack = cellStack; }
    public void setCount(int count) { this.count = count; }
}
