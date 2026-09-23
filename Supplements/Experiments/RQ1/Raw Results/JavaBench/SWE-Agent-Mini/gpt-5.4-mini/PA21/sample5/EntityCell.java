public class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {
    }

    public EntityCell(Position position) {
        super(position);
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    public Entity setentity(Entity newEntity) {
        setEntity(newEntity);
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
