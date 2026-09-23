import java.util.Scanner;

public class ConsolePlayer extends Player {
    private Scanner scanner;

    public ConsolePlayer() {
        this.scanner = new Scanner(System.in);
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
        this.scanner = new Scanner(System.in);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        while (true) {
            System.out.println("Enter source->destination (e.g., a1->b2):");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("->");
            if (parts.length != 2) {
                System.out.println("Invalid format. Use source->destination.");
                continue;
            }
            Place source = parsePlace(parts[0].trim());
            Place destination = parsePlace(parts[1].trim());
            if (source == null || destination == null) {
                System.out.println("Invalid coordinates. Use format like a1.");
                continue;
            }
            for (Move move : availableMoves) {
                if (move.getSource().equals(source) && move.getDestination().equals(destination)) {
                    return move;
                }
            }
            System.out.println("Invalid move. Please choose from available moves:");
            for (Move m : availableMoves) {
                System.out.println("  " + m.getSource().x() + "," + m.getSource().y() + " -> " + m.getDestination().x() + "," + m.getDestination().y());
            }
        }
    }

    private Place parsePlace(String input) {
        if (input.length() < 2) {
            return null;
        }
        int col = input.charAt(0) - 'a';
        int row = 0;
        for (int i = 1; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i))) {
                row = row * 10 + (input.charAt(i) - '0');
            } else {
                return null;
            }
        }
        row--;
        return new Place(col, row);
    }
}
