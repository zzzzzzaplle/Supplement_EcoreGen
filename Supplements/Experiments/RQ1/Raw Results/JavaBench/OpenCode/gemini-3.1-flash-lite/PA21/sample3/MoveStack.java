import java.util.Deque;

public class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack;

    public MoveStack() {}

    public int getPopCount() { return popCount; }
    public void setPopCount(int popCount) { this.popCount = popCount; }
    public Deque<MoveResult> getStack() { return stack; }
    public void setStack(Deque<MoveResult> stack) { this.stack = stack; }

    public void push(MoveResult move) {}
    public MoveResult pop() { return null; }
    public MoveResult peek() { return null; }
    public boolean isEmpty() { return true; }
}
