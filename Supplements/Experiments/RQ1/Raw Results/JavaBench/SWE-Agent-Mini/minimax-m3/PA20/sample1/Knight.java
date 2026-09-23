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
        if (game == null || source == null) {
            return new Move[0];
        }
        int sx = source.x();
        int sy = source.y();
        int[][] deltas = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        java.util.List<Move> moves = new java.util.ArrayList<>();
        for (int[] d : deltas) {
            int dx = d[0];
            int dy = d[1];
            int bx, by;
            if (Math.abs(dx) == 2) {
                bx = sx + dx / 2;
                by = sy;
            } else {
                bx = sx;
                by = sy + dy / 2;
            }
            int tx = sx + dx;
            int ty = sy + dy;
            int size = game.getConfiguration().getSize();
            if (tx < 0 || tx >= size || ty < 0 || ty >= size) {
                continue;
            }
            if (bx < 0 || bx >= size || by < 0 || by >= size) {
                continue;
            }
            Place dest = new Place(tx, ty);
            if (game.getPiece(bx, by) != null) {
                continue;
            }
            moves.add(new Move(source, dest));
        }
        return moves.toArray(new Move[0]);
    }
}
