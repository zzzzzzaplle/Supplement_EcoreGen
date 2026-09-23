public class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        Piece piece = game.getPiece(move.getSource());
        if (piece == null) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}
