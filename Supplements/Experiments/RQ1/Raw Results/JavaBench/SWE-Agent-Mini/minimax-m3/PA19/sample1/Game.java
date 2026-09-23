import java.util.List;

/**
 * Top-level game class coordinating the map, cell stack, pipe queue, and delay bar.
 */
public class Game {

    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
        this.numOfSteps = 0;
        this.map = new Map();
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

    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe pipe = pipeQueue.peek();
        if (pipe == null) {
            return false;
        }
        boolean placed = map.tryPlacePipe(row, colIndex, pipe);
        if (placed) {
            Cell cell = map.getCells()[row][colIndex];
            if (cell instanceof FillableCell) {
                cellStack.push((FillableCell) cell);
            }
            pipeQueue.consume();
            numOfSteps++;
        }
        return placed;
    }

    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    public boolean undoStep() {
        FillableCell cell = cellStack.pop();
        if (cell == null) {
            return false;
        }
        Pipe pipe = cell.getPipe().orElse(null);
        Coordinate coord = cell.getCoord();
        map.undo(coord);
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        numOfSteps++;
        return true;
    }

    public void updateState() {
        int dist = delayBar.distance();
        if (dist <= 0) {
            // During countdown, just countdown
            delayBar.countdown();
            return;
        }
        // After delay ends, water flows
        map.fillBeginTile();
        map.fillTiles(dist);
        delayBar.countdown();
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        int dist = delayBar.distance();
        if (dist <= 0) {
            return false;
        }
        return map.hasLost();
    }
}
