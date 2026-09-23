import java.util.Random;

public class RandomPlayer extends Player {
    private static final Random random = new Random();

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
        try {
            return (Move) availableMoves[index].clone();
        } catch (CloneNotSupportedException e) {
            return availableMoves[index];
        }
    }
}
