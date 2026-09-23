public abstract class Cell implements MapElement {

    public Coordinate coord;

    public Cell() {
    }

    public Cell(Coordinate coord) {
        this.coord = coord;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        switch (c) {
            case 'W':
                return createWall(coord);
            case '.':
                return createFillableCell(coord, null);
            case '^':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.UP);
            case '>':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.RIGHT);
            case '<':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.LEFT);
            case 'v':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.DOWN);
            case 'H':
                return createFillableCell(coord, new Pipe(PipeShape.HORIZONTAL));
            case 'V':
                return createFillableCell(coord, new Pipe(PipeShape.VERTICAL));
            case 'L':
                return createFillableCell(coord, new Pipe(PipeShape.TOP_LEFT));
            case 'J':
                return createFillableCell(coord, new Pipe(PipeShape.TOP_RIGHT));
            case 'M':
                return createFillableCell(coord, new Pipe(PipeShape.BOTTOM_LEFT));
            case 'N':
                return createFillableCell(coord, new Pipe(PipeShape.BOTTOM_RIGHT));
            case 'C':
                return createFillableCell(coord, new Pipe(PipeShape.CROSS));
            default:
                return null;
        }
    }

    private static Cell createWall(Coordinate coord) {
        return new Wall(coord);
    }

    private static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        return new FillableCell(coord, pipe);
    }

    private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }
}
