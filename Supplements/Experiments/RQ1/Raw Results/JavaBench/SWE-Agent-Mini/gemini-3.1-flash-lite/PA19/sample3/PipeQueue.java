import java.util.*;
public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private LinkedList<Pipe> pipeQueue = new LinkedList<>();

    public PipeQueue() {}
    public PipeQueue(List<Pipe> pipes) {
        pipeQueue.addAll(pipes);
    }

    public Pipe peek() { return pipeQueue.peek(); }
    public void consume() { pipeQueue.poll(); if(pipeQueue.size() < MAX_GEN_LENGTH) pipeQueue.add(generateNewPipe()); }
    public void undo(Pipe pipe) { pipeQueue.addFirst(pipe); }
    private static Pipe generateNewPipe() {
        PipeShape[] shapes = PipeShape.values();
        return new Pipe(shapes[new Random().nextInt(shapes.length)], false);
    }
    public LinkedList<Pipe> getPipeQueue() { return pipeQueue; }
    public void setPipeQueue(LinkedList<Pipe> pipeQueue) { this.pipeQueue = pipeQueue; }
}
