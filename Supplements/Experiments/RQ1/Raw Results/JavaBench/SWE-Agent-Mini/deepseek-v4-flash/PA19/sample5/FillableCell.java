import java.util.Optional;

/**
 * A cell that can hold a pipe.
 */
public class FillableCell extends Cell {

    private Pipe pipe;

    public FillableCell() {
        super();
        this.pipe = null;
    }

    public FillableCell(Coordinate coord) {
        super(coord);
        this.pipe = null;
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    /**
     * Returns the pipe contained in this cell, if any.
     *
     * @return an Optional containing the pipe, or empty if no pipe is placed
     */
    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    /**
     * Sets the pipe in this cell.
     *
     * @param pipe the pipe to place, or null to clear
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
