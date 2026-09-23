import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Represents the game map with cells and game logic.
 */
public class Map {

    private int rows;
    private int cols;
    public Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    private Set<MapFilledTile> filledTiles;
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

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[r][c];
                    if (tc.getType() == TerminationType.SOURCE) {
                        this.sourceCell = tc;
                    } else if (tc.getType() == TerminationType.SINK) {
                        this.sinkCell = tc;
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
        if (!(cells[row][col] instanceof FillableCell)) {
            return false;
        }
        FillableCell fillableCell = (FillableCell) cells[row][col];
        if (fillableCell.getPipe().isPresent()) {
            return false;
        }
        fillableCell.setPipe(pipe);
        return true;
    }

    public void undo(Coordinate coord) {
        if (cells[coord.row][coord.col] instanceof FillableCell) {
            FillableCell cell = (FillableCell) cells[coord.row][coord.col];
            cell.setPipe(null);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    public void fillTiles(int distance) {
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;

        Queue<MapFilledTile> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];

        for (MapFilledTile ft : filledTiles) {
            queue.offer(ft);
            visited[ft.row][ft.col] = true;
        }

        if (sourceCell != null) {
            Coordinate scCoord = sourceCell.getCoord();
            if (scCoord != null) {
                if (!visited[scCoord.row][scCoord.col]) {
                    MapFilledTile startTile = new MapFilledTile(scCoord.row, scCoord.col, 0);
                    filledTiles.add(startTile);
                    visited[scCoord.row][scCoord.col] = true;
                    queue.offer(startTile);
                }
            }
        }

        while (!queue.isEmpty()) {
            MapFilledTile current = queue.poll();

            Cell cell = cells[current.row][current.col];
            Pipe pipe = null;
            if (cell instanceof FillableCell) {
                pipe = ((FillableCell) cell).getPipe().orElse(null);
            }

            if (pipe != null) {
                Direction[] conns = pipe.getConnections();
                for (Direction dir : conns) {
                    Coordinate offset = dir.getOffset();
                    Coordinate neighbor = new Coordinate(current.row + offset.row, current.col + offset.col);

                    if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                        continue;
                    }
                    if (visited[neighbor.row][neighbor.col]) {
                        continue;
                    }

                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    Pipe neighborPipe = null;
                    if (neighborCell instanceof FillableCell) {
                        neighborPipe = ((FillableCell) neighborCell).getPipe().orElse(null);
                    }

                    if (neighborPipe != null) {
                        Direction[] neighborConns = neighborPipe.getConnections();
                        for (Direction ndir : neighborConns) {
                            if (ndir == dir.getOpposite()) {
                                if (!visited[neighbor.row][neighbor.col]) {
                                    MapFilledTile newTile = new MapFilledTile(neighbor.row, neighbor.col, current.dist + 1);
                                    filledTiles.add(newTile);
                                    visited[neighbor.row][neighbor.col] = true;
                                    queue.offer(newTile);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static class MapFilledTile {
        private final int row;
        private final int col;
        private final int dist;

        public MapFilledTile(int row, int col, int dist) {
            this.row = row;
            this.col = col;
            this.dist = dist;
        }

        public int row() {
            return row;
        }

        public int col() {
            return col;
        }

        public int dist() {
            return dist;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof MapFilledTile)) return false;
            MapFilledTile other = (MapFilledTile) obj;
            return this.row == other.row && this.col == other.col;
        }

        @Override
        public int hashCode() {
            return 31 * row + col;
        }
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }

        Queue<Coordinate> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];

        Coordinate startCoord = sourceCell.getCoord();
        Direction sourceDir = sourceCell.getPointingTo();
        Coordinate offset = sourceDir.getOffset();
        Coordinate start = new Coordinate(startCoord.row + offset.row, startCoord.col + offset.col);

        if (start.row < 0 || start.row >= rows || start.col < 0 || start.col >= cols) {
            return false;
        }

        queue.offer(start);
        visited[start.row][start.col] = true;

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = cells[current.row][current.col];

            if (cell instanceof TerminationCell && ((TerminationCell) cell).getType() == TerminationType.SINK) {
                return true;
            }

            if (cell instanceof FillableCell) {
                Pipe pipe = ((FillableCell) cell).getPipe().orElse(null);
                if (pipe != null) {
                    Direction[] conns = pipe.getConnections();
                    for (Direction dir : conns) {
                        Coordinate neighborCoord = new Coordinate(current.row + dir.getOffset().row, current.col + dir.getOffset().col);
                        if (neighborCoord.row < 0 || neighborCoord.row >= rows || neighborCoord.col < 0 || neighborCoord.col >= cols) {
                            continue;
                        }
                        if (visited[neighborCoord.row][neighborCoord.col]) {
                            continue;
                        }

                        Cell neighborCell = cells[neighborCoord.row][neighborCoord.col];
                        Pipe neighborPipe = null;
                        if (neighborCell instanceof FillableCell) {
                            neighborPipe = ((FillableCell) neighborCell).getPipe().orElse(null);
                        }

                        if (neighborPipe != null) {
                            for (Direction ndir : neighborPipe.getConnections()) {
                                if (ndir == dir.getOpposite()) {
                                    visited[neighborCoord.row][neighborCoord.col] = true;
                                    queue.offer(neighborCoord);
                                    break;
                                }
                            }
                        } else if (neighborCell instanceof TerminationCell) {
                            visited[neighborCoord.row][neighborCoord.col] = true;
                            queue.offer(neighborCoord);
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
}
