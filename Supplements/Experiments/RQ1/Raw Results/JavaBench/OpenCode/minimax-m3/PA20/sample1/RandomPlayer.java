import java.util.Random;

public class RandomPlayer extends Player {
    private Random random;

    public RandomPlayer() {
        this.random = new Random();
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
        this.random = new Random();
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        return availableMoves[random.nextInt(availableMoves.length)];
    }
}
