import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
        refill();
    }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.poll();
        }
        refill();
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private void refill() {
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.addLast(generateNewPipe());
        }
    }

    private static Pipe generateNewPipe() {
        Random random = new Random();
        PipeShape[] shapes = PipeShape.values();
        PipeShape shape = shapes[random.nextInt(shapes.length)];
        return new Pipe(shape);
    }
}
