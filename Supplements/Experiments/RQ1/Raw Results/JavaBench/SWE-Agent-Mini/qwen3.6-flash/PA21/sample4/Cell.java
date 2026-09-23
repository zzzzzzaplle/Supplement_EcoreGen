import java.util.Objects;

/**
 * Abstract base class for cells on the game board.
 */
public abstract class Cell implements BoardElement {

    private Position position;

    public Cell() {
    }

    public Cell(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    /**
     * Gets the position of this cell.
     *
     * @return the position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position of this cell.
     *
     * @param position the position.
     */
    public void setPosition(Position position) {
        this.position = position;
    }
}
