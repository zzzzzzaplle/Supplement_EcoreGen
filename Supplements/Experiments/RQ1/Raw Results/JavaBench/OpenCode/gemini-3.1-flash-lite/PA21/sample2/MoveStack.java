import java.util.Deque;
import java.util.LinkedList;
public class MoveStack {
    private int popCount;
    private Deque<MoveResult> stack = new LinkedList<>();
    public MoveStack() {}
    public int getPopCount() { return popCount; }
    public void setPopCount(int popCount) { this.popCount = popCount; }
    public Deque<MoveResult> getStack() { return stack; }
    public void setStack(Deque<MoveResult> stack) { this.stack = stack; }
    public void push(MoveResult move) { stack.push(move); }
    public MoveResult pop() { return stack.pop(); }
    public MoveResult peek() { return stack.peek(); }
    public boolean isEmpty() { return stack.isEmpty(); }
}
