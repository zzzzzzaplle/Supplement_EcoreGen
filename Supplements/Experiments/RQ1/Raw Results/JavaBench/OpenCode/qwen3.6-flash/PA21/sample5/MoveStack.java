import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {
        this.popCount = 0;
        this.stack = new ArrayDeque<>();
    }

    public void push(MoveResult move) {
        Objects.requireNonNull(move);
        stack.push(move);
    }

    public MoveResult pop() {
        MoveResult result = stack.pop();
        ++popCount;
        return result;
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
