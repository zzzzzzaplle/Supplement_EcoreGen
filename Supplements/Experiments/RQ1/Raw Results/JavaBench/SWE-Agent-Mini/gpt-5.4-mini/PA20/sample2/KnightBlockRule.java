public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        return true;
    }

public String getDescription() {
        return "knight is blocked by another piece";
    }
}
