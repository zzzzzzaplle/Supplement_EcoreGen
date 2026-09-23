import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    private final LinkedList<Pipe> pipeQueue;

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            for (Pipe pipe : pipes) {
                if (pipeQueue.size() < MAX_GEN_LENGTH) {
                    pipeQueue.add(pipe);
                }
            }
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            return null;
        }
        return pipeQueue.peekFirst();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.pollFirst();
        }
        if (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        Random random = new Random();
        PipeShape[] shapes = PipeShape.values();
        int index = random.nextInt(shapes.length);
        return new Pipe(shapes[index]);
    }

    public int size() {
        return pipeQueue.size();
    }
}
