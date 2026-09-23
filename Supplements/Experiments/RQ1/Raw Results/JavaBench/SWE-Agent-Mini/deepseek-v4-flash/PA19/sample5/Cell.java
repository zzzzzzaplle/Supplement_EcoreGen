/**
 * Abstract base class for all cells on the map.
 */
public abstract class Cell implements MapElement {

    public Coordinate coord;

    public Cell() {
        this.coord = null;
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

    private static Cell createWall(Coordinate coord) {
        return new Wall(coord);
    }

    private static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell cell = new FillableCell(coord);
        cell.setPipe(pipe);
        return cell;
    }

    private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction dir) {
        return new TerminationCell(coord, type, dir);
    }

    /**
     * Creates a Cell from a character representation.
     *
     * @param c               the character
     * @param coord           the coordinate
     * @param terminationType the termination type (used for direction cells)
     * @return the created Cell, or null if character is unknown
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
}
