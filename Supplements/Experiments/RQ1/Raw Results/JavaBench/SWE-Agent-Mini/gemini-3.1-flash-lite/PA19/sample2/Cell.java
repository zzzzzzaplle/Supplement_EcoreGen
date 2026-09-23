public abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {}
    public Cell(Coordinate coord) { this.coord = coord; }
    public Coordinate getCoord() { return coord; }
    public void setCoord(Coordinate coord) { this.coord = coord; }

    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
        switch (c) {
            case 'W':
                return new Wall(coord);
            case '.':
                return new FillableCell(coord);
            case '^':
                return terminationType == null ? null : new TerminationCell(coord, terminationType, Direction.UP);
            case '>':
                return terminationType == null ? null : new TerminationCell(coord, terminationType, Direction.RIGHT);
            case '<':
                return terminationType == null ? null : new TerminationCell(coord, terminationType, Direction.LEFT);
            case 'v':
                return terminationType == null ? null : new TerminationCell(coord, terminationType, Direction.DOWN);
            default:
                return null;
        }
    }
}
