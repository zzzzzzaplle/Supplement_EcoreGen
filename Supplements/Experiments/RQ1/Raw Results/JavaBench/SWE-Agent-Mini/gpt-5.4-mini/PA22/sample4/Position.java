public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }
}
