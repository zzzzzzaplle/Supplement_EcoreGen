import java.util.*;

/**
 * Main game class orchestrating the pipe connection game.
 */
public class Game {
    private int rows;
    private int cols;
    private int delay;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;
    private int numOfSteps;

    public Game() {
        this.cellStack = new CellStack();
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.rows = rows;
        this.cols = cols;
        this.delay = delay;
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    /**
     * Creates a Game from string representation.
     */
    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at the given row and column character (e.g., 'A').
     * Maps player column letter to internal column index using col - 'A' + 1.
     */
    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe nextPipe = pipeQueue.consume();

        if (map.tryPlacePipe(row, colIndex, nextPipe)) {
            // Find the FillableCell that was just placed
            Cell cell = map.cells[row][colIndex];
            if (cell instanceof FillableCell) {
                cellStack.push((FillableCell) cell);
            }
            numOfSteps++;
            return true;
        }

        // Undo of auto-consume when placement fails
        pipeQueue.undo(nextPipe);
        return false;
    }

    /**
     * Skips the current pipe (consumes it) and increments the step count.
     */
    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    /**
     * Undoes the last step. Returns false if there are no steps to undo.
     * Pops the last placed cell from CellStack, restores the pipe to the queue head, clears the cell on the map.
     */
    public boolean undoStep() {
        FillableCell lastCell = cellStack.pop();
        if (lastCell == null) {
            return false;
        }

        // Get the pipe before clearing it
        Pipe pipeToRestore = lastCell.getPipeObject();

        // Clear the cell on the map
        int row = lastCell.coord.row;
        int col = lastCell.coord.col;
        lastCell.setPipe(null);

        // Restore the pipe to the queue head
        pipeQueue.undo(pipeToRestore);

        numOfSteps++;
        return true;
    }

    /**
     * Updates the game state (delay countdown and water flow).
     */
    public void updateState() {
        delayBar.countdown();
        int distance = delayBar.distance();

        if (distance > 0) {
            map.fillTiles(distance);
        }
    }

    /**
     * Checks if the water has reached the source.
     */
    public void checkWin() {
        map.fillBeginTile();
    }

    /**
     * Checks for win condition: connected path from SOURCE to SINK.
     */
    public boolean hasWon() {
        // Check if there's a filled path from source to sink
        return map.checkPath();
    }

    /**
     * Returns true if the game is lost.
     */
    public boolean hasLost() {
        return map.hasLost();
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public Map getMap() {
        return map;
    }

    public CellStack getCellStack() {
        return cellStack;
    }

    public PipeQueue getPipeQueue() {
        return pipeQueue;
    }

    public DelayBar getDelayBar() {
        return delayBar;
    }
}
