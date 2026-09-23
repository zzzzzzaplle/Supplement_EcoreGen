public class VacantRule implements Rule {
    public VacantRule() {
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece == null) {
            return false;
        }
        return true;
    }
}
