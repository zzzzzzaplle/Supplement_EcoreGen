import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * A stack for storing move history.
 */
public class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack = new ArrayDeque<>();

    /**
     * Creates a new MoveStack.
     */
    public MoveStack() {
        this.popCount = 0;
        this.stack = new ArrayDeque<>();
    }

    /**
     * Pushes a move result onto the stack.
     *
     * @param move The move result to push.
     */
    public void push(MoveResult move) {
        Objects.requireNonNull(move);
        stack.push(move);
    }

    /**
     * Pops a move result from the stack.
     *
     * @return The popped move result, or null if stack is empty.
     */
    public MoveResult pop() {
        MoveResult result = stack.pop();
        popCount++;
        return result;
    }

    /**
     * Peeks at the top of the stack without removing it.
     *
     * @return The top move result, or null if stack is empty.
     */
    public MoveResult peek() {
        return stack.peek();
    }

    /**
     * Checks if the stack is empty.
     *
     * @return true if the stack is empty.
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Gets the pop count.
     *
     * @return The number of times pop has been called.
     */
    public int getPopCount() {
        return popCount;
    }

    /**
     * Sets the pop count.
     *
     * @param popCount The pop count.
     */
    public void setPopCount(int popCount) {
        this.popCount = popCount;
    }

    /**
     * Gets the stack.
     *
     * @return The stack.
     */
    public Deque<MoveResult> getStack() {
        return stack;
    }

    /**
     * Sets the stack.
     *
     * @param stack The stack.
     */
    public void setStack(Deque<MoveResult> stack) {
        this.stack = stack;
    }
}
