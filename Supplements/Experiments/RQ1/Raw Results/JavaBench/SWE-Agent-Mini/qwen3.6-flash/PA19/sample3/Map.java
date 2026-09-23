import java.util.*;

public class Map {
    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<Coordinate> filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        findSourceAndSink();
    }

    private void findSourceAndSink() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationType.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }
    }

    public static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe pipe) {
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return false;
        }

        Cell cell = cells[row][col];

        if (!(cell instanceof FillableCell)) {
            return false;
        }

        FillableCell fillable = (FillableCell) cell;

        if (fillable.getPipe() != null) {
            return false;
        }

        fillable.setPipe(pipe);
        return true;
    }

    public void undo(Coordinate coord) {
        FillableCell fillable = (FillableCell) cells[coord.row][coord.col];
        fillable.setPipe(null);
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        

        java.util.Map<Coordinate, Integer> waterDistance = new HashMap<>();

        if (sourceCell != null) {
            waterDistance.put(sourceCell.coord, 0);
            filledTiles.add(sourceCell.coord);
        }

        Queue<Coordinate> queue = new LinkedList<>();
        if (sourceCell != null) {
            queue.add(sourceCell.coord);
        }

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Integer distVal = waterDistance.get(current);
            if (distVal == null) continue;
            int currentDist = distVal;

            if (currentDist >= distance) {
                continue;
            }

            for (Direction dir : Direction.values()) {
                Coordinate next = current.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }

                Cell nextCell = cells[next.row][next.col];
                if (!(nextCell instanceof FillableCell)) {
                    continue;
                }

                FillableCell fillable = (FillableCell) nextCell;
                Pipe pipe = fillable.getPipe();
                if (pipe == null) {
                    continue;
                }

                Direction reverseDir = dir.getOpposite();

                boolean currentHasConnection = false;
                if (current.equals(sourceCell.coord)) {
                    currentHasConnection = sourceCell.pointingTo == dir;
                } else if (current.equals(sinkCell.coord)) {
                    continue;
                } else {
                    Cell currentCell = cells[current.row][current.col];
                    if (currentCell instanceof FillableCell) {
                        Pipe currentPipe = ((FillableCell) currentCell).getPipe();
                        if (currentPipe != null) {
                            for (Direction pd : currentPipe.getConnections()) {
                                if (pd == dir) {
                                    currentHasConnection = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!currentHasConnection) {
                    continue;
                }

                boolean nextHasConnection = false;
                for (Direction pd : pipe.getConnections()) {
                    if (pd == reverseDir) {
                        nextHasConnection = true;
                        break;
                    }
                }

                if (!nextHasConnection) {
                    continue;
                }

                if (!waterDistance.containsKey(next)) {
                    waterDistance.put(next, currentDist + 1);
                    pipe.setFilled();
                    filledTiles.add(next);
                    queue.add(next);
                }
            }
        }
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();

        Coordinate start = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (start.row < 0 || start.row >= rows || start.col < 0 || start.col >= cols) {
            return false;
        }

        visited.add(sourceCell.coord);

        if (start.equals(sinkCell.coord)) {
            return true;
        }

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell currentCell = cells[current.row][current.col];

            if (currentCell instanceof TerminationCell && ((TerminationCell) currentCell).type == TerminationType.SINK) {
                if (current.equals(sinkCell.coord)) {
                    return true;
                }
            }

            List<Direction> directions = new ArrayList<>();
            if (currentCell instanceof FillableCell) {
                Pipe pipe = ((FillableCell) currentCell).getPipe();
                if (pipe != null) {
                    Collections.addAll(directions, pipe.getConnections());
                }
            } else {
                continue;
            }

            for (Direction dir : directions) {
                Coordinate next = current.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }

                if (visited.contains(next)) {
                    continue;
                }

                Cell nextCell = cells[next.row][next.col];

                boolean validConnection = false;
                if (nextCell instanceof FillableCell) {
                    Pipe nextPipe = ((FillableCell) nextCell).getPipe();
                    if (nextPipe != null) {
                        for (Direction pd : nextPipe.getConnections()) {
                            if (pd == dir.getOpposite()) {
                                validConnection = true;
                                break;
                            }
                        }
                    }
                } else if (nextCell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) nextCell;
                    if (tc.type == TerminationType.SINK) {
                        validConnection = true;
                    }
                }

                if (validConnection) {
                    if (nextCell instanceof TerminationCell && ((TerminationCell) nextCell).type == TerminationType.SINK) {
                        return true;
                    }
                    visited.add(next);
                    queue.add(next);
                }
            }
        }

        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public TerminationCell getSourceCell() {
        return sourceCell;
    }

    public void setSourceCell(TerminationCell sourceCell) {
        this.sourceCell = sourceCell;
    }

    public TerminationCell getSinkCell() {
        return sinkCell;
    }

    public void setSinkCell(TerminationCell sinkCell) {
        this.sinkCell = sinkCell;
    }

    public Set<Coordinate> getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set<Coordinate> filledTiles) {
        this.filledTiles = filledTiles;
    }

    public int getPrevFilledTiles() {
        return prevFilledTiles;
    }

    public void setPrevFilledTiles(int prevFilledTiles) {
        this.prevFilledTiles = prevFilledTiles;
    }

    public Integer getPrevFilledDistance() {
        return prevFilledDistance;
    }

    public void setPrevFilledDistance(Integer prevFilledDistance) {
        this.prevFilledDistance = prevFilledDistance;
    }
}
