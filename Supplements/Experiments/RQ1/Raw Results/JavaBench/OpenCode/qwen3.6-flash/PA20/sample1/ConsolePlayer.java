import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsolePlayer extends Player {
    public ConsolePlayer() {}

    public ConsolePlayer(String name, java.awt.Color javaColor) {
        super(name, convertToColor(javaColor));
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        Move move = null;
        while (move == null) {
            System.out.println("Please enter your move (e.g., a1->b2):");
            String input = scanner.nextLine().trim();

            try {
                move = parseMove(input, game, availableMoves);
                if (move == null) {
                    System.out.println("Invalid move. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
        return move;
    }

    private Move parseMove(String input, Game game, Move[] availableMoves) {
        Pattern pattern = Pattern.compile("^([a-zA-Z])(\\d+)->([a-zA-Z])(\\d+)$");
        java.util.regex.Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            return null;
        }

        int x1 = matcher.group(1).toLowerCase().charAt(0) - 'a';
        int y1 = Integer.parseInt(matcher.group(2)) - 1;
        int x2 = matcher.group(3).toLowerCase().charAt(0) - 'a';
        int y2 = Integer.parseInt(matcher.group(4)) - 1;

        Place source = new Place(x1, y1);
        Place destination = new Place(x2, y2);
        Move move = new Move(source, destination);

        // Check if this move is in available moves
        for (Move avail : availableMoves) {
            if (avail.equals(move)) {
                return move;
            }
        }

        return null;
    }

    private static Color convertToColor(java.awt.Color javaColor) {
        int r = javaColor.getRed();
        int g = javaColor.getGreen();
        int b = javaColor.getBlue();

        if (r > 128 && g > 128 && b > 128) {
            return Color.WHITE;
        }
        if (r > 128 && g < 64 && b < 64) {
            return Color.RED;
        }
        if (r < 64 && g > 128 && b < 64) {
            return Color.GREEN;
        }
        if (r < 64 && g < 64 && b > 128) {
            return Color.BLUE;
        }
        return Color.WHITE;
    }
}
