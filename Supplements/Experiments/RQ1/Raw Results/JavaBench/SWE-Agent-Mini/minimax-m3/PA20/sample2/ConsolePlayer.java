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
        while (true) {
            System.out.print("Enter your move (e.g., a1->b2): ");
            String input = scanner.nextLine().trim();
            Move parsed = parseMove(input, game);
            if (parsed != null) {
                for (Move m : availableMoves) {
                    if (m.equals(parsed)) {
                        return parsed;
                    }
                }
                System.out.println("Invalid move: not in available moves. Try again.");
            }
        }
    }

    private Move parseMove(String input, Game game) {
        if (input == null) return null;
        String[] parts = input.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid format. Use source->destination (e.g., a1->b2).");
            return null;
        }
        Place src = parsePlace(parts[0].trim(), game);
        Place dst = parsePlace(parts[1].trim(), game);
        if (src == null || dst == null) {
            return null;
        }
        return new Move(src, dst);
    }

    private Place parsePlace(String s, Game game) {
        if (s == null || s.length() < 2) {
            System.out.println("Invalid coordinate: " + s);
            return null;
        }
        char colChar = s.charAt(0);
        int size = game.getConfiguration().getSize();
        int x = colChar - 'a';
        if (x < 0 || x >= size) {
            System.out.println("Invalid column: " + colChar);
            return null;
        }
        int y;
        try {
            y = Integer.parseInt(s.substring(1)) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid row: " + s.substring(1));
            return null;
        }
        if (y < 0 || y >= size) {
            System.out.println("Row out of bounds: " + (y + 1));
            return null;
        }
        return new Place(x, y);
    }
}
