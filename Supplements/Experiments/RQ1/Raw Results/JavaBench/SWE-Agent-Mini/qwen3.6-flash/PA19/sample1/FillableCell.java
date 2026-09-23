import java.util.Optional;

/**
 * A fillable cell that can hold an optional pipe.
 */
public class FillableCell extends Cell {

    private Pipe pipe;

    public FillableCell() {
        super();
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public Pipe getPipeRaw() {
        return pipe;
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
