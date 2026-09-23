import java.util.Optional;

public class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
        super();
    }

    public FillableCell(Coordinate coord) {
        super(coord);
    }

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        }
        return '.';
    }
}
