import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeQueue {

    private static final int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
        pipeQueue.poll();
        if (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.addLast(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        Random random = new Random();
        PipeShape[] shapes = PipeShape.values();
        PipeShape shape = shapes[random.nextInt(shapes.length)];
        return new Pipe(shape);
    }
}
