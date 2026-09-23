import java.util.Random;

public class RandomPlayer extends Player {

    private static Random random = new Random();

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
        int index = random.nextInt(availableMoves.length);
        return availableMoves[index];
    }
}
