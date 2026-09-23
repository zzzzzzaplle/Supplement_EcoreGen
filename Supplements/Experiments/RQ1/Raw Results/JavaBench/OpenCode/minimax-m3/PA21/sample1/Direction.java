public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public PositionOffset getOffset() {
        switch (this) {
            case UP:
                return new PositionOffset(-1, 0);
            case DOWN:
                return new PositionOffset(1, 0);
            case LEFT:
                return new PositionOffset(0, -1);
            case RIGHT:
                return new PositionOffset(0, 1);
            default:
                return null;
        }
    }

    public int getRowOffset() {
        return getOffset().getDRow();
    }

    public int getColOffset() {
        return getOffset().getDCol();
    }
}
