import java.util.List;

public class Game {

    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
        this.numOfSteps = 0;
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

    public boolean placePipe(int row, char col) {
        if (map == null || pipeQueue == null || cellStack == null) {
            return false;
        }
        int colIndex = col - 'A' + 1;
        Pipe p = pipeQueue.peek();
        if (p == null) {
            return false;
        }
        if (map.tryPlacePipe(row, colIndex, p)) {
            FillableCell fc = (FillableCell) map.cells[row][colIndex];
            cellStack.push(fc);
            pipeQueue.consume();
            numOfSteps++;
            return true;
        }
        return false;
    }

    public void skipPipe() {
        if (pipeQueue == null) {
            return;
        }
        pipeQueue.consume();
        numOfSteps++;
    }

    public boolean undoStep() {
        if (cellStack == null) {
            return false;
        }
        if (cellStack.getCellStack().isEmpty()) {
            return false;
        }
        FillableCell fc = cellStack.pop();
        Pipe pipe = fc.getRawPipe();
        fc.setPipe(null);
        if (map != null) {
            map.undo(fc.coord);
        }
        if (pipe != null && pipeQueue != null) {
            pipeQueue.undo(pipe);
        }
        numOfSteps++;
        return true;
    }

    public void updateState() {
        if (delayBar == null || map == null) {
            return;
        }
        delayBar.countdown();
        int dist = delayBar.distance();
        if (dist > 0) {
            map.fillTiles(dist);
        }
    }

    public boolean hasWon() {
        if (map == null) {
            return false;
        }
        return map.checkPath();
    }

    public boolean hasLost() {
        if (delayBar == null || map == null) {
            return false;
        }
        int dist = delayBar.distance();
        if (dist > 0) {
            return map.hasLost();
        }
        return false;
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
