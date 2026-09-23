import java.util.Optional;

public class FillableCell extends Cell {
    private Pipe pipe;
    public FillableCell() {
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        }
        return '.';
    }

    public boolean hasPipe() {
        return pipe != null;
    }

    public void clearPipe() {
        this.pipe = null;
    }
}
