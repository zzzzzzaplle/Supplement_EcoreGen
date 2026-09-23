public class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        return availableMoves.length > 0 ? availableMoves[0] : null;
    }
}
