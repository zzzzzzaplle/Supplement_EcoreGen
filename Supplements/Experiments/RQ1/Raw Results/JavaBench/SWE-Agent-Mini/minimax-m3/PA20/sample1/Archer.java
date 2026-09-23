public class Archer extends Piece {

    public Archer() {
        super();
    }

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        if (game == null || source == null) {
            return new Move[0];
        }
        int sx = source.x();
        int sy = source.y();
        int size = game.getConfiguration().getSize();
        java.util.List<Move> moves = new java.util.ArrayList<>();
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : dirs) {
            int dx = dir[0];
            int dy = dir[1];
            int step = 1;
            while (true) {
                int tx = sx + dx * step;
                int ty = sy + dy * step;
                if (tx < 0 || tx >= size || ty < 0 || ty >= size) {
                    break;
                }
                Place dest = new Place(tx, ty);
                moves.add(new Move(source, dest));
                step++;
            }
        }
        return moves.toArray(new Move[0]);
    }
}
