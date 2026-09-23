import java.util.Scanner;

public class ConsolePlayer extends Player {
    private transient Scanner scanner;
    
    public ConsolePlayer() {
        this.scanner = new Scanner(System.in);
    }
    
    public ConsolePlayer(String name, Color color) {
        this();
        this.name = name;
        this.color = color;
        this.score = 0;
    }
    
    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        System.out.print("Enter move (e.g., a1->b2): ");
        String input = scanner.nextLine().trim();
        
        // Parse input "a1->b2"
        String[] parts = input.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid input format. Try again.");
            return nextMove(game, availableMoves);
        }
        
        // Parse source
        String sourceStr = parts[0].trim();
        int sourceCol = 0;
        int sourceRow = 0;
        try {
            sourceCol = sourceStr.charAt(0) - 'a';
            sourceRow = Integer.parseInt(sourceStr.substring(1)) - 1;
        } catch (Exception e) {
            System.out.println("Invalid input format. Try again.");
            return nextMove(game, availableMoves);
        }
        
        // Parse destination
        String destStr = parts[1].trim();
        int destCol = 0;
        int destRow = 0;
        try {
            destCol = destStr.charAt(0) - 'a';
            destRow = Integer.parseInt(destStr.substring(1)) - 1;
        } catch (Exception e) {
            System.out.println("Invalid input format. Try again.");
            return nextMove(game, availableMoves);
        }
        
        Place source = new Place(sourceCol, sourceRow);
        Place destination = new Place(destCol, destRow);
        Move candidate = new Move(source, destination);
        
        // Check if the move is in available moves
        for (Move available : availableMoves) {
            if (available.equals(candidate)) {
                return candidate;
            }
        }
        
        System.out.println("Invalid move. Try again.");
        return nextMove(game, availableMoves);
    }
    
    @Override
    public Player clone() throws CloneNotSupportedException {
        ConsolePlayer cloned = (ConsolePlayer) super.clone();
        cloned.scanner = new Scanner(System.in);
        return cloned;
    }
}
