public class EntityCell extends Cell {
    private Entity entity;

    public EntityCell() {
    }

    public EntityCell(Position position) {
        super(position);
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        if (this.entity != null && this.entity.getOwner() == this) {
            this.entity.setOwner(null);
        }
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    public Entity setentity(Entity newEntity) {
        Entity old = this.entity;
        if (this.entity != null && this.entity.getOwner() == this) {
            this.entity.setOwner(null);
        }
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
        return newEntity;
    }
}
