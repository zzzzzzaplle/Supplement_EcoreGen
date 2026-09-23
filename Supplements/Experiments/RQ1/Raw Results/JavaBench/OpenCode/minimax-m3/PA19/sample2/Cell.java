/**
 * Abstract base for any cell that can be rendered on the map.
 */
public abstract class Cell implements MapElement {

    public Coordinate coord;

    public Cell() {
        this.coord = new Coordinate();
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

    public static Cell fromChar(char c, Coordinate coord, TerminationType terminationType) {
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

    protected static Cell createWall(Coordinate coord) {
        return new Wall(coord);
    }

    protected static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell cell = new FillableCell(coord);
        cell.setPipe(pipe);
        return cell;
    }

    protected static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction dir) {
        return new TerminationCell(coord, type, dir);
    }
}
