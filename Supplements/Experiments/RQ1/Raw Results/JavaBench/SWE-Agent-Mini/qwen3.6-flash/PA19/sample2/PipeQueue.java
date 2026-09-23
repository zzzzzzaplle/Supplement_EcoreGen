import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeQueue {

    public static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;
    private static final Random random = new Random();

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        while (pipeQueue.size() < PipeQueue.MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
        pipeQueue.remove();
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
        while (pipeQueue.size() > MAX_GEN_LENGTH) {
            pipeQueue.removeLast();
        }
    }

    public static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        return new Pipe(shapes[random.nextInt(shapes.length)]);
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public int getMaxGenLength() {
        return MAX_GEN_LENGTH;
    }

    public void setMaxGenLength(int maxGenLength) {
        PipeQueue.MAX_GEN_LENGTH = maxGenLength;
    }
}
