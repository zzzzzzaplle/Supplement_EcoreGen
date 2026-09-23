import java.util.Scanner;

public class ConsolePlayer extends Player implements Cloneable {
    private Scanner scanner = new Scanner(System.in);

    public ConsolePlayer() {
        super();
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        // Display available moves with coordinate format
        System.out.println("Available moves:");
        int count = 0;
        for (Move move : availableMoves) {
            count++;
            System.out.println(count + ". " + move.getSource() + " -> " + move.getDestination());
        }
        System.out.println("Enter move number (or enter source->destination format):");

        try {
            String input = scanner.nextLine().trim();

            // Try to parse as move number
            try {
                int moveNum = Integer.parseInt(input);
                if (moveNum >= 1 && moveNum <= availableMoves.length) {
                    return availableMoves[moveNum - 1];
                }
            } catch (NumberFormatException e) {
                // Try to parse as coordinate format like "a1->b2"
            }

            // Parse coordinate format
            ParseResult result = parseCoordinate(input);
            if (result != null) {
                for (Move move : availableMoves) {
                    if (move.getSource().x() == result.sourceX &&
                        move.getSource().y() == result.sourceY &&
                        move.getDestination().x() == result.destX &&
                        move.getDestination().y() == result.destY) {
                        return move;
                    }
                }
            }
        } catch (Exception e) {
            // Invalid input, select first available move
        }

        return availableMoves[0];
    }

    private ParseResult parseCoordinate(String input) {
        int arrowIndex = input.indexOf("->");
        if (arrowIndex == -1) return null;

        String sourceStr = input.substring(0, arrowIndex).trim();
        String destStr = input.substring(arrowIndex + 2).trim();

        Result coordResult = parseCoordinateStr(sourceStr);
        if (coordResult != null) {
            Result dest = parseCoordinateStr(destStr);
            if (dest != null) {
                ParseResult pr = new ParseResult();
                pr.sourceX = coordResult.x;
                pr.sourceY = coordResult.y;
                pr.destX = dest.x;
                pr.destY = dest.y;
                return pr;
            }
        }
        return null;
    }

    private Result parseCoordinateStr(String coord) {
        if (coord.length() < 2) return null;
        int colIndex = coord.charAt(0) - 'a';
        if (colIndex < 0 || colIndex > 25) return null;

        try {
            String rest = coord.substring(1).trim();
            int rowIndex = Integer.parseInt(rest) - 1;
            Result r = new Result();
            r.x = colIndex;
            r.y = rowIndex;
            return r;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static class ParseResult {
        int sourceX, sourceY, destX, destY;
    }

    private static class Result {
        int x, y;
    }

    @Override
    public ConsolePlayer clone() throws CloneNotSupportedException {
        try {
            ConsolePlayer cloned = (ConsolePlayer) super.clone();
            cloned.scanner = new Scanner(System.in);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Impossible", e);
        }
    }
}
