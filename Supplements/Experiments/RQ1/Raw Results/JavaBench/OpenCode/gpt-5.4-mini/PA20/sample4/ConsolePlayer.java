public class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        return availableMoves.length == 0 ? null : availableMoves[0];
    }
}
