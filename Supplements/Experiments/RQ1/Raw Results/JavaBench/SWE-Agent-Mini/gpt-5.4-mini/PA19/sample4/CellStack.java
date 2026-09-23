public class CellStack {
    private java.util.Stack<FillableCell> cellStack;
    private int count;

    public CellStack() {
        this.cellStack = new java.util.Stack<>();
    }

    public void push(FillableCell cell) {
        cellStack.push(cell);
        count++;
    }

    public FillableCell pop() {
        if (cellStack.isEmpty()) {
            return null;
        }
        return cellStack.pop();
    }

    public int getUndoCount() {
        return count;
    }

    public java.util.Stack<FillableCell> getCellStack() {
        return cellStack;
    }

    public void setCellStack(java.util.Stack<FillableCell> cellStack) {
        this.cellStack = cellStack;
    }
}
