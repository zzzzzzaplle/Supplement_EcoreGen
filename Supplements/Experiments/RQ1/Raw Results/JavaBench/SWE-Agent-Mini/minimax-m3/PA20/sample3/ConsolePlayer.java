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
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("->");
            if (parts.length != 2) {
                System.out.println("Invalid format. Please use 'source->destination' (e.g., a1->b2).");
                continue;
            }
            try {
                Place source = parsePlace(parts[0].trim());
                Place destination = parsePlace(parts[1].trim());
                Move move = new Move(source, destination);
                for (Move m : availableMoves) {
                    if (m.equals(move)) {
                        return m;
                    }
                }
                System.out.println("Move is not in the list of available moves. Try again.");
            } catch (Exception e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    private Place parsePlace(String s) {
        if (s.length() < 2) {
            throw new IllegalArgumentException("place must be in the form of a letter followed by a number");
        }
        char col = s.charAt(0);
        int row = Integer.parseInt(s.substring(1));
        int x = col - 'a';
        int y = row - 1;
        return new Place(x, y);
    }
}
