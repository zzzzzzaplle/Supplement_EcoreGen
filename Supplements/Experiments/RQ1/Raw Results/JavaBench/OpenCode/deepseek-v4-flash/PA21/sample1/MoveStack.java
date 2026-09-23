import java.util.Deque;
import java.util.ArrayDeque;

public class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {
        this.stack = new ArrayDeque<>();
        this.popCount = 0;
    }

    public int getPopCount() {
        return popCount;
    }

    public void setPopCount(int popCount) {
        this.popCount = popCount;
    }

    public Deque<MoveResult> getStack() {
        return stack;
    }

    public void setStack(Deque<MoveResult> stack) {
        this.stack = stack;
    }

    public void push(MoveResult move) {
        stack.addLast(move);
    }

    public MoveResult pop() {
        popCount++;
        return stack.pollLast();
    }

    public MoveResult peek() {
        return stack.peekLast();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
