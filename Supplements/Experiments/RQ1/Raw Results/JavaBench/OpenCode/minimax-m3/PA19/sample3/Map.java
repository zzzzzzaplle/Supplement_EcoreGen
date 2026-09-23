import java.util.HashMap;
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
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
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
            filledTiles.remove(coord);
        }
    }

      public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        if (distance < 0) {
            return;
        }
        prevFilledDistance = distance;
        int prevSize = filledTiles.size();

        HashMap<Coordinate, Integer> dist = new HashMap<>();
        Queue<Coordinate> queue = new LinkedList<>();

        sourceCell.setFilled();
        filledTiles.add(sourceCell.coord);
        dist.put(sourceCell.coord, 0);

        Coordinate startNext = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (inBounds(startNext)) {
            dist.put(startNext, 1);
            queue.add(startNext);
        }

        Set<Coordinate> visited = new HashSet<>();
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate curr = queue.poll();
            if (visited.contains(curr)) {
                continue;
            }
            visited.add(curr);

            int currDist = dist.getOrDefault(curr, Integer.MAX_VALUE);
            if (currDist > distance) {
                continue;
            }

            if (!inBounds(curr)) {
                continue;
            }
            Cell c = cells[curr.row][curr.col];
            if (!(c instanceof FillableCell)) {
                continue;
            }
            FillableCell fc = (FillableCell) c;
            if (fc.getPipe().isEmpty()) {
                continue;
            }
            Pipe p = fc.getPipe().get();

            if (!hasConnectedFilledNeighbor(curr, p)) {
                continue;
            }

            p.setFilled();
            filledTiles.add(curr);

            for (Direction d : p.getConnections()) {
                Coordinate next = curr.add(d.getOffset());
                if (!visited.contains(next) && inBounds(next)) {
                    int nextDist = currDist + 1;
                    if (!dist.containsKey(next) || dist.get(next) > nextDist) {
                        dist.put(next, nextDist);
                    }
                    queue.add(next);
                }
            }
        }

        prevFilledTiles = filledTiles.size() - prevSize;
    }

    private boolean hasConnectedFilledNeighbor(Coordinate curr, Pipe p) {
        for (Direction d : p.getConnections()) {
            Coordinate neighbor = curr.add(d.getOffset());
            if (!inBounds(neighbor)) {
                continue;
            }
            Cell nc = cells[neighbor.row][neighbor.col];
            if (nc instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) nc;
                if (tc.type == TerminationType.SOURCE && tc.getIsFilled()) {
                    if (tc.pointingTo == d.getOpposite()) {
                        return true;
                    }
                }
            } else if (nc instanceof FillableCell) {
                FillableCell nfc = (FillableCell) nc;
                if (nfc.getPipe().isPresent() && nfc.getPipe().get().getFilled()) {
                    Pipe np = nfc.getPipe().get();
                    for (Direction nd : np.getConnections()) {
                        if (nd == d.getOpposite()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean inBounds(Coordinate coord) {
        return coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols;
    }

    public boolean checkPath() {
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        visited.add(sourceCell.coord);

        Coordinate start = sourceCell.coord.add(sourceCell.pointingTo.getOffset());
        if (!inBounds(start)) {
            return false;
        }
        queue.add(start);

        while (!queue.isEmpty()) {
            Coordinate curr = queue.poll();
            if (visited.contains(curr)) {
                continue;
            }
            visited.add(curr);

            if (!inBounds(curr)) {
                continue;
            }
            Cell c = cells[curr.row][curr.col];
            if (c instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) c;
                if (tc.type == TerminationType.SINK) {
                    return true;
                }
                continue;
            }
            if (!(c instanceof FillableCell)) {
                continue;
            }
            FillableCell fc = (FillableCell) c;
            if (fc.getPipe().isEmpty()) {
                continue;
            }
            Pipe p = fc.getPipe().get();
            for (Direction d : p.getConnections()) {
                Coordinate next = curr.add(d.getOffset());
                if (!visited.contains(next) && inBounds(next)) {
                    queue.add(next);
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        if (prevFilledDistance == null) {
            return false;
        }
        if (prevFilledDistance <= 0) {
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
