import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue = new LinkedList<>();

    public PipeQueue() {}
    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue.addAll(pipes);
    }

    public Pipe peek() { return pipeQueue.isEmpty() ? null : pipeQueue.getFirst(); }
    public void consume() { if (!pipeQueue.isEmpty()) pipeQueue.removeFirst(); }
    public void undo(Pipe pipe) { pipeQueue.addFirst(pipe); }
    private static Pipe generateNewPipe() { return new Pipe(PipeShape.values()[new Random().nextInt(PipeShape.values().length)]); }

    public LinkedList<Pipe> getPipeQueue() { return pipeQueue; }
    public void setPipeQueue(LinkedList<Pipe> pipeQueue) { this.pipeQueue = pipeQueue; }
}
