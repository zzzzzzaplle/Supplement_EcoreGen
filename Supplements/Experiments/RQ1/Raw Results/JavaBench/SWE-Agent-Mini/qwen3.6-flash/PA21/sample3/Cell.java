import java.util.Objects;

/**
 * Abstract base class for all cell types on the game board.
 */
public abstract class Cell implements BoardElement {
    private Position position;

    /**
     * Creates a default cell.
     */
    public Cell() {
    }

    /**
     * Creates a cell with the specified position.
     *
     * @param position The position of this cell.
     */
    public Cell(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    /**
     * Returns the position of this cell.
     *
     * @return The position of this cell.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position of this cell.
     *
     * @param position The position of this cell.
     */
    public void setPosition(Position position) {
        this.position = Objects.requireNonNull(position);
    }
}
