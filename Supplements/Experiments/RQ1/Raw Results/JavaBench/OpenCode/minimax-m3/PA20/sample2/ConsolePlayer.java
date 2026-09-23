import java.util.Scanner;

public class ConsolePlayer extends Player {

    public ConsolePlayer() {
        super();
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input your move: ");
        String line = scanner.nextLine().trim();
        if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
            return null;
        }
        String[] parts = line.split("->");
        if (parts.length != 2) {
            return null;
        }
        Place source = parsePlace(parts[0].trim());
        Place destination = parsePlace(parts[1].trim());
        if (source == null || destination == null) {
            return null;
        }
        for (Move move : availableMoves) {
            if (move.getSource().equals(source) && move.getDestination().equals(destination)) {
                return move;
            }
        }
        return null;
    }

    private Place parsePlace(String token) {
        if (token.length() < 2) {
            return null;
        }
        char colChar = token.charAt(0);
        int x = colChar - 'a';
        int y;
        try {
            y = Integer.parseInt(token.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        return new Place(x, y);
    }
}
