import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * A stack for storing valid move results for undo functionality.
 */
public class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack;

    /**
     * Creates a new MoveStack.
     */
    public MoveStack() {
        this.popCount = 0;
        this.stack = new ArrayDeque<>();
    }

    /**
     * Returns the number of times pop has been called.
     *
     * @return The pop count.
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
     * Returns the underlying deque.
     *
     * @return The deque.
     */
    public Deque<MoveResult> getStack() {
        return stack;
    }

    /**
     * Sets the underlying deque.
     *
     * @param stack The deque.
     */
    public void setStack(Deque<MoveResult> stack) {
        this.stack = Objects.requireNonNull(stack);
    }

    /**
     * Pushes a move result onto the stack.
     *
     * @param move The move result to push.
     */
    public void push(MoveResult move) {
        stack.push(Objects.requireNonNull(move));
    }

    /**
     * Pops a move result from the stack.
     *
     * @return The popped move result.
     */
    public MoveResult pop() {
        if (stack.isEmpty()) {
            return null;
        }
        popCount++;
        return stack.pop();
    }

    /**
     * Returns the top move result without removing it.
     *
     * @return The top move result, or null if the stack is empty.
     */
    public MoveResult peek() {
        return stack.isEmpty() ? null : stack.peek();
    }

    /**
     * Checks if the stack is empty.
     *
     * @return True if the stack is empty, false otherwise.
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
