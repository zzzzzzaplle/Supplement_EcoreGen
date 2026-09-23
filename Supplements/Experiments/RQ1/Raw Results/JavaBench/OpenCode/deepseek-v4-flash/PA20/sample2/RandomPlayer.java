import java.util.Random;

public class RandomPlayer extends Player {
    private Random random;

    public RandomPlayer() {
        this.random = new Random();
    }

    public RandomPlayer(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
        this.random = new Random();
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves.length == 0) {
            return null;
        }
        int index = random.nextInt(availableMoves.length);
        return availableMoves[index];
    }
}
