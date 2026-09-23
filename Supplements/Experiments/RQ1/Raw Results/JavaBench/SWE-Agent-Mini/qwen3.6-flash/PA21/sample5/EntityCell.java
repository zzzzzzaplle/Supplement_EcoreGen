import java.util.Objects;

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
        this.entity = entity;
        if (entity != null) {
            entity.setowner(this);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    protected Entity getRawEntity() {
        return entity;
    }

    public Entity setentity(Entity newEntity) {
        Entity oldEntity = this.entity;
        if (oldEntity != null) {
            oldEntity.setowner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return oldEntity;
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
