public abstract class Entity implements BoardElement {
    protected EntityCell owner;

    protected Entity() {
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setOwner(EntityCell newOwner) {
        EntityCell old = this.owner;
        this.owner = newOwner;
        return old;
    }
}
