import java.util.Random;

class RandomPlayer extends Player {
    public RandomPlayer() {}

    public RandomPlayer(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        Random random = new Random();
        return availableMoves[random.nextInt(availableMoves.length)];
    }

    @Override
    public RandomPlayer clone() throws CloneNotSupportedException {
        return (RandomPlayer) super.clone();
    }
}
