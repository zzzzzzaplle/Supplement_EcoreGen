public abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {}

    public EntityCell getOwner() { return owner; }
    public EntityCell setowner(EntityCell owner) {
        EntityCell old = this.owner;
        this.owner = owner;
        return old;
    }
}
