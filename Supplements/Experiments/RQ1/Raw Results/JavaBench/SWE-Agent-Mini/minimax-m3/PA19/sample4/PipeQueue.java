import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Pipe queue used to deliver pipes to the player.
 */
public class PipeQueue {

    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;
    private Random random;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        this.random = new Random();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        this.random = new Random();
        if (pipes != null) {
            for (Pipe p : pipes) {
                pipeQueue.add(p);
            }
        }
        ensureFull();
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            ensureFull();
        }
        return pipeQueue.peek();
    }

    public void consume() {
        if (pipeQueue.isEmpty()) {
            ensureFull();
        }
        pipeQueue.poll();
        ensureFull();
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
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

    private void ensureFull() {
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        Random rand = new Random();
        PipeShape s = shapes[rand.nextInt(shapes.length)];
        return new Pipe(s);
    }
}
