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

    public Pipe getRawPipe() {
        return pipe;
    }

    public void setRawPipe(Pipe pipe) {
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
