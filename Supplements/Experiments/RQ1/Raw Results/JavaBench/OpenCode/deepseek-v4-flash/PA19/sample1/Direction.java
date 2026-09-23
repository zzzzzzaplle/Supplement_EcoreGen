public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction getOpposite() {
        return Pa19Helper.getOpposite(this);
    }

    public Coordinate getOffset() {
        return Pa19Helper.getOffset(this);
    }
}
