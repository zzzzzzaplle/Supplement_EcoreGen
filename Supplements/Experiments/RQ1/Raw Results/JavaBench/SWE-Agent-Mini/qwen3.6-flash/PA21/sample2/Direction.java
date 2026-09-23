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
                return new PositionOffset(0, 0);
        }
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
