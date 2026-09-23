import java.util.Scanner;

public class ConsolePlayer extends Player {
    private static final Scanner scanner = new Scanner(System.in);

    public ConsolePlayer() {
        super();
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        while (true) {
            System.out.print("Enter move (e.g., a1->b2): ");
            String input = scanner.nextLine().trim();
            
            try {
                String[] parts = input.split("->");
                if (parts.length != 2) {
                    System.out.println("Invalid format. Use source->destination (e.g., a1->b2)");
                    continue;
                }
                
                String srcStr = parts[0].trim();
                String destStr = parts[1].trim();
                
                int srcCol = srcStr.charAt(0) - 'a';
                int srcRow = Integer.parseInt(srcStr.substring(1)) - 1;
                int destCol = destStr.charAt(0) - 'a';
                int destRow = Integer.parseInt(destStr.substring(1)) - 1;
                
                Place src = new Place(srcCol, srcRow);
                Place dest = new Place(destCol, destRow);
                
                Move move = new Move(src, dest);
                
                // Check if this move is in available moves
                for (Move available : availableMoves) {
                    if (available.equals(move)) {
                        return move;
                    }
                }
                
                System.out.println("Invalid move. Please try again.");
            } catch (Exception e) {
                System.out.println("Invalid input format. Use source->destination (e.g., a1->b2)");
            }
        }
    }
}
