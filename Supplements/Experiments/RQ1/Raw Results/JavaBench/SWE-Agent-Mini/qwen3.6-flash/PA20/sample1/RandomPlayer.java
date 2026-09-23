import java.util.Random;

public class RandomPlayer extends Player implements Cloneable {
    private Random random = new Random();

    public RandomPlayer() {
        super();
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        int index = random.nextInt(availableMoves.length);
        return availableMoves[index];
    }

    @Override
    public RandomPlayer clone() throws CloneNotSupportedException {
        try {
            RandomPlayer cloned = (RandomPlayer) super.clone();
            cloned.random = new Random();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Impossible", e);
        }
    }
}
