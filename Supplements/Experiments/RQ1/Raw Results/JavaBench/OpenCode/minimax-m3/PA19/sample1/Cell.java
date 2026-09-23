public abstract class Cell implements MapElement {

    public Coordinate coord;

    public Cell() {
    }

    public Cell(Coordinate coord) {
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

    static Wall createWall(Coordinate coord) {
        return new Wall(coord);
    }

    static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        return new FillableCell(coord, pipe);
    }

    static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }
}
