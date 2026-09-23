public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public PositionOffset getOffset() {
        return new PositionOffset(getRowOffset(), getColOffset());
    }

    public int getRowOffset() {
        return switch (this) {
            case UP -> -1;
            case DOWN -> 1;
            default -> 0;
        };
    }

    public int getColOffset() {
        return switch (this) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
    }
}
