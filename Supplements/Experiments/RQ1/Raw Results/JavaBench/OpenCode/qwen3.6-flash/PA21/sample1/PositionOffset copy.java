public class PositionOffset {
    private final int dRow;
    private final int dCol;

    public PositionOffset(final int dRow, final int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int getRowOffset() {
        return dRow;
    }

    public int getColOffset() {
        return dCol;
    }
}
