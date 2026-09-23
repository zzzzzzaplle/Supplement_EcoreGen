import java.util.Deque;
import java.util.ArrayDeque;

/**
 * Stack for storing move results to support undo.
 */
public class MoveStack {

    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {
        this.popCount = 0;
        this.stack = new ArrayDeque<>();
    }

    public void push(final MoveResult move) {
        stack.push(move);
    }

    public MoveResult pop() {
        final MoveResult result = stack.poll();
        if (result != null) {
            popCount++;
        }
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

    public void setPopCount(final int popCount) {
        this.popCount = popCount;
    }

    public Deque<MoveResult> getStack() {
        return stack;
    }

    public void setStack(final Deque<MoveResult> stack) {
        this.stack = stack;
    }
}
