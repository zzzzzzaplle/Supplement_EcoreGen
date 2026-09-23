import java.util.Scanner;

public class ConsolePlayer extends Player {

    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        return null;
    }
}
