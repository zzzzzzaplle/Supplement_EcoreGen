import java.util.Stack;

public class CellStack {

    private Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
    }

    public CellStack(Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }

    public Stack<FillableCell> getCellStack() {
        return cellStack;
    }

    public void setCellStack(Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
    }

    public FillableCell pop() {
        return cellStack.pop();
    }

    public int getUndoCount() {
        return count;
    }

    public void setUndoCount(int count) {
        this.count = count;
    }
}
