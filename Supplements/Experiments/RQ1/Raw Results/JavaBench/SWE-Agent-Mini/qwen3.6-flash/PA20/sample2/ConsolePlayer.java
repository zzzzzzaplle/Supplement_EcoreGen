import java.util.*;

public class ConsolePlayer extends Player {
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter your move (source->destination), e.g. a1->b2:");
            String input = scanner.nextLine().trim();
            
            try {
                String[] parts = input.split("->");
                if (parts.length != 2) {
                    System.out.println("Invalid format. Use format source->destination, e.g. a1->b2.");
                    continue;
                }
                
                Place source = parseCoordinate(parts[0].trim());
                Place dest = parseCoordinate(parts[1].trim());
                
                for (Move move : availableMoves) {
                    if (move.getSource().equals(source) && move.getDestination().equals(dest)) {
                        return move;
                    }
                }
                
                System.out.println("Selected move is not valid. Please try again.");
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
    
    private Place parseCoordinate(String coord) {
        if (coord.length() < 2) {
            throw new IllegalArgumentException("Invalid coordinate: " + coord);
        }
        char col = coord.charAt(0);
        String rowStr = coord.substring(1);
        
        if (col < 'a' || col > 'z') {
            throw new IllegalArgumentException("Invalid column: " + col);
        }
        
        int x = col - 'a';
        int y;
        try {
            y = Integer.parseInt(rowStr) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row: " + rowStr);
        }
        
        if (y < 0) {
            throw new IllegalArgumentException("Row must be positive");
        }
        
        return new Place(x, y);
    }
    
    public ConsolePlayer() {
    }
}
