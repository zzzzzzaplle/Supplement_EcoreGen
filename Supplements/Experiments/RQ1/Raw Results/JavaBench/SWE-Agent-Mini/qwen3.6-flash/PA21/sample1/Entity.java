/**
 * Abstract base class for entities on the game board.
 */
public abstract class Entity implements BoardElement {
    private EntityCell owner;

    /**
     * Creates a new Entity.
     */
    public Entity() {
    }

    /**
     * Returns the Unicode character representation.
     *
     * @return The Unicode character.
     */
    @Override
    public abstract char toUnicodeChar();

    /**
     * Returns the ASCII character representation.
     *
     * @return The ASCII character.
     */
    @Override
    public abstract char toASCIIChar();

    /**
     * Sets the owner of this entity.
     *
     * @param ownner The owner EntityCell.
     * @return The owner EntityCell.
     */
    public EntityCell setowner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }

    /**
     * Gets the owner of this entity.
     *
     * @return The owner EntityCell, or null if not owned by any cell.
     */
    public EntityCell getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this entity.
     *
     * @param owner The owner EntityCell.
     */
    public void setOwner(EntityCell owner) {
        this.owner = owner;
    }
}
