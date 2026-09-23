public class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {}
    public EntityCell(Position position) { setPosition(position); }
    public EntityCell(Position position, Entity entity) { setPosition(position); this.entity = entity; }

    public Entity getEntity() { return entity; }
    public Entity setentity(Entity newEntity) {
        this.entity = newEntity;
        return newEntity;
    }
    public void setEntity(Entity newEntity) {
        this.entity = newEntity;
        if (newEntity != null) newEntity.setowner(this);
    }

    /*---SNIPPET: EntityCell.java---*/
    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }
    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }
    /*---END SNIPPET---*/
}
