/**
 * A cell that can contain an entity.
 */
public class EntityCell extends Cell {

    Entity entity;

    public EntityCell() {
    }

    public EntityCell(final Position position) {
        super(position);
    }

    public EntityCell(final Position position, final Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity setentity(final Entity newEntity) {
        final Entity oldEntity = this.entity;
        if (this.entity != null) {
            this.entity.setowner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return oldEntity;
    }

    public void setEntity(final Entity newEntity) {
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
