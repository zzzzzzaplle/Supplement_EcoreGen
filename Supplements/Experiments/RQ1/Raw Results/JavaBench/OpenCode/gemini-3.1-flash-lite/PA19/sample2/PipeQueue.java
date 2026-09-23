import java.util.LinkedList;
import java.util.List;

public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
    }

    public LinkedList<Pipe> getPipeQueue() { return pipeQueue; }
    public void setPipeQueue(LinkedList<Pipe> pipeQueue) { this.pipeQueue = pipeQueue; }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
        pipeQueue.poll();
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        return new Pipe(); // TODO
    }
}
