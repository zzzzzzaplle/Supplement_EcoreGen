public class VacantRule implements Rule {

    public VacantRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null || move.getSource() == null) {
            return false;
        }
        return game.getPiece(move.getSource()) != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}
