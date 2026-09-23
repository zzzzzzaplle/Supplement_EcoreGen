import java.util.List;

/**
 * Main game class, manages map, pipe queue, cell stack, delay bar and step count.
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

    static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe current = pipeQueue.peek();
        if (current == null) {
            return false;
        }
        if (!map.tryPlacePipe(row, colIndex, current)) {
            return false;
        }
        pipeQueue.consume();
        Cell cell = map.cells[row][colIndex];
        if (cell instanceof FillableCell) {
            cellStack.push((FillableCell) cell);
        }
        numOfSteps++;
        return true;
    }

    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    public boolean undoStep() {
        if (cellStack.getCellStack().isEmpty()) {
            return false;
        }
        FillableCell last = cellStack.pop();
        Pipe pipe = last.getPipe().orElse(null);
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        last.setPipe(null);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        delayBar.countdown();
        int distance = delayBar.distance();
        if (delayBar.getCurrentValue() == 0) {
            map.fillBeginTile();
        }
        if (distance > 0) {
            map.fillTiles(distance);
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        return map.hasLost();
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
