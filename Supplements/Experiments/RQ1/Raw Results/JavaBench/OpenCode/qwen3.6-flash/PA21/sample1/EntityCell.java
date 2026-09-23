public class EntityCell extends Cell {
    public Entity entity;

    public EntityCell() {
    }

    protected EntityCell(final Position position) {
        super(position);
    }

    protected EntityCell(final Position position, final Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity setEntity(final Entity newEntity) {
        Entity old = this.entity;
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
        return old;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Player setPlayer(final Player newPlayer) {
        Player old = (Player) getEntity();
        setEntity(newPlayer);
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
