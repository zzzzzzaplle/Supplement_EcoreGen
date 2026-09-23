import java.util.Random;

public class RandomPlayer extends Player {

    public RandomPlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(availableMoves.length);
        return availableMoves[index];
    }
}
