import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Queue of upcoming pipes the player can place. The queue is refilled up to MAX_GEN_LENGTH
 * with randomly generated pipes whenever it runs low.
 */
public class PipeQueue {

    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
        }
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            pipeQueue.add(generateNewPipe());
        }
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
        if (pipe == null) {
            return;
        }
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        PipeShape shape = shapes[new Random().nextInt(shapes.length)];
        return new Pipe(shape);
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
}
