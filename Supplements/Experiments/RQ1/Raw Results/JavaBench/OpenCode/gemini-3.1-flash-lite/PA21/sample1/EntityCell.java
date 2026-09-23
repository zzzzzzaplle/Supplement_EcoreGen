public class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {}

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

    public Entity setEntity(Entity newEntity) {
        this.entity = newEntity;
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
