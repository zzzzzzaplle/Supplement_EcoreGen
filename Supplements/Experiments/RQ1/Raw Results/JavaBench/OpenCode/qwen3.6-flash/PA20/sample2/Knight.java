public class Knight extends Piece {
    @Override
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        int[][] offsets = {
            {-2, -1}, {2, -1}, {-2, 1}, {2, 1},
            {-1, -2}, {-1, 2}, {1, -2}, {1, 2}
        };
        int size = game.getConfiguration().getSize();
        java.util.ArrayList<Move> candidates = new java.util.ArrayList<>();
        for (int[] offset : offsets) {
            int newX = source.x() + offset[0];
            int newY = source.y() + offset[1];
            if (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                candidates.add(new Move(source, new Place(newX, newY)));
            }
        }
        return candidates.toArray(new Move[0]);
    }
}
