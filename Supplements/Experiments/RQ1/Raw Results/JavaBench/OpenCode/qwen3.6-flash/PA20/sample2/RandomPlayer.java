import java.util.Random;

public class RandomPlayer extends Player {
    private Random random = new Random();

    public RandomPlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves.length == 0) {
            throw new RuntimeException("No available moves");
        }
        int index = random.nextInt(availableMoves.length);
        return availableMoves[index];
    }
}
