import java.util.Deque;
import java.util.ArrayDeque;

public class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {
        this.popCount = 0;
        this.stack = new ArrayDeque<>();
    }

    public void push(MoveResult move) {
        stack.push(move);
    }

    public MoveResult pop() {
        MoveResult move = stack.poll();
        if (move != null) {
            popCount++;
        }
        return move;
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

    public void setPopCount(int popCount) {
        this.popCount = popCount;
    }

    public Deque<MoveResult> getStack() {
        return stack;
    }

    public void setStack(Deque<MoveResult> stack) {
        this.stack = stack;
    }
}
