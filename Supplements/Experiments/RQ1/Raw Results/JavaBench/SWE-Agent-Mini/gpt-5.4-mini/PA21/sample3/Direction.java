public enum Direction {
    UP(new PositionOffset(-1, 0)),
    DOWN(new PositionOffset(1, 0)),
    LEFT(new PositionOffset(0, -1)),
    RIGHT(new PositionOffset(0, 1));

    private PositionOffset offset;

    Direction(PositionOffset offset) {
        this.offset = offset;
    }

   

    public PositionOffset getOffset() {
        return offset;
    }

    public void setOffset(PositionOffset offset) {
        this.offset = offset;
    }

    public int getRowOffset() {
        return offset.getDRow();
    }

    public int getColOffset() {
        return offset.getDCol();
    }
}
