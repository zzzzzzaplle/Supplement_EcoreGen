/**
 * Abstract base class for all cell types on the map.
 */
public abstract class Cell implements MapElement {

    public Coordinate coord;

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    protected Cell(Coordinate coord) {
        this.coord = coord;
    }

    public Cell() {
        this(null);
    }

    public abstract static class CellFactory {
        protected static Cell createWall(Coordinate coord) {
            return new Wall(coord);
        }

        protected static Cell createFillableCell(Coordinate coord, Pipe pipe) {
            return new FillableCell(coord, pipe);
        }

        protected static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction direction) {
            return new TerminationCell(coord, type, direction);
        }
    }

    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        switch (c) {
            case 'W':
                return CellFactory.createWall(coord);
            case '.':
                return CellFactory.createFillableCell(coord, null);
            case '^':
                return terminationType == null ? null : CellFactory.createTerminationCell(coord, terminationType, Direction.UP);
            case '>':
                return terminationType == null ? null : CellFactory.createTerminationCell(coord, terminationType, Direction.RIGHT);
            case '<':
                return terminationType == null ? null : CellFactory.createTerminationCell(coord, terminationType, Direction.LEFT);
            case 'v':
                return terminationType == null ? null : CellFactory.createTerminationCell(coord, terminationType, Direction.DOWN);
            default:
                return null;
        }
    }
}
