/**
 * Abstract base class for all cells on the game board.
 */
public abstract class Cell implements BoardElement {

    private Position position;

    public Cell() {
    }

    public Cell(final Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(final Position position) {
        this.position = position;
    }
}
