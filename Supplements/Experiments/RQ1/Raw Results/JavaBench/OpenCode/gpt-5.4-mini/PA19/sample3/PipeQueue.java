import java.util.LinkedList;
import java.util.List;

public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
    }

    public PipeQueue(List<Pipe> pipes) {
        pipeQueue = new LinkedList<>();
        if (pipes != null) {
            pipeQueue.addAll(pipes);
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public Pipe peek() {
        return pipeQueue == null || pipeQueue.isEmpty() ? null : pipeQueue.peek();
    }

    public void consume() {
        if (pipeQueue != null && !pipeQueue.isEmpty()) {
            pipeQueue.poll();
            while (pipeQueue.size() < MAX_GEN_LENGTH) {
                pipeQueue.add(generateNewPipe());
            }
        }
    }

    public void undo(Pipe pipe) {
        if (pipeQueue == null) {
            pipeQueue = new LinkedList<>();
        }
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        return new Pipe();
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public static int getMAX_GEN_LENGTH() {
        return MAX_GEN_LENGTH;
    }

    public static void setMAX_GEN_LENGTH(int MAX_GEN_LENGTH) {
        PipeQueue.MAX_GEN_LENGTH = MAX_GEN_LENGTH;
    }
}
