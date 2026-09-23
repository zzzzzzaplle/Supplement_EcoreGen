class Left extends Move {
    public Left(int initiator) {
        super(initiator);
    }

    public Left() {
        super();
    }

    public Position nextPosition(Position currentPosition) {
        return Position.of(currentPosition.x() - 1, currentPosition.y());
    }
}
