import java.util.Optional;

/**
 * Cell that can hold a pipe.
 */
public class FillableCell extends Cell {

    private Pipe pipe;

    public FillableCell() {
        super();
        this.pipe = null;
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

    @Override
    public char toSingleChar() {
        if (pipe == null) {
            return '.';
        }
        return pipe.toSingleChar();
    }
}
