public abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public Entity(EntityCell owner) {
        this.owner = owner;
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setOwner(EntityCell newOwner) {
        EntityCell prev = this.owner;
        this.owner = newOwner;
        return prev;
    }

    public EntityCell setowner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }
}
