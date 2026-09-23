import java.util.List;

/**
 * Main game class that manages game state, pipe placement, undo, and win/loss conditions.
 */
public class Game {

    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at the given row and column mapped from player input.
     * Maps column letter to internal column index using col - 'A' + 1.
     *
     * @param row the row (playable area aligned)
     * @param col the column letter (e.g., 'A', 'B', etc.)
     * @return true if placement succeeded
     */
    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe pipe = pipeQueue.peek();
        if (pipe == null) {
            return false;
        }
        if (map.tryPlacePipe(row, colIndex, pipe)) {
            FillableCell cell = (FillableCell) map.cells[row][colIndex];
            cell.setPipe(pipe);
            cellStack.push(cell);
            pipeQueue.consume();
            numOfSteps++;
            return true;
        }
        return false;
    }

    /**
     * Skips the current pipe (consumes it without placing) and increments step count.
     */
    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    /**
     * Undoes the last step. Pops the last placed cell, restores the pipe to the queue,
     * clears the cell on the map, and increments the step count.
     *
     * @return true if undo was successful, false if there are no steps to undo
     */
    public boolean undoStep() {
        FillableCell cell = cellStack.pop();
        if (cell == null) {
            return false;
        }
        if (cell.getPipe().isPresent()) {
            Pipe pipe = cell.getPipe().get();
            pipeQueue.undo(pipe);
            cell.setPipe(null);
            map.undo(cell.coord);
            numOfSteps++;
            return true;
        }
        return false;
    }

    /**
     * Updates the game state: countdown delay, fill water, check win/loss.
     */
    public void updateState() {
        delayBar.countdown();
        int distance = delayBar.distance();
        if (distance > 0) {
            map.fillBeginTile();
            map.fillTiles(distance);
        }
    }

    /**
     * Returns true if the player has won (connected path exists from source to sink
     * and all pipes along the path are filled).
     *
     * @return true if won
     */
    public boolean hasWon() {
        // First check if there's a connected path
        if (!map.checkPath()) {
            return false;
        }
        // Check that the path is filled (source and sink are filled)
        // We can check if source is filled and BFS along filled pipes to sink
        if (!map.getSourceCell().isFilled()) {
            return false;
        }

        // BFS along filled pipes to see if sink is reachable
        java.util.Queue<Coordinate> queue = new java.util.LinkedList<>();
        java.util.Set<Coordinate> visited = new java.util.HashSet<>();

        Coordinate sourceOffset = map.getSourceCell().pointingTo.getOffset();
        Coordinate startCoord = map.getSourceCell().coord.add(sourceOffset);

        if (startCoord.row < 0 || startCoord.row >= map.getRows() || startCoord.col < 0 || startCoord.col >= map.getCols()) {
            return false;
        }

        queue.add(startCoord);
        visited.add(startCoord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = map.cells[current.row][current.col];

            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                if (tc.type == TerminationType.SINK && tc.isFilled()) {
                    return true;
                }
            }

            if (cell instanceof FillableCell) {
                FillableCell fc = (FillableCell) cell;
                if (fc.getPipe().isPresent()) {
                    Pipe pipe = fc.getPipe().get();
                    if (pipe.getFilled()) {
                        for (Direction dir : pipe.getConnections()) {
                            Coordinate neighbor = current.add(dir.getOffset());
                            if (neighbor.row >= 0 && neighbor.row < map.getRows() && neighbor.col >= 0 && neighbor.col < map.getCols()
                                    && !visited.contains(neighbor)) {
                                Cell neighborCell = map.cells[neighbor.row][neighbor.col];
                                if (neighborCell instanceof FillableCell) {
                                    FillableCell nfc = (FillableCell) neighborCell;
                                    if (nfc.getPipe().isPresent() && nfc.getPipe().get().getFilled()) {
                                        Direction opposite = dir.getOpposite();
                                        boolean match = false;
                                        for (Direction nd : nfc.getPipe().get().getConnections()) {
                                            if (nd == opposite) {
                                                match = true;
                                                break;
                                            }
                                        }
                                        if (match) {
                                            visited.add(neighbor);
                                            queue.add(neighbor);
                                        }
                                    }
                                } else if (neighborCell instanceof TerminationCell) {
                                    TerminationCell tc = (TerminationCell) neighborCell;
                                    if (tc.type == TerminationType.SINK && tc.isFilled()) {
                                        visited.add(neighbor);
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

    /**
     * Returns true if the player has lost.
     *
     * @return true if lost
     */
    public boolean hasLost() {
        int distance = delayBar.distance();
        return distance > 0 && map.hasLost();
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public void setNumOfSteps(int numOfSteps) {
        this.numOfSteps = numOfSteps;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public CellStack getCellStack() {
        return cellStack;
    }

    public void setCellStack(CellStack cellStack) {
        this.cellStack = cellStack;
    }

    public PipeQueue getPipeQueue() {
        return pipeQueue;
    }

    public void setPipeQueue(PipeQueue pipeQueue) {
        this.pipeQueue = pipeQueue;
    }

    public DelayBar getDelayBar() {
        return delayBar;
    }

    public void setDelayBar(DelayBar delayBar) {
        this.delayBar = delayBar;
    }
}
