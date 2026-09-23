public abstract class Cell {
    public Coordinate coord;

    public Cell() {
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

    private static Cell createWall(Coordinate coord) {
        Wall wall = new Wall();
        wall.coord = coord;
        return wall;
    }

    private static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell cell = new FillableCell();
        cell.coord = coord;
        cell.setPipe(pipe);
        return cell;
    }

    private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        TerminationCell cell = new TerminationCell(coord, type, pointingTo);
        return cell;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }
}
