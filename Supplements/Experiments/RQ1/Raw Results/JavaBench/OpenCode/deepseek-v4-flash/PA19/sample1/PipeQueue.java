import java.util.LinkedList;
import java.util.List;

public class PipeQueue {

    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            pipeQueue.addLast(generateNewPipe());
        }
        return pipeQueue.peekFirst();
    }

    public void consume() {
        pipeQueue.pollFirst();
        if (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.addLast(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        PipeShape shape = shapes[(int) (Math.random() * shapes.length)];
        return new Pipe(shape);
    }
}
