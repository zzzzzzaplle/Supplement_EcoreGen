public class Archer extends Piece {

    public Archer() {
    }

    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        if (game == null || source == null) {
            return new Move[0];
        }
        int size = game.getConfiguration().getSize();
        java.util.ArrayList<Move> moves = new java.util.ArrayList<Move>();
        int[][] directions = new int[][] {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        for (int[] dir : directions) {
            int nx = source.x() + dir[0];
            int ny = source.y() + dir[1];
            while (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                moves.add(new Move(source, new Place(nx, ny)));
                nx += dir[0];
                ny += dir[1];
            }
        }
        return moves.toArray(new Move[0]);
    }
}
