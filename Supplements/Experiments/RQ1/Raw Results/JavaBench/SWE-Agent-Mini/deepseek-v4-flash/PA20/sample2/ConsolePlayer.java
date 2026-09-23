public class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // Read from console
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        while (true) {
            System.out.print("Enter your move (source->destination): ");
            String input = scanner.nextLine().trim();
            if (input.contains("->")) {
                String[] parts = input.split("->");
                if (parts.length == 2) {
                    String src = parts[0].trim();
                    String dst = parts[1].trim();
                    try {
                        int srcX = src.charAt(0) - 'a';
                        int srcY = Integer.parseInt(src.substring(1)) - 1;
                        int dstX = dst.charAt(0) - 'a';
                        int dstY = Integer.parseInt(dst.substring(1)) - 1;
                        Place source = new Place(srcX, srcY);
                        Place destination = new Place(dstX, dstY);
                        Move move = new Move(source, destination);
                        for (Move m : availableMoves) {
                            if (m.equals(move)) {
                                return move;
                            }
                        }
                        System.out.println("Invalid move. Try again.");
                    } catch (Exception e) {
                        System.out.println("Invalid input format. Use e.g., a1->b2");
                    }
                }
            } else {
                System.out.println("Invalid input format. Use e.g., a1->b2");
            }
        }
    }
}
