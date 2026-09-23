import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
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
        return new Pipe(shapes[rand.nextInt(shapes.length)]);
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
