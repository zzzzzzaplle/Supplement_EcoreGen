public class StopCell extends EntityCell {

    public StopCell() {
        super();
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Entity entity) {
        super(position, entity);
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public Entity setEntity(Entity newEntity) {
        return super.setEntity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
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
