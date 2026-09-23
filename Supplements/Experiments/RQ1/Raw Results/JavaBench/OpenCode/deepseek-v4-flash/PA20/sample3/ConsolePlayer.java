import java.util.Scanner;

public class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your move (e.g., a1->b2): ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("->");
            if (parts.length != 2) {
                System.out.println("Invalid format. Use source->destination (e.g., a1->b2)");
                continue;
            }
            Place source = parsePlace(parts[0]);
            Place destination = parsePlace(parts[1]);
            if (source == null || destination == null) {
                System.out.println("Invalid coordinate format.");
                continue;
            }
            Move move = new Move(source, destination);
            for (Move available : availableMoves) {
                if (move.equals(available)) {
                    return move;
                }
            }
            System.out.println("Move not available. Try again.");
        }
    }

    private Place parsePlace(String s) {
        s = s.trim();
        if (s.length() < 2) {
            return null;
        }
        char colChar = s.charAt(0);
        if (colChar < 'a' || colChar > 'z') {
            return null;
        }
        int x = colChar - 'a';
        int y;
        try {
            y = Integer.parseInt(s.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (y < 0) {
            return null;
        }
        return new Place(x, y);
    }
}
