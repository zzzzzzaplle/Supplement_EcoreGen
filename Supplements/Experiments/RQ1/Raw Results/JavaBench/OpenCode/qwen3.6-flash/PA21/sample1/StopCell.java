public class StopCell extends EntityCell {

    public StopCell() {
    }

    public StopCell(final Position position) {
        super(position);
    }

    public StopCell(final Position position, final Entity entity) {
        super(position, entity);
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public Entity setentity(final Entity newEntity) {
        return super.setEntity(newEntity);
    }

    public Player setPlayer(final Player newPlayer) {
        Player old = (Player) getEntity();
        setEntity(newPlayer);
        return old;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '#';
    }
}
