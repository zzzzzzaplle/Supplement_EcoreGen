import java.util.List;

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
        boolean placed = map.tryPlacePipe(row, colIndex, current);
        if (placed) {
            FillableCell cell = (FillableCell) map.cells[row][colIndex];
            cellStack.push(cell);
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
        Pipe pipe = cell.getPipe();
        cell.setPipe(null);
        pipeQueue.undo(pipe);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        delayBar.countdown();
        int dist = delayBar.distance();
        if (dist > 0) {
            if (map.getPrevFilledDistance() == null || dist > map.getPrevFilledDistance()) {
                if (map.getPrevFilledDistance() == null) {
                    map.fillBeginTile();
                }
                map.fillTiles(dist);
            }
        }
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
