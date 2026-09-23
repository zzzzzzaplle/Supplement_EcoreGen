import java.util.ArrayList;

class Archer extends Piece implements Cloneable {
    public Archer() {}

    public Archer(Player player) {
        this.player = player;
    }

    public char getLabel() {
        return 'A';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getSize();
        ArrayList<Move> moves = new ArrayList<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] d : directions) {
            for (int step = 1; step < size; step++) {
                int nx = source.x() + d[0] * step;
                int ny = source.y() + d[1] * step;
                if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                    moves.add(new Move(source, new Place(nx, ny)));
                } else {
                    break;
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    @Override
    public Archer clone() throws CloneNotSupportedException {
        return (Archer) super.clone();
    }
}
