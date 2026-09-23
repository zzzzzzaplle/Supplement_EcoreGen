import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeQueue {
    public static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;
    private static final Random random = new Random();
    private static final PipeShape[] allShapes = PipeShape.values();

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        refillQueue();
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            refillQueue();
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.poll();
            refillQueue();
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private void refillQueue() {
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public static Pipe generateNewPipe() {
        PipeShape shape = allShapes[random.nextInt(allShapes.length)];
        return new Pipe(shape, false);
    }

    public int size() {
        return pipeQueue.size();
    }

    public boolean isEmpty() {
        return pipeQueue.isEmpty();
    }
}
