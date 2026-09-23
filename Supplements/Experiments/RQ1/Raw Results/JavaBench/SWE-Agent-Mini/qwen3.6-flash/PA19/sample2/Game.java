import java.util.List;

public class Game {

    private int numOfSteps = 0;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
        this.map = new Map();
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
    }

    private Game(
            int rows,
            int cols,
            int delay,
            Cell[][] cells,
            List<Pipe> pipes
    ) {
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    static Game fromString(
            int rows,
            int cols,
            int delay,
            String cellsRep,
            List<Pipe> pipes
    ) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe pipe = pipeQueue.peek();
        boolean placed = map.tryPlacePipe(row, colIndex, pipe);
        if (placed) {
            FillableCell placedCell = (FillableCell) map.cells[row][colIndex];
            cellStack.push(placedCell);
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
        if (cellStack.isEmpty()) {
            return false;
        }

        FillableCell cell = cellStack.pop();
        if (cell.getPipe().isPresent()) {
            pipeQueue.undo(cell.getPipe().get());
        }
        map.undo(cell.coord);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        delayBar.countdown();
        if (delayBar.distance() > 0) {
            map.fillTiles(delayBar.distance());
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        return delayBar.distance() > 0 && map.hasLost();
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

    public int getNumOfStepsField() {
        return numOfSteps;
    }

    public void setNumOfSteps(int numOfSteps) {
        this.numOfSteps = numOfSteps;
    }
}
