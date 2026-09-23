public class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
    }

    public java.util.Optional<Pipe> getPipe() {
        return java.util.Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public char toSingleChar() {
        return pipe == null ? '.' : pipe.toSingleChar();
    }
}
