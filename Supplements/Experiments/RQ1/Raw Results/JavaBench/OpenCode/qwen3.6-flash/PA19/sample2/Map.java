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
        this(0, 0, new Cell[0][0]);
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
        findTerminationCells();
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

    static Map fromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(final Coordinate coord, final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row >= rows - 1 || col < 1 || col >= cols - 1) {
            return false;
        }
        if (!(cells[row][col] instanceof FillableCell)) {
            return false;
        }
        FillableCell fillableCell = (FillableCell) cells[row][col];
        if (fillableCell.getPipe() != null) {
            return false;
        }
        fillableCell.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        if (cells[coord.row][coord.col] instanceof FillableCell filledCell) {
            Pipe pipe = filledCell.getPipe();
            filledCell.setPipe(null);
            if (pipe != null) {
                CellStack.undoPipe(pipe);
            }
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (filledTiles.isEmpty()) {
            return;
        }
        Set<Coordinate> newFilled = new HashSet<>();
        List<Coordinate> currentFrontier = new ArrayList<>(filledTiles);

        for (Coordinate coord : currentFrontier) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof TerminationCell && !((TerminationCell) cell).isFilled()) {
                ((TerminationCell) cell).setFilled();
            }
        }

        for (int i = 0; i < distance; i++) {
            List<Coordinate> nextFrontier = new ArrayList<>();
            for (Coordinate coord : currentFrontier) {
                Cell cell = cells[coord.row][coord.col];
                if (cell instanceof FillableCell fillable) {
                    Pipe pipe = fillable.getPipe();
                    if (pipe != null) {
                        Direction[] connections = pipe.getConnections();
                        for (Direction dir : connections) {
                            Coordinate neighbor = coord.add(dir.getOffset());
                            if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                                Cell neighborCell = cells[neighbor.row][neighbor.col];
                                if (neighborCell instanceof FillableCell neighborFillable) {
                                    Pipe neighborPipe = neighborFillable.getPipe();
                                    if (neighborPipe != null) {
                                        Direction[] neighborConnections = neighborPipe.getConnections();
                                        for (Direction neighborDir : neighborConnections) {
                                            if (neighborDir == dir.getOpposite()) {
                                                if (!filledTiles.contains(neighbor)) {
                                                    newFilled.add(neighbor);
                                                    nextFrontier.add(neighbor);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            currentFrontier = nextFrontier;
            if (currentFrontier.isEmpty()) {
                break;
            }
        }
        for (Coordinate coord : newFilled) {
            Cell cell = cells[coord.row][coord.col];
            if (cell instanceof TerminationCell) {
                ((TerminationCell) cell).setFilled();
            }
            if (cell instanceof FillableCell fillable && fillable.getPipe() != null) {
                filledTiles.add(coord);
            }
        }
    }

    public boolean checkPath() {
        if (sourceCell == null) {
            return false;
        }
        boolean[][] visited = new boolean[rows][cols];
        Queue<Coordinate> queue = new LinkedList<>();
        Coordinate sourceNeighbor = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (sourceNeighbor.row >= 0 && sourceNeighbor.row < rows && sourceNeighbor.col >= 0 && sourceNeighbor.col < cols) {
            queue.add(sourceNeighbor);
            visited[sourceNeighbor.row][sourceNeighbor.col] = true;
        }

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell currentCell = cells[current.row][current.col];
            if (currentCell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) currentCell;
                if (tc.type == TerminationType.SINK) {
                    return true;
                }
                continue;
            }
            if (currentCell instanceof FillableCell fillable) {
                Pipe pipe = fillable.getPipe();
                if (pipe != null) {
                    Direction[] connections = pipe.getConnections();
                    for (Direction dir : connections) {
                        Coordinate neighbor = current.add(dir.getOffset());
                        if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                            if (!visited[neighbor.row][neighbor.col]) {
                                visited[neighbor.row][neighbor.col] = true;
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        if (prevFilledDistance == null || prevFilledDistance <= 0) {
            return false;
        }
        return prevFilledTiles == 0;
    }

    private void findTerminationCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.type == TerminationType.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }
    }
}
