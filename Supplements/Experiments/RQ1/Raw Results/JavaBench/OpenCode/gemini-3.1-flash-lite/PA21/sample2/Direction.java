public enum Direction {
    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);

    private final PositionOffset offset;

    Direction(int r, int c) { this.offset = new PositionOffset(r, c); }

    public PositionOffset getOffset() { return offset; }
    public int getRowOffset() { return offset.getDRow(); }
    public int getColOffset() { return offset.getDCol(); }
}
