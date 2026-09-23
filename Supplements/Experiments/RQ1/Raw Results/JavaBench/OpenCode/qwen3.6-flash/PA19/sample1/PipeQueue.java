import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Queue of available pipes for the player.
 */
public class PipeQueue {

    public static final int MAX_GEN_LENGTH = 5;
    private  LinkedList<Pipe> pipeQueue;
    private static final Random random = new Random();
    private static final PipeShape[] ALL_SHAPES = PipeShape.values();

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        ensureQueueSize();
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            return generateNewPipe();
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.removeFirst();
        }
        ensureQueueSize();
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private void ensureQueueSize() {
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.addLast(generateNewPipe());
        }
    }

    /**
     * Generates a new random pipe.
     */
    private static Pipe generateNewPipe() {
        PipeShape shape = ALL_SHAPES[random.nextInt(ALL_SHAPES.length)];
        return new Pipe(shape, false);
    }
}
