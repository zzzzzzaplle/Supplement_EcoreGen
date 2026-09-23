public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private java.util.LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
    }

    public PipeQueue(java.util.List<Pipe> pipes) {
        this.pipeQueue = new java.util.LinkedList<>(pipes);
    }

    public java.util.LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(java.util.LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
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

    private static Pipe generateNewPipe() {
        return new Pipe(PipeShape.HORIZONTAL);
    }
}
