import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Pipe queue, auto-refills to MAX_GEN_LENGTH and supports undoing by re-inserting.
 */
public class PipeQueue {

    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        for (int i = 0; i < MAX_GEN_LENGTH; ++i) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.poll();
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        Random random = new Random();
        PipeShape shape = shapes[random.nextInt(shapes.length)];
        return new Pipe(shape);
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public static int getMaxGenLength() {
        return MAX_GEN_LENGTH;
    }
}
