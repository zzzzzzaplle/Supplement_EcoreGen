import java.util.Scanner;

public class ConsolePlayer extends Player {

    public ConsolePlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your move (e.g., a1->b2): ");
        String input = scanner.nextLine().trim();
        String[] parts = input.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid input format. Please use format: source->destination (e.g., a1->b2)");
            return nextMove(game, availableMoves);
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
            System.out.println("Move not available. Try again.");
            return nextMove(game, availableMoves);
        } catch (Exception e) {
            System.out.println("Invalid input. Please use format: a1->b2");
            return nextMove(game, availableMoves);
        }
    }

    private Place parsePlace(String input) {
        String s = input.trim().toLowerCase();
        int x = s.charAt(0) - 'a';
        int y = Integer.parseInt(s.substring(1)) - 1;
        return new Place(x, y);
    }
}
