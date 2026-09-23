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
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            pipeQueue.addAll(pipes);
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Returns the head pipe without removing it.
     *
     * @return the head pipe
     */
    public Pipe peek() {
        return pipeQueue.peek();
    }

    /**
     * Consumes the head pipe (removes it) and refills if needed.
     */
    public void consume() {
        pipeQueue.poll();
        if (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Inserts a pipe back to the front of the queue.
     *
     * @param pipe the pipe to restore
     */
    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    /**
     * Generates a new random pipe.
     *
     * @return a new random Pipe
     */
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
