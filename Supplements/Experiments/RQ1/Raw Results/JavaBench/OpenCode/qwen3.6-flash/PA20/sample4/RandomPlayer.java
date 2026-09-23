import java.util.Random;

public class RandomPlayer extends Player {

    public RandomPlayer() {
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        return null;
    }
}
