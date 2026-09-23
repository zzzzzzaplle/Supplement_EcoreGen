public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;


    public PositionOffset getOffset() {
        return switch (this) {
            case UP -> new PositionOffset(-1, 0);
            case DOWN -> new PositionOffset(1, 0);
            case LEFT -> new PositionOffset(0, -1);
            case RIGHT -> new PositionOffset(0, 1);
        };
    }

    public int getRowOffset() {
        return getOffset().getDRow();
    }

    public int getColOffset() {
        return getOffset().getDCol();
    }
}
