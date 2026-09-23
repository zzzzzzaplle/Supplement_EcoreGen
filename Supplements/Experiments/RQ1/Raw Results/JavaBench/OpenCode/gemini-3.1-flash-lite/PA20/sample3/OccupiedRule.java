public class OccupiedRule implements Rule {
    public OccupiedRule() {}
    public boolean validate(Game game, Move move) { return false; }
    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}