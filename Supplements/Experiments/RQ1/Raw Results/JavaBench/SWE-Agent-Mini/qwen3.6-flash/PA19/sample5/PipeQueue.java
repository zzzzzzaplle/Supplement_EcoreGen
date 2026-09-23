import java.util.*;

/**
 * Queue of pipes available for the player to place.
 */
public class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;
    private Random random;

    public PipeQueue() {
        this(new ArrayList<>());
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        this.random = new Random();

        if (pipes != null && !pipes.isEmpty()) {
            this.pipeQueue.addAll(pipes);
        }
        // Ensure we have enough pipes
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Returns the next pipe without removing it.
     */
    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            return generateNewPipe();
        }
        return pipeQueue.peekFirst();
    }

    /**
     * Removes and returns the next pipe from the queue.
     */
    public Pipe consume() {
        Pipe next = peek();
        pipeQueue.removeFirst();
        // Auto-refill
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
        return next;
    }

    /**
     * Inserts a pipe back to the front of the queue.
     */
    public void undo(Pipe pipe) {
        if (pipe != null) {
            pipeQueue.addFirst(pipe);
        }
    }

    /**
     * Generates a new pipe with a random shape.
     */
    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        PipeShape shape = shapes[new Random().nextInt(shapes.length)];
        return new Pipe(shape);
    }

    public boolean isEmpty() {
        return pipeQueue.isEmpty();
    }
}
