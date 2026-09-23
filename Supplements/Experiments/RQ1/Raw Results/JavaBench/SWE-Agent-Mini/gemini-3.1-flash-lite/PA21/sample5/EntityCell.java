public class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {}
    public EntityCell(Position position) { super(position); }
    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
    }

    public Entity getEntity() { return entity; }
    public Entity setentity(Entity newEntity) {
        Entity old = this.entity;
        this.entity = newEntity;
        if (newEntity != null) newEntity.setowner(this);
        return old;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }
    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }
}
