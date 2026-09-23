import java.util.Optional;

public class FillableCell extends Cell implements MapElement {
    private Pipe pipe;

    public FillableCell() {
    }

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public char toSingleChar() {
        return pipe == null ? '.' : pipe.toSingleChar();
    }

    public Pipe getPipeValue() {
        return pipe;
    }

    public void setPipeValue(Pipe pipe) {
        this.pipe = pipe;
    }
}
