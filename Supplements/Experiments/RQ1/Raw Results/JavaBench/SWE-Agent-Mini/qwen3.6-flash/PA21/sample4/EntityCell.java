import java.util.Objects;

/**
 * A cell that can optionally contain an entity.
 */
public class EntityCell extends Cell {

    private Entity entity;

    public EntityCell() {
        super();
    }

    public EntityCell(Position position) {
        super(position);
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        setentity(entity);
    }

    /**
     * Gets the entity contained in this cell.
     *
     * @return the entity, or null if no entity is present.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Sets the entity in this cell and updates the entity's owner.
     *
     * @param newEntity the entity to set.
     * @return the previous entity.
     */
    public Entity setentity(Entity newEntity) {
        Entity prevEntity = this.entity;
        if (prevEntity != null) {
            prevEntity.setowner(null);
        }
        this.entity = newEntity;
        if (this.entity != null) {
            this.entity.setowner(this);
        }
        return prevEntity;
    }

    public void setEntity(Entity newEntity) {
        setentity(newEntity);
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
