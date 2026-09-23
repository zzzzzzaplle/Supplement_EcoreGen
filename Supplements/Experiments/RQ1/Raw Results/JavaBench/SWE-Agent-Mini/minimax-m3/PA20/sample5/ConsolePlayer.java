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
            try {
                String[] parts = input.split("->");
                if (parts.length != 2) {
                    System.out.println("Invalid format. Use source->destination, e.g., a1->b2");
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
                System.out.println("That move is not in the available moves. Try again.");
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    private Place parsePlace(String s) {
        if (s.length() < 2) {
            throw new IllegalArgumentException("invalid place: " + s);
        }
        char col = s.charAt(0);
        int row = Integer.parseInt(s.substring(1));
        int x = col - 'a';
        int y = row - 1;
        return new Place(x, y);
    }

    @Override
    public ConsolePlayer clone() throws CloneNotSupportedException {
        return (ConsolePlayer) super.clone();
    }
}
