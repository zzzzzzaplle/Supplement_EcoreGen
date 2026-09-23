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
        java.util.List<Move> moves = new java.util.ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            for (int step = 1; step < size; step++) {
                int nx = source.x() + dir[0] * step;
                int ny = source.y() + dir[1] * step;
                if (nx < 0 || ny < 0 || nx >= size || ny >= size) {
                    break;
                }
                moves.add(new Move(source, new Place(nx, ny)));
            }
        }
        return moves.toArray(new Move[0]);
    }
}
