import java.util.List;
import java.util.Optional;

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

    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char colChar) {
        int col = colChar - 'A' + 1;

        Pipe pipe = pipeQueue.peek();
        if (pipe == null) {
            return false;
        }

        if (!map.tryPlacePipe(row, col, pipe)) {
            return false;
        }

        FillableCell fillable = (FillableCell) map.cells[row][col];
        cellStack.push(fillable);
        pipeQueue.consume();
        numOfSteps++;
        return true;
    }

    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    public boolean undoStep() {
        CellStack stack = this.cellStack;
        if (stack.isEmpty()) {
            return false;
        }

        FillableCell cell = stack.pop();
        if (cell == null) {
            return false;
        }

        Pipe pipe = cell.getPipe();
        if (pipe != null) {
            pipeQueue.undo(pipe);
            cell.setPipe(null);
        }

        numOfSteps++;
        return true;
    }

    public void updateState() {
        delayBar.countdown();

        int distance = delayBar.distance();

        if (distance <= 0) {
            return;
        }

        map.setPrevFilledTiles(map.getFilledTiles().size());

        if (distance == 1) {
            map.fillBeginTile();
        }
        map.fillTiles(distance);

        map.setPrevFilledDistance(distance);
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        int distance = delayBar.distance();
        if (distance <= 0) {
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
}
