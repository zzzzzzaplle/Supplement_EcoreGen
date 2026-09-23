/**
 * Abstract base class for cells on the game board.
 */
public abstract class Cell implements BoardElement {
    private Position position;

    /**
     * Creates a new Cell with the specified position.
     *
     * @param position The position of this cell.
     */
    public Cell(Position position) {
        this.position = position;
    }

    /**
     * Gets the position of this cell.
     *
     * @return The position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position of this cell.
     *
     * @param position The position.
     */
    public void setPosition(Position position) {
        this.position = position;
    }
}
