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

        // Orthogonal directions: up, down, left, right
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int curX = x + dx;
            int curY = y + dy;
            int piecesBetween = 0;

            while (curX >= 0 && curX < size && curY >= 0 && curY < size) {
                Place currentPlace = new Place(curX, curY);
                Piece pieceAtPlace = game.getPiece(currentPlace);

                if (pieceAtPlace == null) {
                    // Non-capturing move if no pieces in between
                    if (piecesBetween == 0) {
                        moves.add(new Move(source, new Place(curX, curY)));
                    }
                    // If piecesBetween > 0, this would be a capture move but no piece at destination
                    // Only capture if exactly one piece between and destination has an opponent piece
                } else {
                    piecesBetween++;
                    if (piecesBetween == 1) {
                        // This piece between source and destination - could be a screen for capture
                        // Don't add as move here, just continue to look beyond
                    } else if (piecesBetween == 2) {
                        // Second piece encountered, no further moves in this direction
                        break;
                    }
                }

                curX += dx;
                curY += dy;
            }
        }

        // Now add capturing moves: exactly one piece between source and destination
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int curX = x + dx;
            int curY = y + dy;
            boolean foundScreen = false;
            int screenX = -1, screenY = -1;

            while (curX >= 0 && curX < size && curY >= 0 && curY < size) {
                Place currentPlace = new Place(curX, curY);
                Piece pieceAtPlace = game.getPiece(currentPlace);

                if (!foundScreen) {
                    if (pieceAtPlace != null) {
                        foundScreen = true;
                        screenX = curX;
                        screenY = curY;
                    }
                } else {
                    if (pieceAtPlace != null) {
                        // This is the capture target
                        // Check if it's an opponent piece
                        moves.add(new Move(source, new Place(curX, curY)));
                        break; // Can only capture one piece with this screen
                    }
                }
                curX += dx;
                curY += dy;
            }
        }

        return moves.toArray(new Move[0]);
    }
}
