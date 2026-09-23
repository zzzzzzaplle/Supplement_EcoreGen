import java.util.ArrayDeque;
import java.util.Deque;

public class MoveStack {
    private int popCount;
    private final Deque<MoveResult> stack;

    public MoveStack() {
        this.stack = new ArrayDeque<>();
        this.popCount = 0;
    }

    public void push(MoveResult move) {
        if (move instanceof Alive) {
            stack.push(move);
        }
    }

    public MoveResult pop() {
        if (!isEmpty()) {
            popCount++;
            return stack.pop();
        }
        return null;
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int getUndoCount() {
        return popCount;
    }

    public int getPopCount() {
        return popCount;
    }
}
