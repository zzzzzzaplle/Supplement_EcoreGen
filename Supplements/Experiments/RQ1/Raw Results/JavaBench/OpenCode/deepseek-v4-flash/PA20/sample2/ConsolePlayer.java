import java.util.Scanner;

public class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your move (e.g. a1->b2): ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("->");
            if (parts.length != 2) {
                System.out.println("Invalid input format. Use source->destination (e.g. a1->b2)");
                continue;
            }
            try {
                Place source = parsePlace(parts[0]);
                Place destination = parsePlace(parts[1]);
                Move move = new Move(source, destination);
                for (Move m : availableMoves) {
                    if (m.equals(move)) {
                        return m;
                    }
                }
                System.out.println("Invalid move. Try again.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid coordinate format. Try again.");
            }
        }
    }

    private Place parsePlace(String s) {
        if (s.length() < 2) {
            throw new IllegalArgumentException();
        }
        char colChar = s.charAt(0);
        if (colChar < 'a' || colChar > 'z') {
            throw new IllegalArgumentException();
        }
        int x = colChar - 'a';
        int y = Integer.parseInt(s.substring(1)) - 1;
        return new Place(x, y);
    }
}
