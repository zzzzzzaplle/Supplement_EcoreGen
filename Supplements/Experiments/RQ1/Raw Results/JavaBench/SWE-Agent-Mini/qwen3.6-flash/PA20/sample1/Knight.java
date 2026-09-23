public class Knight extends Piece implements Cloneable {
    public Knight() {
        super();
    }

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};

        int size = game.getConfiguration().getSize();
        java.util.ArrayList<Move> moves = new java.util.ArrayList<>();

        for (int i = 0; i < 8; i++) {
            int newX = source.x() + dx[i];
            int newY = source.y() + dy[i];

            if (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                Place dest = new Place(newX, newY);
                Move move = new Move(source, dest);
                moves.add(move);
            }
        }

        return moves.toArray(new Move[0]);
    }
}
