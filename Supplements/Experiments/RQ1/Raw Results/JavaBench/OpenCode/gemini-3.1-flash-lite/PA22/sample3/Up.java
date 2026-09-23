public class Up extends Move {
    public Up() {}

    public Up(int initiator) {
        super(initiator);
    }

    @Override
    public Position nextPosition(Position currentPosition) { return null; }
}
