import java.util.Optional;

public class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
    }

    public FillableCell(Coordinate coord) {
        super(coord);
    }

    public Pipe getPipeValue() {
        return pipe;
    }

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public char toSingleChar() {
        if (pipe == null) {
            return '.';
        }
        return pipe.toSingleChar();
    }

    public Pipe getPipeRaw() {
        return pipe;
    }

    public void setPipeValue(Pipe pipe) {
        this.pipe = pipe;
    }

    public Pipe getPipeForSetter() {
        return pipe;
    }
}
