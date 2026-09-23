public class CellStack {
    private java.util.Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new java.util.Stack<>();
    }

    public CellStack(java.util.Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }

    public java.util.Stack<FillableCell> getCellStack() {
        return cellStack;
    }

    public void setCellStack(java.util.Stack<FillableCell> cellStack) {
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
}
