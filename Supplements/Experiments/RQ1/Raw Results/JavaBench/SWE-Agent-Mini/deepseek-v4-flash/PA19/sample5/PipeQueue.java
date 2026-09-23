import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A queue of pipes that auto-refills to a fixed length.
 */
public class PipeQueue {

    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            pipeQueue.addAll(pipes);
        }
        // ensure the queue is full
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Peeks at the head pipe without removing it.
     *
     * @return the head pipe
     */
    public Pipe peek() {
        return pipeQueue.peek();
    }

    /**
     * Consumes (removes) the head pipe from the queue and auto-refills.
     */
    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.poll();
        }
        // auto-refill to maintain queue length
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Inserts a pipe back to the front of the queue (for undo).
     *
     * @param pipe the pipe to insert at the front
     */
    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    /**
     * Generates a new random pipe.
     *
     * @return a randomly generated Pipe
     */
    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        Random random = new Random();
        PipeShape shape = shapes[random.nextInt(shapes.length)];
        return new Pipe(shape);
    }
}
