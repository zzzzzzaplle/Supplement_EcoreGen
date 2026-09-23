public abstract class Entity implements BoardElement {
    private EntityCell owner;

    public Entity() {
    }

    public EntityCell getOwner() {
        return owner;
    }

    public EntityCell setowner(EntityCell owner) {
        EntityCell prev = this.owner;
        this.owner = owner;
        return prev;
    }

    public void setOwner(EntityCell owner) {
        setowner(owner);
    }

    @Override
    public char toUnicodeChar() {
        return '?';
    }

    @Override
    public char toASCIIChar() {
        return '?';
    }
}
