public class VacantRule implements Rule {
    public VacantRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getPiece(move.getSource()) == null) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}
