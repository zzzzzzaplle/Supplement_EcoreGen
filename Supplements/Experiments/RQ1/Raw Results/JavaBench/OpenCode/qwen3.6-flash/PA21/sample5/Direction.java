public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final PositionOffset offset;
    private final int rowOffset;
    private final int colOffset;

    Direction(int rowOffset, int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
        this.offset = new PositionOffset(rowOffset, colOffset);
    }

    public PositionOffset getOffset() {
        return offset;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public int getColOffset() {
        return colOffset;
    }
}
