import java.util.List;

/**
 * Top-level game state; ties Map, CellStack, PipeQueue and DelayBar together.
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
        this.delayBar = new DelayBar(0);
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
            Cell c = map.cells[row][colIndex];
            if (c instanceof FillableCell) {
                cellStack.push((FillableCell) c);
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
        cell.clearPipe();
        if (pipe != null) {
            pipeQueue.undo(pipe);
        }
        numOfSteps++;
        return true;
    }

    public void updateState() {
        if (delayBar.getCurrentValue() > 0) {
            delayBar.countdown();
            if (delayBar.getCurrentValue() == 0) {
                map.fillBeginTile();
                map.fillTiles(delayBar.distance());
            }
        } else {
            map.fillTiles(delayBar.distance());
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        if (delayBar.getCurrentValue() > 0) {
            return false;
        }
        if (delayBar.distance() <= 0) {
            return false;
        }
        return map.hasLost();
    }
}
