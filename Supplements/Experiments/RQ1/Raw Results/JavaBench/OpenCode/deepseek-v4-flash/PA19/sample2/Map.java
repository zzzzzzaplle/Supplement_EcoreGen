import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * The game map containing cells and managing pipe placement and water flow.
 */
public class Map {

    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<String> filledTiles;
    private int prevFilledTiles;
    private Integer prevFilledDistance;

    public Map() {
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

    public static Map fromString(int rows, int cols, String cellsRep) {
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
            ((FillableCell) cell).setPipe(null);
        }
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (distance <= 0) {
            return;
        }

        Queue<Coordinate> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        int[] distMap = new int[rows * cols];
        for (int i = 0; i < rows * cols; i++) {
            distMap[i] = -1;
        }

        Coordinate startCoord = sourceCell.getCoord();
        Direction startDir = sourceCell.getPointingTo();
        Coordinate firstStep = startCoord.add(startDir.getOffset());

        int sr = firstStep.row;
        int sc = firstStep.col;
        if (sr >= 1 && sr <= rows - 2 && sc >= 1 && sc <= cols - 2) {
            Cell firstCell = cells[sr][sc];
            if (firstCell instanceof FillableCell) {
                FillableCell fc = (FillableCell) firstCell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    Direction[] connections = pipe.getConnections();
                    boolean connects = false;
                    for (Direction d : connections) {
                        if (d == startDir.getOpposite()) {
                            connects = true;
                            break;
                        }
                    }
                    if (connects) {
                        queue.add(firstStep);
                        visited.add(sr + "," + sc);
                        distMap[sr * cols + sc] = 1;
                        pipe.setFilled();
                        filledTiles.add(sr + "," + sc);
                    }
                }
            }
        }

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currentDist = distMap[current.row * cols + current.col];
            if (currentDist >= distance) {
                continue;
            }

            Cell cell = cells[current.row][current.col];
            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate next = current.add(dir.getOffset());
                        int nr = next.row;
                        int nc = next.col;
                        if (nr >= 1 && nr <= rows - 2 && nc >= 1 && nc <= cols - 2) {
                            String key = nr + "," + nc;
                            if (!visited.contains(key)) {
                                Cell nextCell = cells[nr][nc];
                                if (nextCell instanceof FillableCell) {
                                    FillableCell nfc = (FillableCell) nextCell;
                                    if (nfc.getPipe().isPresent()) {
                                        Pipe nextPipe = nfc.getPipe().get();
                                        boolean connects = false;
                                        for (Direction nd : nextPipe.getConnections()) {
                                            if (nd == dir.getOpposite()) {
                                                connects = true;
                                                break;
                                            }
                                        }
                                        if (connects) {
                                            visited.add(key);
                                            distMap[nr * cols + nc] = currentDist + 1;
                                            queue.add(next);
                                            nextPipe.setFilled();
                                            filledTiles.add(key);
                                        }
                                    }
                                } else if (nextCell instanceof TerminationCell) {
                                    TerminationCell tc = (TerminationCell) nextCell;
                                    visited.add(key);
                                    tc.setFilled();
                                }
                            }
                        }
                    }
                }
            }
        }

        int newFilled = filledTiles.size();
        prevFilledTiles = newFilled - (distance > 1 ? filledTilesBeforeLastFill() : 0);
        prevFilledDistance = distance;
    }

    private int filledTilesBeforeLastFill() {
        return filledTiles.size() - prevFilledTiles;
    }

    public boolean checkPath() {
        Queue<Coordinate> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        Coordinate startCoord = sourceCell.getCoord();
        Direction startDir = sourceCell.getPointingTo();
        Coordinate firstStep = startCoord.add(startDir.getOffset());

        int sr = firstStep.row;
        int sc = firstStep.col;
        if (sr < 1 || sr > rows - 2 || sc < 1 || sc > cols - 2) {
            return false;
        }
        Cell firstCell = cells[sr][sc];
        if (!(firstCell instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) firstCell;
        if (!fc.getPipe().isPresent()) {
            return false;
        }
        Pipe firstPipe = fc.getPipe().get();
        boolean connects = false;
        for (Direction d : firstPipe.getConnections()) {
            if (d == startDir.getOpposite()) {
                connects = true;
                break;
            }
        }
        if (!connects) {
            return false;
        }

        queue.add(firstStep);
        visited.add(sr + "," + sc);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = cells[current.row][current.col];
            if (cell instanceof FillableCell) {
                FillableCell currentFc = (FillableCell) cell;
                if (currentFc.getPipe().isPresent()) {
                    Pipe pipe = currentFc.getPipe().get();
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate next = current.add(dir.getOffset());
                        int nr = next.row;
                        int nc = next.col;
                        String key = nr + "," + nc;
                        if (!visited.contains(key)) {
                            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                                Cell nextCell = cells[nr][nc];
                                if (nextCell instanceof FillableCell) {
                                    FillableCell nfc = (FillableCell) nextCell;
                                    if (nfc.getPipe().isPresent()) {
                                        Pipe nextPipe = nfc.getPipe().get();
                                        boolean conn = false;
                                        for (Direction nd : nextPipe.getConnections()) {
                                            if (nd == dir.getOpposite()) {
                                                conn = true;
                                                break;
                                            }
                                        }
                                        if (conn) {
                                            visited.add(key);
                                            queue.add(next);
                                        }
                                    }
                                } else if (nextCell instanceof TerminationCell) {
                                    TerminationCell tc = (TerminationCell) nextCell;
                                    if (tc.getType() == TerminationType.SINK) {
                                        return true;
                                    }
                                }
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

    public Set<String> getFilledTiles() {
        return filledTiles;
    }

    public void setFilledTiles(Set<String> filledTiles) {
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
