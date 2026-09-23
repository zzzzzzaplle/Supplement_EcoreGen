import java.util.Objects;

/**
 * Abstract base class for all entities that can exist in an EntityCell.
 */
public abstract class Entity implements BoardElement {
    private EntityCell owner;

    /**
     * Creates a default entity.
     */
    public Entity() {
    }

    /**
     * Returns the owner EntityCell of this entity.
     *
     * @return The owner EntityCell, or null if not owned by any cell.
     */
    public EntityCell getOwner() {
        return owner;
    }

    /**
     * Sets the owner EntityCell of this entity.
     *
     * @param owner The owner EntityCell.
     */
    public void setOwner(EntityCell owner) {
        this.owner = owner;
    }

    public EntityCell setowner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }
}
