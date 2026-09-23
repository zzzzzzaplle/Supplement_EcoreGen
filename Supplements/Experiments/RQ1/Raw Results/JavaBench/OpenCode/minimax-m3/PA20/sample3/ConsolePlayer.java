import java.util.Scanner;

public class ConsolePlayer extends Player {
    private Scanner scanner;

    public ConsolePlayer() {
        super();
        this.scanner = new Scanner(System.in);
    }

    public ConsolePlayer(String name) {
        super(name);
        this.scanner = new Scanner(System.in);
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
        this.scanner = new Scanner(System.in);
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        while (true) {
            System.out.printf("%s%s%s, please input your move: ", this.color, this.name, Color.DEFAULT);
            String input;
            try {
                input = scanner.nextLine().trim();
            } catch (Exception e) {
                continue;
            }
            Move move = parseMove(input);
            if (move == null) {
                System.out.println("Invalid input format. Expected: a1->b2");
                continue;
            }
            for (Move candidate : availableMoves) {
                if (candidate.equals(move)) {
                    return candidate;
                }
            }
            System.out.println("The move is not available, please input again.");
        }
    }

    public Move parseMove(String input) {
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

    public Place parsePlace(String token) {
        if (token == null || token.length() < 2) {
            return null;
        }
        char colChar = Character.toLowerCase(token.charAt(0));
        if (colChar < 'a' || colChar > 'z') {
            return null;
        }
        int x = colChar - 'a';
        int y;
        try {
            y = Integer.parseInt(token.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (y < 0) {
            return null;
        }
        return new Place(x, y);
    }
}
