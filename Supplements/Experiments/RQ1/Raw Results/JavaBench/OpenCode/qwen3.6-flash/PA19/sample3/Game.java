import java.util.List;
import java.util.Optional;

/**
 * Represents the main game state and operations.
 */
public class Game {

    private int numOfSteps;
    private Map map;
    private CellStack cellStack;
    private PipeQueue pipeQueue;
    private DelayBar delayBar;

    public Game() {
        this(0, 0, 0, new Cell[0][0], List.of());
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.cellStack = new CellStack();
        this.pipeQueue = pipes != null ? new PipeQueue(pipes) : new PipeQueue();
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    public static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char col) {
        Pipe currentPipe = pipeQueue.peek();
        if (currentPipe == null) {
            return false;
        }
        int colIndex = col - 'A' + 1;
        Coordinate coord = new Coordinate(row, colIndex);
        if (map.tryPlacePipe(coord, currentPipe)) {
            FillableCell placedCell = (FillableCell) map.cells[row][colIndex];
            cellStack.push(placedCell);
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
        if (cellStack.isEmpty()) {
            return false;
        }
        FillableCell lastCell = cellStack.pop();
        Coordinate coord = lastCell.coord;
        if (coord != null) {
            if (coord.row >= 0 && coord.row < map.getRows() && coord.col >= 0 && coord.col < map.getCols()) {
                if (map.cells[coord.row][coord.col] instanceof FillableCell) {
                    FillableCell placedCell = (FillableCell) map.cells[coord.row][coord.col];
                    Pipe removedPipe = placedCell.getPipe().orElse(null);
                    if (removedPipe != null) {
                        pipeQueue.undo(removedPipe);
                    }
                    placedCell.setPipe(null);
                }
            }
        }
        numOfSteps++;
        return true;
    }

    public void updateState() {
        if (delayBar != null) {
            delayBar.countdown();
        }
        map.fillBeginTile();
        if (delayBar != null && delayBar.distance() > 0) {
            map.fillTiles(delayBar.distance());
        }
    }

    public boolean hasWon() {
        return map.checkPath();
    }

    public boolean hasLost() {
        if (delayBar != null && delayBar.distance() <= 0) {
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
