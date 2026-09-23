import java.util.Optional;

/**
 * A fillable cell that can hold a pipe.
 */
public class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    @Override
    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    @Override
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

    public Pipe getPipeObject() {
        return pipe;
    }
}
