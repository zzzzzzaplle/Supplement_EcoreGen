public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final PositionOffset offset;

    Direction(int dRow, int dCol) {
        this.offset = new PositionOffset(dRow, dCol);
    }

    public PositionOffset getOffset() {
        return offset;
    }

    public int getRowOffset() {
        return offset.getdRow();
    }

    public int getColOffset() {
        return offset.getdCol();
    }
}
