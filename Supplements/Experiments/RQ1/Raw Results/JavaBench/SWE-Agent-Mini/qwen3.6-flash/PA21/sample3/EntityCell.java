import java.util.Objects;

/**
 * A cell type that can contain an {@link Entity}.
 */
public class EntityCell extends Cell {
    private Entity entity;

    /**
     * Creates a default entity cell.
     */
    public EntityCell() {
        super();
    }

    /**
     * Creates an entity cell with the specified position.
     *
     * @param position The position of this cell.
     */
    public EntityCell(Position position) {
        super(position);
    }

    /**
     * Creates an entity cell with the specified position and entity.
     *
     * @param position The position of this cell.
     * @param entity   The entity to place in this cell.
     */
    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    /**
     * Returns the entity contained in this cell.
     *
     * @return The entity, or null if this cell is empty.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Sets the entity in this cell.
     *
     * @param newEntity The new entity to place in this cell.
     */
    public void setEntity(Entity newEntity) {
        if (this.entity != null) {
            this.entity.setOwner(null);
        }
        this.entity = newEntity;
        if (this.entity != null) {
            this.entity.setOwner(this);
        }
    }

    /**
     * Sets the entity in this cell, returning the previous entity.
     *
     * @param newEntity The new entity to place in this cell.
     * @return The previous entity, or null if this cell was empty.
     */
    public Entity setentity(Entity newEntity) {
        Entity oldEntity = this.entity;
        if (this.entity != null) {
            this.entity.setowner(null);
        }
        this.entity = newEntity;
        if (this.entity != null) {
            this.entity.setowner(this);
        }
        return oldEntity;
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
