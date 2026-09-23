import java.util.*;
import java.util.stream.Collectors;

abstract class AbstractSokobanGame implements SokobanGame {

    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected void setExit() {
        this.isExitSpecified = true;
    }

    protected boolean shouldStop() {
        return this.isExitSpecified || this.state.isWin();
    }

    protected abstract ActionResult processAction(Action action);

    protected abstract Action fetchAction();

    @Override
    public void run() {
        System.out.println(StringResources.GAME_READY_MESSAGE);

        while (!shouldStop()) {
            Action action = fetchAction();
            processAction(action);
        }
    }
}
