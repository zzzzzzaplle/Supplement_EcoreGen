import java.util.Random;

public class RandomPlayer extends Player {
    private Random random;

    public RandomPlayer() {
        super();
        this.random = new Random();
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
        this.random = new Random();
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        int index = this.random.nextInt(availableMoves.length);
        return availableMoves[index];
    }
}
