public abstract class Cell implements BoardElement {
    private Position position;

    public Cell() {
    }

    public Cell(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public char toUnicodeChar() {
        return '?';
    }

    @Override
    public char toASCIIChar() {
        return '?';
    }
}
