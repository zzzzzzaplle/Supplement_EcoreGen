public class EntityCell extends Cell {

    private Entity entity;

    public EntityCell() {
    }

    public EntityCell(Position position) {
        super(position);
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        setEntity(entity);
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity setEntity(Entity newEntity) {
        Entity prev = this.entity;
        if (prev != null) {
            prev.setOwner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
        return prev;
    }

    public Entity setentity(Entity newEntity) {
        Entity p = this.entity;
        if (this.entity != null) {
            this.entity.setowner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setowner(this);
        }
        return p;
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
