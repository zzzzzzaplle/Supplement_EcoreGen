public class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {}

    public Entity getEntity() {
        return entity;
    }

    public Entity setEntity(Entity entity) {
        this.entity = entity;
        return entity;
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
