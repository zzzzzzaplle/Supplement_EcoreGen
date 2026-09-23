public class RandomPlayer extends Player {
    public RandomPlayer() {
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        return availableMoves.length > 0 ? availableMoves[0] : null;
    }
}
