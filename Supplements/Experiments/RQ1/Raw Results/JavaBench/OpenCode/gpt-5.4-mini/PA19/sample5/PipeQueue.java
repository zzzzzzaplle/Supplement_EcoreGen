import java.util.LinkedList;
import java.util.List;

public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
    }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.removeFirst();
        }
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() {
        return new Pipe();
    }

    public static int getMAX_GEN_LENGTH() {
        return MAX_GEN_LENGTH;
    }

    public static void setMAX_GEN_LENGTH(int MAX_GEN_LENGTH) {
        PipeQueue.MAX_GEN_LENGTH = MAX_GEN_LENGTH;
    }

    public LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }
}
