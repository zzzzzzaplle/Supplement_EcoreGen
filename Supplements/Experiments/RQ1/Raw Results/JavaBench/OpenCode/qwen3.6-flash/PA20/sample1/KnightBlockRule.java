import java.util.Objects;

public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();

        // Determine the blocking square based on the direction
        Piece blockedPiece;
        if (Math.abs(dx) == 2) {
            // Moving 2 horizontally, 1 vertically - block at middle x
            int blockX = (move.getSource().x() + move.getDestination().x()) / 2;
            blockedPiece = game.getPiece(blockX, move.getSource().y());
        } else {
            // Moving 1 horizontally, 2 vertically - block at middle y
            int blockY = (move.getSource().y() + move.getDestination().y()) / 2;
            blockedPiece = game.getPiece(move.getSource().x(), blockY);
        }

        // The move is blocked if there's a piece in the blocking square
        return blockedPiece == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
