/**
 * Abstract base class for cells in the map grid.
 */
public abstract class Cell implements MapElement {

    public Coordinate coord;

    public Cell() {
        this.coord = new Coordinate(0, 0);
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
                return terminationType == null ? null : createTerminationCell(coord, coord.row, coord.col, terminationType, Direction.LEFT);
            case 'v':
                return terminationType == null ? null : createTerminationCell(coord, terminationType, Direction.DOWN);
            default:
                return null;
        }
    }

    protected static Wall createWall(Coordinate coord) {
        return new Wall(coord);
    }

    protected static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        return new FillableCell(coord, pipe);
    }

    protected static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }

    protected static TerminationCell createTerminationCell(Coordinate coord, int row, int col, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }
}
