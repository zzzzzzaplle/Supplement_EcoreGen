import java.util.Scanner;

class ConsolePlayer extends Player {
    public ConsolePlayer() {}

    public ConsolePlayer(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter move (e.g., a1->b2): ");
        String input = scanner.nextLine().trim();
        scanner.close();

        try {
            String[] parts = input.split("->");
            int sourceCol = parts[0].charAt(0) - 'a';
            int sourceRow = Integer.parseInt(parts[0].substring(1)) - 1;
            int destCol = parts[1].charAt(0) - 'a';
            int destRow = Integer.parseInt(parts[1].substring(1)) - 1;

            Move proposed = new Move(new Place(sourceCol, sourceRow), new Place(destCol, destRow));
            for (Move m : availableMoves) {
                if (m.equals(proposed)) {
                    return m;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return availableMoves.length > 0 ? availableMoves[0] : null;
    }

    @Override
    public ConsolePlayer clone() throws CloneNotSupportedException {
        return (ConsolePlayer) super.clone();
    }
}
