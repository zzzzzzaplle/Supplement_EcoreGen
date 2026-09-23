import java.util.Optional;

/**
 * A Cell that can hold an optional pipe.
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

    public Pipe getPipeField() {
        return pipe;
    }

    public void setPipeField(Pipe pipe) {
        this.pipe = pipe;
    }

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public void clearPipe() {
        this.pipe = null;
    }

    public boolean hasPipe() {
        return pipe != null;
    }

    public char toSingleChar() {
        if (pipe == null) {
            return '.';
        }
        return pipe.toSingleChar();
    }
}
