import java.util.List;

/**
 * Main game class managing the map, pipe queue, cell stack, and delay bar.
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

    /**
     * Creates a Game from string parameters.
     */
    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at the given row and column letter.
     * Column letter maps to internal index: col - 'A' + 1.
     *
     * @param row the row (playable index)
     * @param col the column letter (e.g., 'A')
     * @return true if placement was successful
     */
    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe pipe = pipeQueue.peek();
        if (pipe == null) {
            return false;
        }
        boolean success = map.tryPlacePipe(row, colIndex, pipe);
        if (success) {
            FillableCell fc = (FillableCell) map.getCells()[row][colIndex];
            cellStack.push(fc);
            pipeQueue.consume();
            numOfSteps++;
        }
        return success;
    }

    /**
     * Skips the current pipe in the queue.
     */
    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    /**
     * Undoes the last step.
     *
     * @return true if an undo was performed, false if no steps to undo
     */
    public boolean undoStep() {
        if (cellStack.getCellStack().isEmpty()) {
            return false;
        }
        FillableCell cell = cellStack.pop();
        Coordinate coord = cell.getCoord();
        Pipe pipe = cell.getPipe().orElse(null);
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        map.undo(coord);
        numOfSteps++;
        return true;
    }

    /**
     * Updates the game state by advancing the delay and water flow.
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
     * Checks if the player has won (path from source to sink exists).
     *
     * @return true if a path exists
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * Checks if the player has lost.
     *
     * @return true if lost
     */
    public boolean hasLost() {
        return map.hasLost();
    }

    /**
     * Returns the number of steps taken.
     *
     * @return step count
     */
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
