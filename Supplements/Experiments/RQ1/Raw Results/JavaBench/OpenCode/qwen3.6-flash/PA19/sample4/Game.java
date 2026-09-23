import java.util.List;

public class Game {
    private final int numOfSteps;
    private final Map map;
    private final CellStack cellStack;
    private final PipeQueue pipeQueue;
    private final DelayBar delayBar;

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

    public boolean placePipe(int row, int col) {
        Pipe currentPipe = pipeQueue.peek();
        if (currentPipe == null) {
            return false;
        }

        if (map.tryPlacePipe(row, col, currentPipe)) {
            Cell cell = map.getCell(row, col);
            if (cell instanceof FillableCell fillableCell) {
                cellStack.push(fillableCell);
            }
            pipeQueue.consume();
            return true;
        }
        return false;
    }

    public void skipPipe() {
        pipeQueue.consume();
    }

    public boolean undoStep() {
        if (cellStack.isEmpty()) {
            return false;
        }
        FillableCell cell = cellStack.pop();
        if (cell != null) {
            Pipe pipe = cell.getPipe().orElse(null);
            if (pipe != null) {
                cell.clearPipe();
                pipeQueue.undo(pipe);
            }
            return true;
        }
        return false;
    }

    public void updateState() {
        if (delayBar.isActive()) {
            delayBar.countdown();
        } else {
            map.fillBeginTile();
            map.fillTiles(delayBar.distance());
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

    public Map getMap() {
        return map;
    }

    public CellStack getCellStack() {
        return cellStack;
    }

    public PipeQueue getPipeQueue() {
        return pipeQueue;
    }

    public DelayBar getDelayBar() {
        return delayBar;
    }
}
