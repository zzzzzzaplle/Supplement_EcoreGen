import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A queue of pipes for the player to place.
 */
public class PipeQueue {

    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            refillQueue();
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (pipeQueue.isEmpty()) {
            refillQueue();
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
        Random rand = new Random();
        PipeShape[] shapes = PipeShape.values();
        PipeShape shape = shapes[rand.nextInt(shapes.length)];
        return new Pipe(shape);
    }

    private void refillQueue() {
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public static int getMaxGenLength() {
        return MAX_GEN_LENGTH;
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }
}
