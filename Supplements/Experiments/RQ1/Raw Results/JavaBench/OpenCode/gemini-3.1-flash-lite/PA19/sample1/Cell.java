public abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {}

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

    private static Wall createWall(Coordinate coord) {
        Wall w = new Wall();
        w.coord = coord;
        return w;
    }

    private static FillableCell createFillableCell(Coordinate coord, Pipe p) {
        FillableCell fc = new FillableCell();
        fc.coord = coord;
        fc.setPipe(p);
        return fc;
    }

    private static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction dir) {
        return new TerminationCell(coord, type, dir);
    }
}
