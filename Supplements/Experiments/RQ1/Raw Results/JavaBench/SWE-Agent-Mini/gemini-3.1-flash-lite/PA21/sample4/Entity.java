public abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {}
    public EntityCell getOwner() { return owner; }
    public EntityCell setowner(EntityCell owner) {
        this.owner = owner;
        return owner;
    }
    public void setOwner(EntityCell owner) {
        this.owner = owner;
    }
}
