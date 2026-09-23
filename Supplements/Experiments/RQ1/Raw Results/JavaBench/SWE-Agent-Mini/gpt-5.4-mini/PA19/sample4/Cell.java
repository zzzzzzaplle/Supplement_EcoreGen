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

    protected static Wall createWall(Coordinate coord) {
        Wall wall = new Wall();
        wall.setCoord(coord);
        return wall;
    }

    protected static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell cell = new FillableCell();
        cell.setCoord(coord);
        cell.setPipe(pipe);
        return cell;
    }

    protected static TerminationCell createTerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        return new TerminationCell(coord, type, pointingTo);
    }
}
