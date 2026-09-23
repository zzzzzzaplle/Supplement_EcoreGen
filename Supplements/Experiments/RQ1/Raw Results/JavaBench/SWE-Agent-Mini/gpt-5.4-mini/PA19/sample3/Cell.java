public abstract class Cell implements MapElement {
    public Coordinate coord;

    public Cell() {
    }

    public static Cell fromChar(final char c, final Coordinate coord, final TerminationType terminationType)
  {
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

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    private static Cell createWall(Coordinate coord) { return new Wall(coord); }
    private static Cell createFillableCell(Coordinate coord, Pipe pipe) { return new FillableCell(coord, pipe); }
    private static Cell createTerminationCell(Coordinate coord, TerminationType terminationType, Direction direction) { return new TerminationCell(coord, terminationType, direction); }
}
