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
        while (true) {
            System.out.print("Enter your move (e.g., a1->b2): ");
            String input;
            if (scanner.hasNextLine()) {
                input = scanner.nextLine().trim();
            } else {
                return null;
            }
            try {
                String[] parts = input.split("->");
                if (parts.length != 2) {
                    System.out.println("Invalid input format. Try again.");
                    continue;
                }
                Place source = parsePlace(parts[0].trim());
                Place destination = parsePlace(parts[1].trim());
                Move move = new Move(source, destination);
                for (Move m : availableMoves) {
                    if (m.equals(move)) {
                        return m;
                    }
                }
                System.out.println("Invalid move. Try again.");
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    private Place parsePlace(String s) {
        if (s.length() < 2) {
            throw new IllegalArgumentException("Invalid place: " + s);
        }
        char col = s.charAt(0);
        int x = col - 'a';
        int y = Integer.parseInt(s.substring(1)) - 1;
        return new Place(x, y);
    }
}
