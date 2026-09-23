public abstract class Cell implements MapElement {
  public Coordinate coord;

  public Cell() {}
  public Coordinate getCoord() { return coord; }
  public void setCoord(Coordinate coord) { this.coord = coord; }

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

  // Factory helpers, assuming these classes exist or will be created
  private static Cell createWall(Coordinate coord) {
      Wall w = new Wall();
      w.setCoord(coord);
      return w;
  }
  private static Cell createFillableCell(Coordinate coord, Pipe pipe) {
      FillableCell fc = new FillableCell();
      fc.setCoord(coord);
      fc.setPipe(pipe);
      return fc;
  }
  private static Cell createTerminationCell(Coordinate coord, TerminationType type, Direction dir) {
      return new TerminationCell(coord, type, dir);
  }
}
