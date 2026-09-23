import java.util.List;

/**
 * Main game class that manages the pipe-connection game logic.
 */
public class Game {

    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
        this.numOfSteps = 0;
        this.map = null;
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.numOfSteps = 0;
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
    }

    /**
     * Creates a Game from string representation.
     */
    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at the given row and column letter.
     * Maps column letter to internal column index using col - 'A' + 1.
     *
     * @param row the row number (1-based, playable area)
     * @param col the column letter (e.g., 'A')
     * @return true if placement was successful, false otherwise
     */
    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe currentPipe = pipeQueue.peek();
        if (currentPipe == null) {
            return false;
        }

        boolean success = map.tryPlacePipe(row, colIndex, currentPipe);
        if (success) {
            FillableCell cell = (FillableCell) map.cells[row][colIndex];
            cellStack.push(cell);
            pipeQueue.consume();
            numOfSteps++;
        }
        return success;
    }

    /**
     * Skips the current pipe (consumes it without placing).
     */
    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    /**
     * Undoes the last step. Pops the last placed cell from CellStack,
     * restores the pipe to the queue head, clears the cell on the map,
     * and increments the step count.
     *
     * @return true if undo was successful, false if there are no steps to undo
     */
    public boolean undoStep() {
        FillableCell cell = cellStack.pop();
        if (cell == null) {
            return false;
        }

        java.util.Optional<Pipe> pipeOpt = cell.getPipe();
        if (pipeOpt.isPresent()) {
            Pipe pipe = pipeOpt.get();
            pipeQueue.undo(pipe);
            map.undo(cell.coord);
            numOfSteps++;
            return true;
        }

        return false;
    }

    /**
     * Updates the game state: countdown delay, then fill tiles.
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
     * Checks if the player has won (connected path from source to sink).
     *
     * @return true if there is a connected path
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * Checks if the player has lost.
     * Returns true when prevFilledTiles == 0 and delay has ended.
     *
     * @return true if the player has lost
     */
    public boolean hasLost() {
        if (delayBar.distance() <= 0) {
            return false;
        }
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

    public void setNumOfSteps(int numOfSteps) {
        this.numOfSteps = numOfSteps;
    }
}
