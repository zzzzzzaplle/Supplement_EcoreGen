import java.util.Optional;

/**
 * A cell that can hold a pipe.
 */
public class FillableCell extends Cell {

    private Pipe pipe;

    public FillableCell() {
    }

    public FillableCell(Coordinate coord) {
        super(coord);
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    /**
     * Returns the pipe if present.
     */
    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    /**
     * Sets the pipe in this cell.
     */
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
