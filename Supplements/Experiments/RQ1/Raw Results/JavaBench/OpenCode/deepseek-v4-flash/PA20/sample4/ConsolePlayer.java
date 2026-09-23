import java.util.Scanner;

public class ConsolePlayer extends Player {

    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your move (e.g., a1->b2): ");
        String input = scanner.nextLine();
        String[] parts = input.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid input format. Use source->destination.");
            return nextMove(game, availableMoves);
        }
        int sourceX = parts[0].charAt(0) - 'a';
        int sourceY = Integer.parseInt(parts[0].substring(1)) - 1;
        int destX = parts[1].charAt(0) - 'a';
        int destY = Integer.parseInt(parts[1].substring(1)) - 1;
        Move move = new Move(new Place(sourceX, sourceY), new Place(destX, destY));
        for (Move m : availableMoves) {
            if (m.equals(move)) {
                return move;
            }
        }
        System.out.println("Invalid move. Try again.");
        return nextMove(game, availableMoves);
    }
}
