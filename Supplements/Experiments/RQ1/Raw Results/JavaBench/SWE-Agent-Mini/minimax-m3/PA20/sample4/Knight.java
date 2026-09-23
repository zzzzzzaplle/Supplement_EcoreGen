public class Knight extends Piece {

    public Knight() {
        super();
    }

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        java.util.List<Move> moves = new java.util.ArrayList<>();
        int[][] deltas = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] d : deltas) {
            int nx = source.x() + d[0];
            int ny = source.y() + d[1];
            if (nx >= 0 && ny >= 0 && nx < game.getConfiguration().getSize() && ny < game.getConfiguration().getSize()) {
                moves.add(new Move(source, new Place(nx, ny)));
            }
        }
        return moves.toArray(new Move[0]);
    }
}
