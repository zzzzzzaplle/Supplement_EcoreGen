import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represents a queue of pipes for the player to place.
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
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.addLast(generateNewPipe());
        }
    }

    /**
     * Peeks at the head pipe without removing it.
     *
     * @return the head pipe
     */
    public Pipe peek() {
        return pipeQueue.peekFirst();
    }

    /**
     * Consumes (removes) the head pipe and refills if needed.
     */
    public void consume() {
        pipeQueue.pollFirst();
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.addLast(generateNewPipe());
        }
    }

    /**
     * Inserts a pipe back to the front of the queue.
     *
     * @param pipe the pipe to insert
     */
    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    /**
     * Generates a new random pipe.
     *
     * @return a new Pipe with a random shape
     */
    private static Pipe generateNewPipe() {
        Random rand = new Random();
        PipeShape[] shapes = PipeShape.values();
        return new Pipe(shapes[rand.nextInt(shapes.length)]);
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }
}
