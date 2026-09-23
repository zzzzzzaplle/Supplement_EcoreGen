import java.util.ArrayList;

class Knight extends Piece implements Cloneable {
    public Knight() {}

    public Knight(Player player) {
        this.player = player;
    }

    public char getLabel() {
        return 'K';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getSize();
        ArrayList<Move> moves = new ArrayList<>();
        int[][] offsets = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] offset : offsets) {
            int nx = source.x() + offset[0];
            int ny = source.y() + offset[1];
            if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                moves.add(new Move(source, new Place(nx, ny)));
            }
        }
        return moves.toArray(new Move[0]);
    }

    @Override
    public Knight clone() throws CloneNotSupportedException {
        return (Knight) super.clone();
    }
}
