import java.util.*;

public class Game {
  private int numOfSteps;
  private Map map;
  private CellStack cellStack;
  private PipeQueue pipeQueue;
  private DelayBar delayBar;

  public Game() {}
  public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
      this.map = new Map(rows, cols, cells);
      this.cellStack = new CellStack();
      this.pipeQueue = new PipeQueue(pipes);
      this.delayBar = new DelayBar(delay);
  }

  public int getNumOfSteps() { return numOfSteps; }
  public void setNumOfSteps(int numOfSteps) { this.numOfSteps = numOfSteps; }
  public Map getMap() { return map; }
  public void setMap(Map map) { this.map = map; }
  public CellStack getCellStack() { return cellStack; }
  public void setCellStack(CellStack cellStack) { this.cellStack = cellStack; }
  public PipeQueue getPipeQueue() { return pipeQueue; }
  public void setPipeQueue(PipeQueue pipeQueue) { this.pipeQueue = pipeQueue; }
  public DelayBar getDelayBar() { return delayBar; }
  public void setDelayBar(DelayBar delayBar) { this.delayBar = delayBar; }

  static Game fromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
  }

  public boolean placePipe(int row, char colSpec) { return true; }
  public void skipPipe() {}
  public boolean undoStep() { return true; }
  public void updateState() {}
  public boolean hasWon() { return true; }
  public boolean hasLost() { return true; }
}
