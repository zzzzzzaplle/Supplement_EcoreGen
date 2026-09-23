import java.util.Scanner;

public class ConsolePlayer extends Player {

    public ConsolePlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // In a real implementation, this would read from console
        // For now, we return a simple placeholder
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        // Parse input like "a1->b2"
        String[] parts = input.split("->");
        if (parts.length == 2) {
            String src = parts[0];
            String dst = parts[1];
            int srcX = src.charAt(0) - 'a';
            int srcY = Integer.parseInt(src.substring(1)) - 1;
            int dstX = dst.charAt(0) - 'a';
            int dstY = Integer.parseInt(dst.substring(1)) - 1;
            return new Move(new Place(srcX, srcY), new Place(dstX, dstY));
        }
        return null;
    }
}
