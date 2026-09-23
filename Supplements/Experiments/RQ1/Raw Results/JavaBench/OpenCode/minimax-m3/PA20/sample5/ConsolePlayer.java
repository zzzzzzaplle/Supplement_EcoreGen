import java.util.Scanner;

public class ConsolePlayer extends Player {
    private static Scanner scanner = new Scanner(System.in);

    public ConsolePlayer() {
        super();
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        System.out.print(this.getName() + " (" + this.getColor() + this.getName() + Color.DEFAULT + "), enter your move: ");
        while (true) {
            String line = scanner.nextLine();
            if (line == null) {
                continue;
            }
            line = line.trim();
            Move move = parseMove(line);
            if (move == null) {
                System.out.print("Invalid format. Please use the format 'a1->b2': ");
                continue;
            }
            if (availableMoves == null) {
                return move;
            }
            for (Move m : availableMoves) {
                if (m.equals(move)) {
                    return move;
                }
            }
            System.out.print("Move not in available moves. Try again: ");
        }
    }

    private Move parseMove(String input) {
        if (input == null) {
            return null;
        }
        String[] parts = input.split("->");
        if (parts.length != 2) {
            return null;
        }
        Place source = parsePlace(parts[0].trim());
        Place destination = parsePlace(parts[1].trim());
        if (source == null || destination == null) {
            return null;
        }
        return new Move(source, destination);
    }

    private Place parsePlace(String s) {
        if (s == null || s.length() < 2) {
            return null;
        }
        char colChar = Character.toLowerCase(s.charAt(0));
        if (colChar < 'a' || colChar > 'z') {
            return null;
        }
        int col = colChar - 'a';
        int row;
        try {
            row = Integer.parseInt(s.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (row < 0) {
            return null;
        }
        return new Place(col, row);
    }
}
