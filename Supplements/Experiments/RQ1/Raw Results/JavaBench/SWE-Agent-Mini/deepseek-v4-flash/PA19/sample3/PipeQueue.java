import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Queue of pipes for the player to place.
 */
public class PipeQueue {

    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        // Auto-refill to fixed length
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.addLast(generateNewPipe());
        }
    }

    /**
     * Peeks at the head pipe without removing it.
     */
    public Pipe peek() {
        return pipeQueue.peekFirst();
    }

    /**
     * Consumes the head pipe (removes it).
     */
    public void consume() {
        pipeQueue.pollFirst();
        // Auto-refill
        pipeQueue.addLast(generateNewPipe());
    }

    /**
     * Inserts a pipe back to the front of the queue (for undo).
     */
    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
        // Remove last if over max
        if (pipeQueue.size() > MAX_GEN_LENGTH) {
            pipeQueue.removeLast();
        }
    }

    /**
     * Generates a new random pipe.
     */
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
