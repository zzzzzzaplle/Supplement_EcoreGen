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
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Available moves:");
        for (Move m : availableMoves) {
            System.out.println("  " + m);
        }
        while (true) {
            System.out.print("Enter your move (e.g. a1->b2): ");
            String line = scanner.nextLine().trim().toLowerCase();
            Move parsed = parseMove(line);
            if (parsed == null) {
                System.out.println("Invalid format. Please use format like a1->b2.");
                continue;
            }
            for (Move m : availableMoves) {
                if (m.equals(parsed)) {
                    return m;
                }
            }
            System.out.println("Move is not in available moves. Try again.");
        }
    }

    private Move parseMove(String line) {
        if (line == null || !line.contains("->")) {
            return null;
        }
        String[] parts = line.split("->");
        if (parts.length != 2) {
            return null;
        }
        Place source = parsePlace(parts[0].trim());
        Place dest = parsePlace(parts[1].trim());
        if (source == null || dest == null) {
            return null;
        }
        return new Move(source, dest);
    }

    private Place parsePlace(String s) {
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
        return new Place(x, y);
    }
}
