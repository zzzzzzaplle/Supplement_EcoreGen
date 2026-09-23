public abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public EntityCell setOwner(EntityCell owner) {
        EntityCell old = this.owner;
        this.owner = owner;
        return old;
    }

    public EntityCell setowner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }

    public EntityCell getOwner() {
        return owner;
    }
}
