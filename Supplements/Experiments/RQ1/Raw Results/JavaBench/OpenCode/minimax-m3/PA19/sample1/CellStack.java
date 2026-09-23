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
        FillableCell cell = cellStack.pop();
        count++;
        return cell;
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
