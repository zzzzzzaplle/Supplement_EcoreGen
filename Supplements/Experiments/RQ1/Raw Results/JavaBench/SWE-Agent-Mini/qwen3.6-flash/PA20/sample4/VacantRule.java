public class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece pieceAtSource = game.getPiece(move.getSource());
        return pieceAtSource != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}
