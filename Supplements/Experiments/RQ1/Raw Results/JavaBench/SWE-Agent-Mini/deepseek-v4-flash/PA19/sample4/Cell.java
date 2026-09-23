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
            default:
                return null;
        }
    }

    public static Cell createWall(Coordinate coord) {
        Wall wall = new Wall();
        wall.coord = coord;
        return wall;
    }

    public static Cell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell fc = new FillableCell();
        fc.coord = coord;
        if (pipe != null) {
            fc.setPipe(pipe);
        }
        return fc;
    }

    public static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }
}
