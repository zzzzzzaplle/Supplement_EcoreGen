public class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {}
    public EntityCell(Position position) { super(position); }
    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != ) entity.setOwner(this);
    }

    public Entity getEntity() { return entity; }
    public void setEntity(Entity newEntity) {
        this.entity = newEntity;
        if (newEntity != null) newEntity.setOwner(this);
    }
    public Entity setentity(Entity newEntity) {
        this.entity = newEntity;
        if (newEntity != null) newEntity.setOwner(this);
        return newEntity;
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
