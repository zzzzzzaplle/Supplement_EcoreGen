public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int r, c;

    Direction(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public PositionOffset getOffset() { return new PositionOffset(r, c); }
    public int getRowOffset() { return r; }
    public int getColOffset() { return c; }
}
