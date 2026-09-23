import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Queue of pipes available for placement, auto-refilled with random pipes.
 */
public class PipeQueue {

    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        refill();
    }

    public static int getMaxGenLength() {
        return MAX_GEN_LENGTH;
    }

    public static void setMaxGenLength(int maxGenLength) {
        MAX_GEN_LENGTH = maxGenLength;
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public Pipe peek() {
        ensureSize();
        if (pipeQueue.isEmpty()) {
            return null;
        }
        return pipeQueue.peek();
    }

    public void consume() {
        ensureSize();
        if (!pipeQueue.isEmpty()) {
            pipeQueue.poll();
        }
        refill();
    }

    public void undo(Pipe pipe) {
        if (pipe == null) {
            return;
        }
        pipeQueue.addFirst(pipe);
    }

    public boolean isEmpty() {
        return pipeQueue.isEmpty();
    }

    public int size() {
        return pipeQueue.size();
    }

    private void refill() {
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    private void ensureSize() {
        refill();
    }

    private static Pipe generateNewPipe() {
        PipeShape[] values = PipeShape.values();
        Random random = new Random();
        PipeShape shape = values[random.nextInt(values.length)];
        return new Pipe(shape);
    }
}
