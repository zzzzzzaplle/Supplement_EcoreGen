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
        System.out.print(this.name + " (" + this.color + this.name + Color.DEFAULT + "), enter your move (e.g., a1->b2): ");
        String input = scanner.nextLine().trim();
        Move parsed = parseMove(input, game);
        if (parsed == null) {
            return nextMove(game, availableMoves);
        }
        for (Move m : availableMoves) {
            if (m.equals(parsed)) {
                return m;
            }
        }
        System.out.println("Invalid move. Please try again.");
        return nextMove(game, availableMoves);
    }

    private Move parseMove(String input, Game game) {
        try {
            String[] parts = input.split("->");
            if (parts.length != 2) {
                return null;
            }
            Place source = parsePlace(parts[0].trim());
            Place destination = parsePlace(parts[1].trim());
            if (source == null || destination == null) {
                return null;
            }
            return new Move(source, destination);
        } catch (Exception e) {
            return null;
        }
    }

    private Place parsePlace(String s) {
        if (s.length() < 2) {
            return null;
        }
        char colChar = s.charAt(0);
        int x = colChar - 'a';
        int y;
        try {
            y = Integer.parseInt(s.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (x < 0 || y < 0) {
            return null;
        }
        return new Place(x, y);
    }
}
