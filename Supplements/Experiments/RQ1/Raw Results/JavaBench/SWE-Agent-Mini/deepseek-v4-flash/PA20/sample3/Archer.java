import java.util.ArrayList;
import java.util.List;

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
        List<Move> moves = new ArrayList<>();
        int x = source.getX();
        int y = source.getY();
        int size = game.getConfiguration().getSize();

        // Four orthogonal directions: up, down, left, right
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int steps = 1;
            boolean foundScreen = false;

            while (true) {
                int destX = x + dx * steps;
                int destY = y + dy * steps;
                if (destX < 0 || destX >= size || destY < 0 || destY >= size) {
                    break;
                }
                Place destination = new Place(destX, destY);
                Piece pieceAtDest = game.getPiece(destination);

                if (!foundScreen) {
                    // No screen yet - path must be clear
                    if (pieceAtDest == null) {
                        // Non-capturing move: add if destination is empty
                        moves.add(new Move(source, destination));
                    } else {
                        // Found a screen piece
                        foundScreen = true;
                    }
                } else {
                    // Have a screen - looking for capture target
                    if (pieceAtDest != null) {
                        // Can capture if not friendly
                        if (!pieceAtDest.getPlayer().equals(this.getPlayer())) {
                            moves.add(new Move(source, destination));
                        }
                        break; // Can't go past this piece
                    }
                }
                steps++;
            }
        }
        return moves.toArray(new Move[0]);
    }
}
