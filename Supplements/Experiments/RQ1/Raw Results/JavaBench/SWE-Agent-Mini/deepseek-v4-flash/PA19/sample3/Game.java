import java.util.*;

/**
 * Main game class managing map, cell stack, pipe queue, and delay bar.
 */
public class Game {

    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
    }

    /**
     * Constructs a Game with the given parameters.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes != null ? pipes : new ArrayList<>());
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
     * Places a pipe at the given row and column letter.
     * Column letter 'A' maps to internal col 1.
     */
    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe currentPipe = pipeQueue.peek();
        if (currentPipe == null) {
            return false;
        }

        Coordinate coord = new Coordinate(row, colIndex);
        if (map.tryPlacePipe(coord, currentPipe)) {
            // Find the FillableCell at this coordinate
            Cell cell = map.getCells()[row][colIndex];
            if (cell instanceof FillableCell) {
                cellStack.push((FillableCell) cell);
            }
            pipeQueue.consume();
            numOfSteps++;
            return true;
        }
        return false;
    }

    /**
     * Skips the current pipe (consumes without placing).
     */
    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    /**
     * Undoes the last step.
     */
    public boolean undoStep() {
        FillableCell cell = cellStack.pop();
        if (cell == null) {
            return false;
        }

        // Get the pipe that was in the cell
        Pipe pipe = cell.getPipe().orElse(null);
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }

        // Clear the cell on the map
        Coordinate coord = cell.getCoord();
        map.undo(coord);

        numOfSteps++;
        return true;
    }

    /**
     * Updates the game state (water flow).
     */
    public void updateState() {
        delayBar.countdown();
        int distance = delayBar.distance();

        if (distance > 0) {
            if (distance == 1) {
                map.fillBeginTile();
            }
            map.fillTiles(distance);
        }
    }

    /**
     * Returns true if the player has won.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * Returns true if the player has lost.
     */
    public boolean hasLost() {
        int distance = delayBar.distance();
        return distance > 0 && map.hasLost();
    }

    /**
     * Returns the number of steps taken.
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
