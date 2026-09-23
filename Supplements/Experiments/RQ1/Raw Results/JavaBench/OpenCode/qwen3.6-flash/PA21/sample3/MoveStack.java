public class MoveStack {

    private int popCount;
    private java.util.Deque<MoveResult> stack;

    public MoveStack() {
        this.stack = new java.util.ArrayDeque<>();
        this.popCount = 0;
    }

    public int getPopCount() {
        return popCount;
    }

    public int size() {
        return stack.size();
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
