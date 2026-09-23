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
        System.out.print("Enter your move (e.g., a1->b2): ");
        String input = scanner.nextLine().trim();
        
        String[] parts = input.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid input format. Use source->destination (e.g., a1->b2)");
            return null;
        }
        
        Place source = parsePlace(parts[0]);
        Place destination = parsePlace(parts[1]);
        
        if (source == null || destination == null) {
            System.out.println("Invalid coordinate format.");
            return null;
        }
        
        return new Move(source, destination);
    }
    
    private Place parsePlace(String coord) {
        if (coord == null || coord.length() < 2) return null;
        
        char colChar = coord.charAt(0);
        if (colChar < 'a' || colChar > 'z') return null;
        
        try {
            int x = colChar - 'a';
            int y = Integer.parseInt(coord.substring(1)) - 1;
            return new Place(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
