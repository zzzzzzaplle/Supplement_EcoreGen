import java.util.*;

/**
 * Main game class managing player actions and game state.
 */
public class Game {

    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;
    private int numOfSteps;

    public Game() {
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
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

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public void setNumOfSteps(int numOfSteps) {
        this.numOfSteps = numOfSteps;
    }

    static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char colChar) {
        // Map column letter to internal column index: col - 'A' + 1
        int col = colChar - 'A' + 1;
        Coordinate coord = new Coordinate(row, col);

        Pipe pipe = pipeQueue.peek();
        if (map.tryPlacePipe(coord, pipe)) {
            FillableCell fillableCell = (FillableCell) map.cells[row][col];
            cellStack.push(fillableCell);
            pipeQueue.consume();
            numOfSteps++;
            return true;
        }
        return false;
    }

    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    public boolean undoStep() {
        if (cellStack.getUndoCount() == 0) {
            return false;
        }

        FillableCell lastCell = cellStack.pop();
        Coordinate coord = lastCell.coord;
        Pipe pipe = lastCell.getPipe().orElse(null);

        // Clear the cell on the map
        if (coord != null && coord.col != 0) {
            map.undo(coord);
        }

        // Restore pipe to queue head
        pipeQueue.undo(pipe);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        // Countdown phase
        if (delayBar.distance() <= 0) {
            delayBar.countdown();
        } else {
            // Water flow
            int distance = delayBar.distance();
            map.fillBeginTile();
            map.fillTiles(distance);
            delayBar.countdown();
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        return map.hasLost();
    }
}
