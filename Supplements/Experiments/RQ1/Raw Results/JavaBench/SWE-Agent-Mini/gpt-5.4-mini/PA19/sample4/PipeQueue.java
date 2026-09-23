public class PipeQueue {
    private static int MAX_GEN_LENGTH = 5;
    private java.util.LinkedList<Pipe> pipeQueue;

    public PipeQueue(java.util.List<Pipe> pipes) {
        this.pipeQueue = new java.util.LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
    }

    public Pipe peek() {
        return pipeQueue.peekFirst();
    }

    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.removeFirst();
        }
    }

    public void undo(Pipe pipe) {
        if (pipe != null) {
            pipeQueue.addFirst(pipe);
        }
    }

    private static Pipe generateNewPipe() {
        return new Pipe();
    }

    public java.util.LinkedList<Pipe> getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(java.util.LinkedList<Pipe> pipeQueue) {
        this.pipeQueue = pipeQueue;
    }
}
