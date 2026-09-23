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
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity setentity(Entity newEntity) {
        Entity old = this.entity;
        if (old != null) {
            old.setOwner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
        return old;
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
