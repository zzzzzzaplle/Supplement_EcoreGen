public abstract class Cell implements BoardElement {
    private Position position;

    public Cell() {}
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
}
