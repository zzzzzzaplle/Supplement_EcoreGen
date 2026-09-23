import java.util.Objects;

public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        Piece destinationPiece = game.getPiece(move.getDestination());

        // Archer moves orthogonally
        int dx = Math.abs(move.getSource().x() - move.getDestination().x());
        int dy = Math.abs(move.getSource().y() - move.getDestination().y());

        // Must be a straight line (either horizontal or vertical)
        if (dx * dy != 0) {
            return false;
        }

        // Check the path between source and destination
        int stepX = move.getDestination().x() > move.getSource().x() ? 1 :
                    move.getDestination().x() < move.getSource().x() ? -1 : 0;
        int stepY = move.getDestination().y() > move.getSource().y() ? 1 :
                    move.getDestination().y() < move.getSource().y() ? -1 : 0;

        int currentX = move.getSource().x();
        int currentY = move.getSource().y();
        int piecesFound = 0;

        while (currentX != move.getDestination().x() || currentY != move.getDestination().y()) {
            currentX += stepX;
            currentY += stepY;
            Piece pieceOnPath = game.getPiece(currentX, currentY);
            if (pieceOnPath != null) {
                piecesFound++;
                if (piecesFound > 1) {
                    // More than one piece in the path - invalid
                    return false;
                }
            }
        }

        if (destinationPiece == null) {
            // Non-capturing move - path must be clear
            return piecesFound == 0;
        } else {
            // Capturing move - there must be exactly 1 piece between source and destination (the screen/mount)
            // The piece at the destination is the target, so we need exactly 1 piece before it
            return piecesFound == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
