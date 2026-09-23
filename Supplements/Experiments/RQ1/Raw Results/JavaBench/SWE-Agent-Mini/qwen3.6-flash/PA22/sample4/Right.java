class Right extends Move {
    public Right(int initiator) {
        super(initiator);
    }

    public Right() {
        super();
    }

    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() + 1, currentPosition.y());
    }
}
