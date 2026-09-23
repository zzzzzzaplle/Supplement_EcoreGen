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
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.filledTiles = new HashSet<>();
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
        if (!(cells[row][col] instanceof FillableCell)) {
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
        if (sourceCell == null) {
            prevFilledTiles = 0;
            prevFilledDistance = distance;
            return;
        }

        Set<Coordinate> newlyFilled = new HashSet<>();
        Set<Coordinate> visited = new HashSet<>();

        Queue<Object[]> queue = new LinkedList<>();
        queue.add(new Object[]{sourceCell.coord, Integer.valueOf(0)});
        visited.add(sourceCell.coord);
        sourceCell.setFilled();
        if (!filledTiles.contains(sourceCell.coord)) {
            newlyFilled.add(sourceCell.coord);
        }

        while (!queue.isEmpty()) {
            Object[] cur = queue.poll();
            Coordinate coord = (Coordinate) cur[0];
            int d = ((Integer) cur[1]).intValue();

            if (d >= distance) {
                continue;
            }

            Cell cell = cells[coord.row][coord.col];
            Direction[] connections;
            if (cell instanceof TerminationCell) {
                connections = new Direction[]{((TerminationCell) cell).pointingTo};
            } else if (cell instanceof FillableCell) {
                Optional<Pipe> pipeOpt = ((FillableCell) cell).getPipe();
                if (pipeOpt.isEmpty()) {
                    continue;
                }
                connections = pipeOpt.get().getConnections();
            } else {
                continue;
            }

            for (Direction dir : connections) {
                Coordinate offset = dir.getOffset();
                Coordinate next = coord.add(offset);
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (visited.contains(next)) {
                    continue;
                }

                Cell nextCell = cells[next.row][next.col];
                if (nextCell instanceof FillableCell) {
                    Optional<Pipe> nextPipeOpt = ((FillableCell) nextCell).getPipe();
                    if (nextPipeOpt.isEmpty()) {
                        continue;
                    }
                    boolean matches = false;
                    for (Direction nd : nextPipeOpt.get().getConnections()) {
                        if (nd == dir.getOpposite()) {
                            matches = true;
                            break;
                        }
                    }
                    if (!matches) {
                        continue;
                    }
                    nextPipeOpt.get().setFilled();
                    visited.add(next);
                    if (!filledTiles.contains(next)) {
                        newlyFilled.add(next);
                    }
                    queue.add(new Object[]{next, Integer.valueOf(d + 1)});
                } else if (nextCell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) nextCell;
                    if (tc.type == TerminationType.SINK && tc.pointingTo == dir) {
                        tc.setFilled();
                        visited.add(next);
                        if (!filledTiles.contains(next)) {
                            newlyFilled.add(next);
                        }
                    }
                }
            }
        }

        prevFilledTiles = newlyFilled.size();
        filledTiles.addAll(newlyFilled);
        prevFilledDistance = distance;
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();

        Coordinate startOffset = sourceCell.pointingTo.getOffset();
        Coordinate start = sourceCell.coord.add(startOffset);
        if (start.row < 0 || start.row >= rows || start.col < 0 || start.col >= cols) {
            return false;
        }
        if (cells[start.row][start.col] == sinkCell) {
            return true;
        }

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Coordinate coord = queue.poll();
            Cell cell = cells[coord.row][coord.col];

            if (cell == sinkCell) {
                return true;
            }
            if (!(cell instanceof FillableCell)) {
                continue;
            }
            Optional<Pipe> pipeOpt = ((FillableCell) cell).getPipe();
            if (pipeOpt.isEmpty()) {
                continue;
            }

            for (Direction dir : pipeOpt.get().getConnections()) {
                Coordinate offset = dir.getOffset();
                Coordinate next = coord.add(offset);
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (visited.contains(next)) {
                    continue;
                }
                visited.add(next);

                Cell nextCell = cells[next.row][next.col];
                if (nextCell == sinkCell) {
                    return true;
                }
                queue.add(next);
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
