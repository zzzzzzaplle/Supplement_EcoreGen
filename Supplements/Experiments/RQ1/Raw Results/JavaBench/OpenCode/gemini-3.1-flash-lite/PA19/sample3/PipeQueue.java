import java.util.*;

public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {}
    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
    }

    public Pipe peek() { return pipeQueue.peek(); }
    public void consume() { pipeQueue.poll(); }
    public void undo(Pipe pipe) { pipeQueue.addFirst(pipe); }
    private static Pipe generateNewPipe() { return new Pipe(); }
}
