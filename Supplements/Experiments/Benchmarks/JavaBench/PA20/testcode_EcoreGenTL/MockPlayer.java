import edu.pa20.Color;
import edu.pa20.Game;
import edu.pa20.Move;
import edu.pa20.Player;
import org.eclipse.emf.common.util.EList;

import java.util.Random;

public class MockPlayer extends Player {
    private Move[] nextMoves;
    private int nextMoveCounter = 0;

    public MockPlayer() {
        this(Color.CYAN);
    }

    public MockPlayer(Color color) {
        setName("MockPlayer-" + new Random().nextInt());
        setColor(color);
    }

    public void resetCounter() {
        this.nextMoveCounter = 0;
    }

    public void setNextMoves(Move[] nextMoves) {
        this.nextMoves = nextMoves;
        this.resetCounter();
    }

    @Override
    public Move nextMove(Game game, EList<Move> availableMoves) {
        if (availableMoves == null || availableMoves.isEmpty()) {
            throw new IllegalArgumentException("availableMoves must be non-empty");
        }
        if (nextMoves == null) {
            return availableMoves.get(new Random().nextInt(availableMoves.size()));
        } else {
            return this.nextMoves[this.nextMoveCounter++];
        }
    }
}
