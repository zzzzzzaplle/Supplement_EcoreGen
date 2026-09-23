import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * A stack for tracking move history for undo functionality.
 */
public class MoveStack {

    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {
        this.popCount = 0;
        this.stack = new ArrayDeque<>();
    }

    /**
     * Gets the number of items popped from the stack.
     *
     * @return the pop count.
     */
    public int getPopCount() {
        return popCount;
    }

    /**
     * Sets the pop count.
     *
     * @param popCount the pop count.
     */
    public void setPopCount(int popCount) {
        this.popCount = popCount;
    }

    /**
     * Gets the stack.
     *
     * @return the stack.
     */
    public Deque<MoveResult> getStack() {
        return stack;
    }

    /**
     * Sets the stack.
     *
     * @param stack the stack.
     */
    public void setStack(Deque<MoveResult> stack) {
        this.stack = Objects.requireNonNull(stack);
    }

    /**
     * Pushes a move result onto the stack.
     *
     * @param move the move result to push.
     */
    public void push(MoveResult move) {
        stack.push(Objects.requireNonNull(move));
    }

    /**
     * Pops the top move result from the stack.
     *
     * @return the popped move result, or null if the stack is empty.
     */
    public MoveResult pop() {
        MoveResult result = stack.pop();
        popCount++;
        return result;
    }

    /**
     * Peeks at the top move result without removing it.
     *
     * @return the top move result, or null if the stack is empty.
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
}
