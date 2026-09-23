import java.util.Stack;

public class CellStack {
    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
    }

    public CellStack(Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
        count++;
    }

    public FillableCell pop() {
        return cellStack.pop();
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
