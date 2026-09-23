import java.util.Optional;

/**
 * A fillable cell that can hold an optional pipe.
 */
public class FillableCell extends Cell {
    private Pipe pipe;

    public FillableCell() {
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        this.pipe = pipe;
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public Pipe getPipe() {
        return pipe;
    }

    @Override
    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        }
        return '.';
    }
}
