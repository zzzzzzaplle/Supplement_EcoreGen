import java.util.Scanner;

public class ConsolePlayer extends Player {

    private static Scanner scanner = new Scanner(System.in);

    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        System.out.println(this.getName() + ", your available moves:");
        for (Move m : availableMoves) {
            System.out.println("  " + formatMove(m));
        }
        while (true) {
            System.out.print("Enter your move (e.g., a1->b2) or 'quit': ");
            String line;
            if (scanner.hasNextLine()) {
                line = scanner.nextLine().trim();
            } else {
                return null;
            }
            if (line.equalsIgnoreCase("quit")) {
                return null;
            }
            Move parsed = parseMove(line);
            if (parsed != null && containsMove(availableMoves, parsed)) {
                return parsed;
            }
            System.out.println("Invalid move. Try again.");
        }
    }

    private boolean containsMove(Move[] moves, Move candidate) {
        for (Move m : moves) {
            if (m.equals(candidate)) {
                return true;
            }
        }
        return false;
    }

    private String formatMove(Move move) {
        return formatPlace(move.getSource()) + "->" + formatPlace(move.getDestination());
    }

    private String formatPlace(Place place) {
        if (place == null) {
            return "?";
        }
        char col = (char) ('a' + place.x());
        int row = place.y() + 1;
        return String.valueOf(col) + row;
    }

    private Move parseMove(String input) {
        if (input == null) {
            return null;
        }
        String[] parts = input.split("->");
        if (parts.length != 2) {
            return null;
        }
        Place src = parsePlace(parts[0].trim());
        Place dst = parsePlace(parts[1].trim());
        if (src == null || dst == null) {
            return null;
        }
        return new Move(src, dst);
    }

    private Place parsePlace(String token) {
        if (token == null || token.length() < 2) {
            return null;
        }
        char col = Character.toLowerCase(token.charAt(0));
        if (col < 'a' || col > 'z') {
            return null;
        }
        int x = col - 'a';
        int y;
        try {
            y = Integer.parseInt(token.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (y < 0) {
            return null;
        }
        return new Place(x, y);
    }
}
