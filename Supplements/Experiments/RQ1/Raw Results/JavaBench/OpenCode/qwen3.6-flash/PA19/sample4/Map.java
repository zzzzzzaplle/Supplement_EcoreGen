import java.util.HashSet;
import java.util.Set;

public class Map {
    private final int rows;
    private final int cols;
    public final Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private final Set<Coordinate> filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell tc) {
                    if (tc.getType() == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.getType() == TerminationType.SINK) {
                        this.sinkCell = tc;
                    }
                }
            }
        }
    }

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }
        return cells[row][col];
    }

    public void setCell(int row, int col, Cell cell) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            cells[row][col] = cell;
        }
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell fillableCell)) {
            return false;
        }
        if (fillableCell.hasPipe()) {
            return false;
        }
        fillableCell.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        Cell cell = getCell(coord.row, coord.col);
        if (cell instanceof FillableCell fillableCell) {
            fillableCell.clearPipe();
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }

        prevFilledTiles = 0;
        Set<Coordinate> newlyFilled = new HashSet<>();

        for (int d = 1; d <= distance; d++) {
            Set<Coordinate> currentLevel = new HashSet<>();
            if (d == 1) {
                currentLevel.add(sourceCell.coord);
            }

            for (Coordinate coord : filledTiles) {
                Cell cell = getCell(coord.row, coord.col);
                if (cell instanceof FillableCell fillableCell) {
                    fillableCell.getPipe().ifPresent(pipe -> {
                        if (pipe.getFilled()) {
                            for (Direction dir : pipe.getConnections()) {
                                Coordinate offset = dir.getOffset();
                                Coordinate neighborCoord = coord.add(offset);
                                Cell neighbor = getCell(neighborCoord.row, neighborCoord.col);
                                if (neighbor != null) {
                                    if (neighbor instanceof FillableCell neighborFillable) {
                                        neighborFillable.getPipe().ifPresent(neighborPipe -> {
                                            if (!neighborPipe.getFilled()) {
                                                Direction opposite = dir.getOpposite();
                                                for (Direction neighborDir : neighborPipe.getConnections()) {
                                                    if (neighborDir == opposite) {
                                                        neighborPipe.setFilled();
                                                        newlyFilled.add(neighborCoord);
                                                        break;
                                                    }
                                                }
                                            }
                                        });
                                    } else if (neighbor instanceof TerminationCell tc && tc.getType() == TerminationType.SINK) {
                                        if (!tc.getPointingTo().equals(dir.getOpposite())) {
                                            tc.setFilled();
                                            newlyFilled.add(neighborCoord);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }

            filledTiles.addAll(newlyFilled);
            prevFilledTiles = newlyFilled.size();
        }

        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        if (!sourceCell.getPointingTo().equals(Direction.UP) && !sourceCell.getPointingTo().equals(Direction.DOWN)
                && !sourceCell.getPointingTo().equals(Direction.LEFT) && !sourceCell.getPointingTo().equals(Direction.RIGHT)) {
            return false;
        }

        Set<Coordinate> visited = new HashSet<>();
        java.util.Queue<Coordinate> queue = new java.util.LinkedList<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = getCell(current.row, current.col);

            if (cell instanceof TerminationCell tc && tc.getType() == TerminationType.SINK && tc.getPointingTo() != null) {
                return true;
            }

            if (cell instanceof FillableCell fillableCell) {
                fillableCell.getPipe().ifPresent(pipe -> {
                    if (pipe.getFilled()) {
                        for (Direction dir : pipe.getConnections()) {
                            Coordinate neighborCoord = current.add(dir.getOffset());
                            if (!visited.contains(neighborCoord)) {
                                Cell neighbor = getCell(neighborCoord.row, neighborCoord.col);
                                if (neighbor != null) {
                                    if (neighbor instanceof FillableCell neighborFillable) {
                                        neighborFillable.getPipe().ifPresent(neighborPipe -> {
                                            if (neighborPipe.getFilled()) {
                                                Direction opposite = dir.getOpposite();
                                                for (Direction neighborDir : neighborPipe.getConnections()) {
                                                    if (neighborDir == opposite) {
                                                        visited.add(neighborCoord);
                                                        queue.add(neighborCoord);
                                                        break;
                                                    }
                                                }
                                            }
                                        });
                                    } else if (neighbor instanceof TerminationCell neighborTc && neighborTc.getType() == TerminationType.SINK) {
                                        if (neighborTc.getPointingTo() == dir.getOpposite()) {
                                            visited.add(neighborCoord);
                                            queue.add(neighborCoord);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        return false;
    }

    public boolean hasLost() {
        return prevFilledDistance != null && prevFilledDistance > 0 && prevFilledTiles == 0;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public TerminationCell getSourceCell() {
        return sourceCell;
    }

    public TerminationCell getSinkCell() {
        return sinkCell;
    }

    public Set<Coordinate> getFilledTiles() {
        return filledTiles;
    }

    public Cell[][] getCells() {
        return cells;
    }
}
