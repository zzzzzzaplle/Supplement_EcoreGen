import java.util.Optional;

public class FillableCell extends Cell {

    private Pipe pipe;

    public FillableCell(Coordinate coord) {
        super.coord = coord;
        this.pipe = null;
    }

    public FillableCell() {}

    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(this.pipe);
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

    public Pipe getPipeField() {
        return pipe;
    }

    public void setPipeField(Pipe pipe) {
        this.pipe = pipe;
    }
}
