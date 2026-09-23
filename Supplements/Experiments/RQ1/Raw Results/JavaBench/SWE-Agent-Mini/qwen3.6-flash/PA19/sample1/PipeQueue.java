import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Queue of pipes for the player to place.
 */
public class PipeQueue {

    public static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;
    private static final Random random = new Random();
    private static final PipeShape[] allShapes = PipeShape.values();

    public PipeQueue() {
        pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        pipeQueue = new LinkedList<>();
        if (pipes != null) {
            pipeQueue.addAll(pipes);
        }
        // Auto-fill to MAX_GEN_LENGTH
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
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
            // Refill
            while (pipeQueue.size() < MAX_GEN_LENGTH) {
                pipeQueue.add(generateNewPipe());
            }
        }
    }

    public void undo(Pipe pipe) {
        if (pipe != null) {
            pipeQueue.addFirst(pipe);
            // Trim excess
            while (pipeQueue.size() > MAX_GEN_LENGTH) {
                pipeQueue.removeLast();
            }
        }
    }

    private static Pipe generateNewPipe() {
        PipeShape shape = allShapes[random.nextInt(allShapes.length)];
        return new Pipe(shape, false);
    }
}
