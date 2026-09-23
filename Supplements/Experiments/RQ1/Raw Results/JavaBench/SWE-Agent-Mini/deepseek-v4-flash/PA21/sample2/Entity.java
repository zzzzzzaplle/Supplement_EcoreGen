/**
 * Abstract base class for all entities that can be placed on entity cells.
 */
public abstract class Entity implements BoardElement {

    private EntityCell owner;

    public Entity() {
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setowner(final EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }

    public void setOwner(final EntityCell owner) {
        setowner(owner);
    }
}
