public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public PositionOffset getOffset() {
        return new PositionOffset(getRowOffset(), getColOffset());
    }

    public int getRowOffset() {
        switch (this) {
            case UP:
                return -1;
            case DOWN:
                return 1;
            default:
                return 0;
        }
    }

    public int getColOffset() {
        switch (this) {
            case LEFT:
                return -1;
            case RIGHT:
                return 1;
            default:
                return 0;
        }
    }
}
