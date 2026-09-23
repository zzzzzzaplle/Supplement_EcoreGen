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

    public EntityCell setOwner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }

    public EntityCell setowner(EntityCell owner) {
        EntityCell p = this.owner;
        this.owner = owner;
        return p;
    }
}
