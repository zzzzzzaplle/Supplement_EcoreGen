import edu.pa20.Color;
import edu.pa20.Move;
import edu.pa20.Game;
import org.eclipse.emf.common.util.EList;


public class EventuallyWinPlayer extends MockPlayer {
    public EventuallyWinPlayer(Color color) {
        super(color);
    }

    @Override
    public Move nextMove(Game game, EList<Move> availableMoves) {
        int minDistance = Integer.MAX_VALUE;
        Move best = availableMoves.get(0);
        for (Move move :
                availableMoves) {
            int dist = Math.abs(move.getDestination().getX() - move.getSource().getX()) +
                    Math.abs(move.getDestination().getY() - move.getSource().getY());
            if (dist <= minDistance) {
                minDistance = dist;
                best = move;
            }
        }
        return best;
    }
}
