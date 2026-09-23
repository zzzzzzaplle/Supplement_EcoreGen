import java.util.Optional;

public abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {
    }

    public abstract char toSingleChar();

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
        Wall wall = new Wall();
        wall.coord = coord;
        return wall;
    }

    private static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell cell = new FillableCell();
        cell.coord = coord;
        if (pipe != null) {
            cell.pipe = pipe;
        }
        return cell;
    }

    private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        TerminationCell cell = new TerminationCell(coord, type, pointingTo);
        return cell;
    }
}
