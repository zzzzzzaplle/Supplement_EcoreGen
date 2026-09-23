import java.util.Random;

public class RandomPlayer extends Player {
    public RandomPlayer() {
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        Random random = new Random();
        return availableMoves[random.nextInt(availableMoves.length)];
    }
}
