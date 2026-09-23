import edu.pa20.Color;
import edu.pa20.Game;
import edu.pa20.Move;
import org.eclipse.emf.common.util.EList;


public class RuleViolationPlayer extends EventuallyWinPlayer {
    public RuleViolationPlayer(Color color) {
        super(color);
    }

    public interface NextMoveListener {
        Move nextMove(Game game, EList<Move> availableMoves);
    }

    private NextMoveListener nextMoveListener;

    public void setNextMoveListener(NextMoveListener nextMoveListener) {
        this.nextMoveListener = nextMoveListener;
    }

    @Override
    public Move nextMove(Game game, EList<Move> availableMoves) {
        if (this.nextMoveListener == null) {
            return super.nextMove(game, availableMoves);
        } else {
            Move move = this.nextMoveListener.nextMove(game, availableMoves);
            if (move == null) {
                return super.nextMove(game, availableMoves);
            }
            return move;
        }
    }
}
