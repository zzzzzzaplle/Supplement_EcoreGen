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
            System.out.print("Your move: ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("->");
            if (parts.length != 2) {
                System.out.println("Invalid format. Use format: a1->b2");
                continue;
            }
            try {
                Place source = parsePlace(parts[0]);
                Place destination = parsePlace(parts[1]);
                Move move = new Move(source, destination);
                for (Move m : availableMoves) {
                    if (m.equals(move)) {
                        return move;
                    }
                }
                System.out.println("Invalid move. Try again.");
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    private Place parsePlace(String s) {
        char colChar = s.charAt(0);
        int x = colChar - 'a';
        int y = Integer.parseInt(s.substring(1)) - 1;
        return new Place(x, y);
    }
}
