public class RandomPlayer extends Player {
    public RandomPlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        return availableMoves == null || availableMoves.length == 0 ? null : availableMoves[0];
    }
}
