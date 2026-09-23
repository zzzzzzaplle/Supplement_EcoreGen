public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        return true;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
