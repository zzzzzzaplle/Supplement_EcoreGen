import java.util.Objects;

public class FirstNMovesProtectionRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        int numMoves = game.getNumMoves();
        int numProtectedMoves = game.getConfiguration().getNumMovesProtection();

        if (numMoves >= numProtectedMoves) {
            return true;
        }

        // During protection, no captures allowed
        Piece destinationPiece = game.getPiece(move.getDestination());
        return destinationPiece == null;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + 0 + " moves are not allowed";
    }
}
