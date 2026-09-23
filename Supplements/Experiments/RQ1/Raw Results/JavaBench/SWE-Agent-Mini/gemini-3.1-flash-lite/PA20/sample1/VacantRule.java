public class VacantRule implements Rule {
    public VacantRule() {}
    @Override
    public boolean validate(Game game, Move move) { return true; }
    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}
