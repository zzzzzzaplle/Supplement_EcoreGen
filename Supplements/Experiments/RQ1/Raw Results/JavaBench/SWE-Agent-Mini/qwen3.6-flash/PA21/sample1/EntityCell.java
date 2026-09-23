/**
 * A cell that can contain an Entity.
 */
public class EntityCell extends Cell {
    private Entity entity;

    /**
     * Creates a new EntityCell with the specified position and entity.
     *
     * @param position The position of this cell.
     * @param entity   The entity contained in this cell, or null.
     */
    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setowner(this);
        }
    }
    public EntityCell()
    {
    }
    /**
     * Creates a new EntityCell with the specified position and no entity.
     *
     * @param position The position of this cell.
     */
    public EntityCell(Position position) {
        super(position);
        this.entity = null;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }
    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }

    /**
     * Sets the entity in this cell.
     *
     * @param newEntity The new entity.
     * @return The previous entity, or null if there was none.
     */
    public Entity setentity(Entity newEntity) {
        Entity oldEntity = entity;
        if (entity != null) {
            entity.setowner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return oldEntity;
    }

    /**
     * Gets the entity contained in this cell.
     *
     * @return The entity, or null if empty.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Sets the entity contained in this cell.
     *
     * @param entity The entity.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            entity.setowner(this);
        }
    }
}
