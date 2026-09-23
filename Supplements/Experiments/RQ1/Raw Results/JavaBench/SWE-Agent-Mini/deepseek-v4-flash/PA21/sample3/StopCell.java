public class StopCell extends EntityCell {

    public StopCell() {
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Player player) {
        super(position, player);
    }

    public void setEntity(Entity newEntity) {
        super.setentity(newEntity);
    }

    public Entity setentity(Entity newEntity) {
        return super.setentity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        Entity oldEntity = getEntity();
        setEntity(newPlayer);
        return (Player) oldEntity;
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
