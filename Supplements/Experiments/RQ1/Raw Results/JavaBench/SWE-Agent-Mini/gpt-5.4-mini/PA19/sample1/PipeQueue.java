import java.util.LinkedList;
import java.util.List;

public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
    }

    public PipeQueue(List<Pipe> pipes) {
        pipeQueue = new LinkedList<>(pipes);
    }

    public Pipe peek() {
        return pipeQueue.peek();
    }

    public void consume() {
        pipeQueue.poll();
    }

    public void undo(Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    private static Pipe generateNewPipe() { return new Pipe(); }

    public LinkedList<Pipe> getPipeQueue() { return pipeQueue; }
    public void setPipeQueue(LinkedList<Pipe> pipeQueue) { this.pipeQueue = pipeQueue; }
    public static int getMAX_GEN_LENGTH() { return MAX_GEN_LENGTH; }
    public static void setMAX_GEN_LENGTH(int mAX_GEN_LENGTH) { MAX_GEN_LENGTH = mAX_GEN_LENGTH; }
}
