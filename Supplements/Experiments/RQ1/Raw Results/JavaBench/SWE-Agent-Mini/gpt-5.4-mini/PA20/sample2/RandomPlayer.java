public class RandomPlayer extends Player {
    public RandomPlayer() {
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        return availableMoves != null && availableMoves.length > 0 ? availableMoves[0] : null;
    }
}
