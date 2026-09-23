public class Knight extends Piece {

    public Knight() {
    }

    public Knight(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        if (game == null || source == null) {
            return new Move[0];
        }
        int size = game.getConfiguration().getSize();
        int[][] deltas = new int[][] {
                {1, 2}, {2, 1}, {-1, 2}, {-2, 1},
                {1, -2}, {2, -1}, {-1, -2}, {-2, -1}
        };
        java.util.ArrayList<Move> moves = new java.util.ArrayList<Move>();
        for (int[] d : deltas) {
            int nx = source.x() + d[0];
            int ny = source.y() + d[1];
            if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                continue;
            }
            moves.add(new Move(source, new Place(nx, ny)));
        }
        return moves.toArray(new Move[0]);
    }
}
