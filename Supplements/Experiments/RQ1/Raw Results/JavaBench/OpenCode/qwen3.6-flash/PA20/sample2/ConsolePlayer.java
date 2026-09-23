import java.util.Scanner;

public class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose your move:");
            for (int i = 0; i < availableMoves.length; i++) {
                System.out.println("[" + i + "] " + availableMoves[i].toString());
            }
            String input = scanner.nextLine().trim();
            if (input.length() < 5) {
                continue;
            }
            try {
                String[] parts = input.split("->");
                Place source = parseCoordinate(parts[0].trim().toLowerCase());
                Place destination = parseCoordinate(parts[1].trim().toLowerCase());
                Move selected = new Move(source, destination);
                if (selected == null) {
                    continue;
                }
                for (Move move : availableMoves) {
                    if (move.equals(selected)) {
                        return move;
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    private Place parseCoordinate(String coord) {
        char colChar = coord.charAt(0);
        int col = colChar - 'a';
        int row = Integer.parseInt(coord.substring(1)) - 1;
        return new Place(col, row);
    }
}
