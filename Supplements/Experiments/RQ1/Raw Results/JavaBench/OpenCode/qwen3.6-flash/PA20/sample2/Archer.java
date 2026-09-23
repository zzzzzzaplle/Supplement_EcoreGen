public class Archer extends Piece {
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };
        java.util.ArrayList<Move> candidates = new java.util.ArrayList<>();
        int size = game.getConfiguration().getSize();
        for (int[] dir : directions) {
            int newX = source.x() + dir[0];
            int newY = source.y() + dir[1];
            while (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                candidates.add(new Move(source, new Place(newX, newY)));
                Piece dest = game.getPiece(new Place(newX, newY));
                if (dest != null) {
                    break;
                }
                newX += dir[0];
                newY += dir[1];
            }
        }
        return candidates.toArray(new Move[0]);
    }
}
