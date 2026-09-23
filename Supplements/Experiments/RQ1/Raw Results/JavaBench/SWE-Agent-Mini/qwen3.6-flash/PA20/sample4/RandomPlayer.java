import java.util.Random;

public class RandomPlayer extends Player {
    private static final Random random = new Random();

    public RandomPlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        int index = random.nextInt(availableMoves.length);
        return availableMoves[index];
    }
}
