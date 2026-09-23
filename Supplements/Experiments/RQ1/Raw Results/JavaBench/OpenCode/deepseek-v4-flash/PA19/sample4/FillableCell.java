public class FillableCell extends Cell {

    private Pipe pipe;

    public FillableCell() {
    }

    public FillableCell(Coordinate coord) {
        super(coord);
    }

    public Pipe getPipe() {
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
