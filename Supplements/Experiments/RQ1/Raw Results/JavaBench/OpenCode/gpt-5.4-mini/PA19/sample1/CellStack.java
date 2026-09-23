import java.util.LinkedList;
import java.util.List;
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
        count++;
    }

    public FillableCell pop() {
        if (cellStack.isEmpty()) {
            return null;
        }
        count--;
        return cellStack.pop();
    }

    public int getUndoCount() {
        return count;
    }
}
