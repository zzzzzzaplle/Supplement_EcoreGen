import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Queue holding the upcoming pipes the player can place.
 */
public class PipeQueue {

    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
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
        if (pipeQueue.isEmpty()) {
            pipeQueue.add(generateNewPipe());
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (pipeQueue.isEmpty()) {
            pipeQueue.add(generateNewPipe());
        }
        pipeQueue.poll();
        if (pipeQueue.size() < MAX_GEN_LENGTH) {
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
}
