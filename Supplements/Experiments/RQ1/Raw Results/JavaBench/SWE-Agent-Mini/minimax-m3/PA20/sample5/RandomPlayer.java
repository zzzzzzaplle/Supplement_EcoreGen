import java.util.Random;

public class RandomPlayer extends Player {

    public RandomPlayer() {
        super();
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
        int idx = random.nextInt(availableMoves.length);
        return availableMoves[idx];
    }

    @Override
    public RandomPlayer clone() throws CloneNotSupportedException {
        return (RandomPlayer) super.clone();
    }
}
