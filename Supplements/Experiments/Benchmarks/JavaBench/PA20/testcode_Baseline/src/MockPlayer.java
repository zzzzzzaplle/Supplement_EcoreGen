import java.util.Random;

public class MockPlayer extends Player {
    private Move[] nextMoves;
    private int nextMoveCounter = 0;

    public MockPlayer() {
        this(Color.CYAN);
    }

    public MockPlayer(Color color) {
        super();
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
    public Move nextMove(Game game, Move[] availableMoves) {
        if (nextMoves == null) {
            return availableMoves[new Random().nextInt(availableMoves.length)];
        } else {
            return this.nextMoves[this.nextMoveCounter++];
        }
    }
}
