import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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
        this.filledTiles = new HashSet<>();
    }

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
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.getType() == TerminationType.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.getType() == TerminationType.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }
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
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) cell;
        if (fc.getPipe().isPresent()) {
            return false;
        }
        fc.setPipe(p);
        return true;
    }

    public void undo(Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (cell instanceof FillableCell) {
            FillableCell fc = (FillableCell) cell;
            fc.setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (prevFilledDistance != null && distance <= prevFilledDistance) {
            return;
        }

        fillBeginTile();

        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        Coordinate start = sourceCell.getCoord().add(sourceCell.getPointingTo().getOffset());
        queue.add(start);
        visited.add(start);

        int steps = 1;
        if (distance <= 0) {
            return;
        }

        while (!queue.isEmpty() && steps <= distance) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Coordinate current = queue.poll();
                int r = current.row;
                int c = current.col;

                if (r < 0 || r >= rows || c < 0 || c >= cols) {
                    continue;
                }
                Cell cell = cells[r][c];
                if (!(cell instanceof FillableCell)) {
                    continue;
                }
                FillableCell fc = (FillableCell) cell;
                if (!fc.getPipe().isPresent()) {
                    continue;
                }

                Pipe pipe = fc.getPipe().get();
                pipe.setFilled();
                filledTiles.add(current);

                for (Direction dir : pipe.getConnections()) {
                    Coordinate nextCoord = current.add(dir.getOffset());
                    int nr = nextCoord.row;
                    int nc = nextCoord.col;

                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                        continue;
                    }
                    if (visited.contains(nextCoord)) {
                        continue;
                    }

                    Cell nextCell = cells[nr][nc];
                    if (nextCell instanceof FillableCell) {
                        FillableCell nextFc = (FillableCell) nextCell;
                        if (nextFc.getPipe().isPresent()) {
                            Pipe nextPipe = nextFc.getPipe().get();
                            for (Direction nextDir : nextPipe.getConnections()) {
                                if (nextDir.getOpposite() == dir) {
                                    visited.add(nextCoord);
                                    queue.add(nextCoord);
                                    break;
                                }
                            }
                        }
                    } else if (nextCell instanceof TerminationCell) {
                        TerminationCell tc = (TerminationCell) nextCell;
                        if (tc.getType() == TerminationType.SINK) {
                            Direction sinkDir = tc.getPointingTo();
                            if (sinkDir.getOpposite() == dir) {
                                visited.add(nextCoord);
                            }
                        } else if (tc.getType() == TerminationType.SOURCE) {
                            if (r == sourceCell.getCoord().row && c == sourceCell.getCoord().col) {
                            } else {
                                visited.add(nextCoord);
                            }
                        }
                    }
                }
            }
            steps++;
        }

        prevFilledDistance = distance;
        prevFilledTiles = filledTiles.size();
    }

    public boolean checkPath() {
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        Coordinate start = sourceCell.getCoord().add(sourceCell.getPointingTo().getOffset());
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int r = current.row;
            int c = current.col;

            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                continue;
            }
            Cell cell = cells[r][c];

            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.getType() == TerminationType.SINK) {
                    return true;
                }
            }

            if (!(cell instanceof FillableCell)) {
                continue;
            }
            FillableCell fc = (FillableCell) cell;
            if (!fc.getPipe().isPresent()) {
                continue;
            }
            Pipe pipe = fc.getPipe().get();

            for (Direction dir : pipe.getConnections()) {
                Coordinate nextCoord = current.add(dir.getOffset());
                int nr = nextCoord.row;
                int nc = nextCoord.col;

                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                    continue;
                }
                if (visited.contains(nextCoord)) {
                    continue;
                }

                Cell nextCell = cells[nr][nc];
                if (nextCell instanceof FillableCell) {
                    FillableCell nextFc = (FillableCell) nextCell;
                    if (nextFc.getPipe().isPresent()) {
                        Pipe nextPipe = nextFc.getPipe().get();
                        for (Direction nextDir : nextPipe.getConnections()) {
                            if (nextDir.getOpposite() == dir) {
                                visited.add(nextCoord);
                                queue.add(nextCoord);
                                break;
                            }
                        }
                    }
                } else if (nextCell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) nextCell;
                    if (tc.getType() == TerminationType.SINK) {
                        Direction sinkDir = tc.getPointingTo();
                        if (sinkDir.getOpposite() == dir) {
                            return true;
                        }
                    }
                    if (tc.getType() == TerminationType.SOURCE) {
                        if (r == sourceCell.getCoord().row && c == sourceCell.getCoord().col) {
                        } else {
                            visited.add(nextCoord);
                            queue.add(nextCoord);
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == 0 && prevFilledDistance != null && prevFilledDistance > 0;
    }
}
