public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        return false;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
