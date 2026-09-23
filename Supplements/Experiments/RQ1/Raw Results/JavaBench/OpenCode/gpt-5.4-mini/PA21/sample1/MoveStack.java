public class MoveStack {
    private int popCount;
    private java.util.Deque<MoveResult> stack;

    public MoveStack() {
        this.stack = new java.util.ArrayDeque<>();
    }

    public int getPopCount() {
        return popCount;
    }

    public void setPopCount(int popCount) {
        this.popCount = popCount;
    }

    public java.util.Deque<MoveResult> getStack() {
        return stack;
    }

    public void setStack(java.util.Deque<MoveResult> stack) {
        this.stack = stack;
    }

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
