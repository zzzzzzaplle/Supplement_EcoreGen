import java.util.ArrayDeque;
import java.util.Deque;

public class MoveStack {
    private int popCount;
    private final Deque<MoveResult> stack;

    public MoveStack() {
        this.stack = new ArrayDeque<>();
        this.popCount = 0;
    }

    public void push(final MoveResult move) {
        stack.push(move);
    }

    public MoveResult pop() {
        popCount++;
        return stack.pop();
    }

    public MoveResult peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int getPopCount() {
        return popCount;
    }
}
