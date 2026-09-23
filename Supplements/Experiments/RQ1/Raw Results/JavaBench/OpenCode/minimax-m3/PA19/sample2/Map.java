import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * The game map. Index 0 and (rows-1)/(cols-1) form the outer wall border; the playable
 * area is at internal indices [1..rows-2] x [1..cols-2], which aligns with 1-based user input.
 */
public class Map {

    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {
        this.rows = 0;
        this.cols = 0;
        this.cells = new Cell[0][0];
        this.filledTiles = new HashSet();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet();
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.type == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.type == TerminationType.SINK) {
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

    public boolean tryPlacePipe(Coordinate coord, Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    public boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        if (cells[row][col] == null || !(cells[row][col] instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) cells[row][col];
        if (fc.getPipe().isPresent()) {
            return false;
        }
        fc.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        if (coord == null) {
            return;
        }
        if (coord.row < 0 || coord.row >= rows || coord.col < 0 || coord.col >= cols) {
            return;
        }
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            ((FillableCell) cell).setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (distance < 0) {
            return;
        }
        Set<Coordinate> visited = new HashSet<Coordinate>();
        Queue<Cell> queue = new LinkedList<Cell>();
        if (sourceCell == null) {
            return;
        }
        queue.add(sourceCell);
        visited.add(sourceCell.coord);

        int currentDist = 0;
        int levelSize = 1;
        boolean sourceExpanded = false;

        while (!queue.isEmpty() && currentDist <= distance) {
            int nextLevelSize = 0;
            for (int i = 0; i < levelSize; i++) {
                Cell current = queue.poll();
                if (current == null) {
                    continue;
                }
                if (current == sourceCell && !sourceExpanded) {
                    Direction d = sourceCell.pointingTo;
                    if (d != null) {
                        Coordinate newCoord = sourceCell.coord.add(d.getOffset());
                        if (newCoord.row >= 0 && newCoord.row < rows
                                && newCoord.col >= 0 && newCoord.col < cols) {
                            Cell neighbor = cells[newCoord.row][newCoord.col];
                            if (neighbor instanceof FillableCell) {
                                FillableCell nfc = (FillableCell) neighbor;
                                Pipe np = nfc.getPipe().orElse(null);
                                if (np != null && Arrays.asList(np.getConnections()).contains(d.getOpposite())) {
                                    visited.add(newCoord);
                                    queue.add(neighbor);
                                    nextLevelSize++;
                                }
                            }
                        }
                    }
                    sourceExpanded = true;
                } else if (current == sinkCell) {
                    sinkCell.setFilled();
                } else if (current instanceof FillableCell) {
                    FillableCell fc = (FillableCell) current;
                    Pipe pipe = fc.getPipe().orElse(null);
                    if (pipe != null) {
                        pipe.setFilled();
                        filledTiles.add(fc.coord);
                        for (Direction d : pipe.getConnections()) {
                            Coordinate newCoord = fc.coord.add(d.getOffset());
                            if (visited.contains(newCoord)) {
                                continue;
                            }
                            if (newCoord.row < 0 || newCoord.row >= rows
                                    || newCoord.col < 0 || newCoord.col >= cols) {
                                continue;
                            }
                            Cell neighbor = cells[newCoord.row][newCoord.col];
                            if (neighbor instanceof FillableCell) {
                                FillableCell nfc = (FillableCell) neighbor;
                                Pipe np = nfc.getPipe().orElse(null);
                                if (np != null
                                        && Arrays.asList(np.getConnections()).contains(d.getOpposite())) {
                                    visited.add(newCoord);
                                    queue.add(neighbor);
                                    nextLevelSize++;
                                }
                            } else if (neighbor == sinkCell) {
                                visited.add(newCoord);
                                queue.add(neighbor);
                                nextLevelSize++;
                            }
                        }
                    }
                }
            }
            levelSize = nextLevelSize;
            currentDist++;
        }
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        Set<Coordinate> visited = new HashSet<Coordinate>();
        Queue<Cell> queue = new LinkedList<Cell>();
        queue.add(sourceCell);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current == null) {
                continue;
            }
            if (current == sinkCell) {
                return true;
            }
            Direction[] dirs;
            if (current == sourceCell) {
                dirs = new Direction[]{sourceCell.pointingTo};
            } else if (current instanceof FillableCell) {
                FillableCell fc = (FillableCell) current;
                if (fc.getPipe().isEmpty()) {
                    continue;
                }
                dirs = fc.getPipe().get().getConnections();
            } else {
                continue;
            }
            for (Direction d : dirs) {
                if (d == null) {
                    continue;
                }
                Coordinate newCoord = current.coord.add(d.getOffset());
                if (newCoord.row < 0 || newCoord.row >= rows
                        || newCoord.col < 0 || newCoord.col >= cols) {
                    continue;
                }
                if (visited.contains(newCoord)) {
                    continue;
                }
                Cell neighbor = cells[newCoord.row][newCoord.col];
                if (neighbor == sinkCell) {
                    return true;
                }
                if (neighbor instanceof FillableCell) {
                    FillableCell nfc = (FillableCell) neighbor;
                    if (nfc.getPipe().isPresent()) {
                        visited.add(newCoord);
                        queue.add(neighbor);
                    }
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

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
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

    public Set getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set filledTiles) {
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
