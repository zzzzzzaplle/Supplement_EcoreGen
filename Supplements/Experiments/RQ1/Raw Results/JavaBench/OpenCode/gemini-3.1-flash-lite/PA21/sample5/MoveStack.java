import java.util.*;

public class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack = new ArrayDeque<>();

    public MoveStack() {}

    public void push(MoveResult move) {
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
}
