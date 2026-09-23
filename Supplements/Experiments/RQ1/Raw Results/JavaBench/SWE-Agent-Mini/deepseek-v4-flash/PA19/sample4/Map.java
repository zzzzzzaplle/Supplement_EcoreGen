import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
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
        this.prevFilledTiles = 0;
        this.prevFilledDistance = null;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this();
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;

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
        if (distance <= 0) {
            return;
        }

        Set<Coordinate> newlyFilled = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        Coordinate sourceCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate startCoord = sourceCoord.add(sourceDir.getOffset());

        if (startCoord.row < 0 || startCoord.row >= rows || startCoord.col < 0 || startCoord.col >= cols) {
            prevFilledTiles = 0;
            prevFilledDistance = distance;
            return;
        }

        queue.add(startCoord);
        visited.add(sourceCoord);

        int currentDist = 1;

        while (!queue.isEmpty() && currentDist <= distance) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Coordinate current = queue.poll();
                if (current == null || visited.contains(current)) {
                    continue;
                }
                visited.add(current);

                Cell cell = cells[current.row][current.col];
                if (cell instanceof FillableCell) {
                    FillableCell fc = (FillableCell) cell;
                    Optional<Pipe> pipeOpt = fc.getPipe();
                    if (pipeOpt.isPresent()) {
                        Pipe pipe = pipeOpt.get();
                        pipe.setFilled();
                        newlyFilled.add(current);
                        filledTiles.add(current);

                        Direction[] connections = pipe.getConnections();
                        for (Direction dir : connections) {
                            Coordinate neighbor = current.add(dir.getOffset());
                            if (!visited.contains(neighbor) && isInBounds(neighbor)) {
                                Cell neighborCell = cells[neighbor.row][neighbor.col];
                                if (neighborCell instanceof FillableCell) {
                                    FillableCell nfc = (FillableCell) neighborCell;
                                    Optional<Pipe> nPipeOpt = nfc.getPipe();
                                    if (nPipeOpt.isPresent()) {
                                        Pipe nPipe = nPipeOpt.get();
                                        Direction[] nConnections = nPipe.getConnections();
                                        boolean connected = false;
                                        for (Direction nDir : nConnections) {
                                            if (nDir == dir.getOpposite()) {
                                                connected = true;
                                                break;
                                            }
                                        }
                                        if (connected) {
                                            queue.add(neighbor);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            currentDist++;
        }

        prevFilledTiles = newlyFilled.size();
        prevFilledDistance = distance;
    }

    private boolean isInBounds(Coordinate coord) {
        return coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols;
    }

    public boolean checkPath() {
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        Coordinate sourceCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate startCoord = sourceCoord.add(sourceDir.getOffset());

        if (!isInBounds(startCoord)) {
            return false;
        }

        queue.add(startCoord);
        visited.add(sourceCoord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            Cell cell = cells[current.row][current.col];
            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.getType() == TerminationType.SINK) {
                    return true;
                }
                continue;
            }

            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                Optional<Pipe> pipeOpt = fc.getPipe();
                if (pipeOpt.isPresent()) {
                    Pipe pipe = pipeOpt.get();
                    Direction[] connections = pipe.getConnections();
                    for (Direction dir : connections) {
                        Coordinate neighbor = current.add(dir.getOffset());
                        if (!visited.contains(neighbor) && isInBounds(neighbor)) {
                            Cell neighborCell = cells[neighbor.row][neighbor.col];
                            if (neighborCell instanceof FillableCell) {
                                FillableCell nfc = (FillableCell) neighborCell;
                                Optional<Pipe> nPipeOpt = nfc.getPipe();
                                if (nPipeOpt.isPresent()) {
                                    Pipe nPipe = nPipeOpt.get();
                                    Direction[] nConnections = nPipe.getConnections();
                                    boolean connected = false;
                                    for (Direction nDir : nConnections) {
                                        if (nDir == dir.getOpposite()) {
                                            connected = true;
                                            break;
                                        }
                                    }
                                    if (connected) {
                                        queue.add(neighbor);
                                    }
                                }
                            } else if (neighborCell instanceof TerminationCell) {
                                TerminationCell ntc = (TerminationCell) neighborCell;
                                if (ntc.getType() == TerminationType.SINK) {
                                    Direction sinkDir = ntc.getPointingTo();
                                    if (sinkDir == dir.getOpposite()) {
                                        queue.add(neighbor);
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
        return prevFilledTiles == 0;
    }
}
