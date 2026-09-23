import java.util.Optional;

/**
 * A cell that can hold a pipe.
 */
public class FillableCell extends Cell {

    private Pipe pipe;

    public FillableCell() {
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    /**
     * Returns the pipe in this cell, if present.
     *
     * @return an Optional containing the pipe, or empty if none
     */
    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    /**
     * Sets the pipe in this cell.
     *
     * @param pipe the pipe to set
     */
    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    /**
     * Returns the character representation of this cell.
     *
     * @return pipe character if pipe exists, otherwise '.'
     */
    @Override
    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        }
        return '.';
    }
}
