import java.util.Scanner;

public class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter move (e.g., a1->b2): ");
            String input = scanner.nextLine().trim();
            for (Move move : availableMoves) {
                String srcName = toCoordinateName(move.getSource());
                String destName = toCoordinateName(move.getDestination());
                String expected = srcName + "->" + destName;
                if (expected.equals(input)) {
                    return move;
                }
            }
            System.out.println("Invalid move, try again.");
        }
    }

    private String toCoordinateName(Place place) {
        char col = (char) ('a' + place.x());
        int row = place.y() + 1;
        return "" + col + row;
    }
}
