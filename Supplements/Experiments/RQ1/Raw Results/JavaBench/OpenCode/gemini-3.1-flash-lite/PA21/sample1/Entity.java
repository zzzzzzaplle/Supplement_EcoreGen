public abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {}

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setOwner(EntityCell owner) {
        this.owner = owner;
        return owner;
    }
}
