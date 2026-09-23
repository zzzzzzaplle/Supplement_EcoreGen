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
        // Knight moves in L-shape: (2,1) or (1,2) in any direction
        int[] dx = {2, 2, -2, -2, 1, 1, -1, -1};
        int[] dy = {1, -1, 1, -1, 2, -2, 2, -2};
        java.util.ArrayList<Move> moves = new java.util.ArrayList<>();
        int sx = source.x();
        int sy = source.y();
        for (int i = 0; i < dx.length; i++) {
            int nx = sx + dx[i];
            int ny = sy + dy[i];
            if (nx >= 0 && nx < game.getConfiguration().getSize() && ny >= 0 && ny < game.getConfiguration().getSize()) {
                Place dest = new Place(nx, ny);
                Move move = new Move(source, dest);
                // Check blocking square
                int bx, by;
                if (Math.abs(dx[i]) == 2) {
                    bx = (sx + nx) / 2;
                    by = sy;
                } else {
                    bx = sx;
                    by = (sy + ny) / 2;
                }
                Place blockPlace = new Place(bx, by);
                Piece blockPiece = game.getPiece(blockPlace);
                if (blockPiece != null) {
                    continue; // blocked
                }
                moves.add(move);
            }
        }
        return moves.toArray(new Move[0]);
    }
}
