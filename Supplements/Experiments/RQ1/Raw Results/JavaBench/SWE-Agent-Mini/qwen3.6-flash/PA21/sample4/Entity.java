import java.util.Objects;

/**
 * Abstract base class for entities on the game board.
 */
public abstract class Entity implements BoardElement {

    private EntityCell owner;

    public Entity() {
    }

    /**
     * Gets the owner EntityCell.
     *
     * @return the owner EntityCell.
     */
    public EntityCell getOwner() {
        return owner;
    }

    /**
     * Sets the owner EntityCell and updates the owner's entity reference.
     *
     * @param owner the owner EntityCell.
     * @return this Entity.
     */
    public EntityCell setowner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }

    public void setOwner(EntityCell owner) {
        setowner(owner);
    }
}
