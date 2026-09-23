public abstract class Cell {
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

    private static Wall createWall(Coordinate coord) {
        Wall wall = new Wall();
        wall.setCoord(coord);
        return wall;
    }

    private static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell cell = new FillableCell();
        cell.setCoord(coord);
        cell.setPipe(pipe);
        return cell;
    }

    private static TerminationCell createTerminationCell(Coordinate coord, TerminationType terminationType, Direction pointingTo) {
        TerminationCell cell = new TerminationCell();
        cell.setCoord(coord);
        cell.setType(terminationType);
        cell.setPointingTo(pointingTo);
        return cell;
    }
}
