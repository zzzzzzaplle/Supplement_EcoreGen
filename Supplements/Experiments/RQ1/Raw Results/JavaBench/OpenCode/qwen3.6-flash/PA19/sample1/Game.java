import java.util.List;

/**
 * Main game controller: handles player actions, water flow, and win/loss conditions.
 */
public class Game {

    private  int numOfSteps;
    private  Map map;
    private  CellStack cellStack;
    private  PipeQueue pipeQueue;
    private  DelayBar delayBar;

    public Game() {
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
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

    static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at the given row and column letter.
     * Maps column letter 'A' to internal column index 1, 'B' to 2, etc.
     */
    public boolean placePipe(int row, char colLetter) {
        int col = colLetter - 'A' + 1;
        Pipe pipe = pipeQueue.peek();

        Coordinate coord = new Coordinate(row, col);
        boolean success = map.tryPlacePipe(coord, pipe);
        if (success) {
            FillableCell placedCell = (FillableCell) map.getCells()[row][col];
            placedCell.setPipe(pipe);
            cellStack.push(placedCell);
        }
        pipeQueue.consume();
        numOfSteps++;
        return success;
    }

    /**
     * Consumes the current pipe from the queue and increments the step count.
     */
    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    /**
     * Undoes the last placed cell: pops from CellStack, restores pipe to queue head,
     * clears the cell on the map, and increments the step count.
     * Returns false if there are no steps to undo.
     */
    public boolean undoStep() {
        if (cellStack.getCellStack().isEmpty()) {
            return false;
        }
        FillableCell undone = cellStack.pop();
        Pipe pipe = undone.getPipe();
        map.undo(undone.getCoord());
        pipeQueue.undo(pipe);
        numOfSteps++;
        return true;
    }

    /**
     * Advances the game state: handles delay countdown and water flow.
     */
    public void updateState() {
        delayBar.countdown();
        int distance = delayBar.distance();

        if (distance >= 1) {
            if (distance == 1) {
                map.fillBeginTile();
            }
            map.fillTiles(distance);
        }
    }

    /**
     * Returns true when a connected path exists from SOURCE to SINK.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * Returns true when water no longer flows and no progress is being made.
     */
    public boolean hasLost() {
        return map.hasLost();
    }
}
