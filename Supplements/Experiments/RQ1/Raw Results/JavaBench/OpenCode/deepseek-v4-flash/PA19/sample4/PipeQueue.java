import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeQueue {

    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            Pipe p = generateNewPipe();
            pipeQueue.addLast(p);
        }
        return pipeQueue.peekFirst();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.pollFirst();
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.addLast(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        Random rand = new Random();
        PipeShape[] shapes = PipeShape.values();
        PipeShape shape = shapes[rand.nextInt(shapes.length)];
        return new Pipe(shape);
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }
}
