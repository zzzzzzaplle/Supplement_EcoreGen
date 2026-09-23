public class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {}
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
