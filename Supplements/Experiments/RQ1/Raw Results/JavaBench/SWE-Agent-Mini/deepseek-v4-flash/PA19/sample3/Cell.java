/**
 * Abstract base class for all cell types on the map.
 */
public abstract class Cell implements MapElement {

    public Coordinate coord;

    public Cell() {
    }

    public Cell(Coordinate coord) {
        this.coord = coord;
    }

    /**
     * Factory method to create a Cell from a character representation.
     */
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
            default:
                return null;
        }
    }

    private static Cell createWall(Coordinate coord) {
        return new Wall(coord);
    }

    private static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell fc = new FillableCell(coord);
        fc.setPipe(pipe);
        return fc;
    }

    private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }
}
