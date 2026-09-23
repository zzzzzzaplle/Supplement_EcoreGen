public class StopCell extends EntityCell {
    public StopCell() {
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Player player) {
        super(position, player);
    }

    public Entity setentity(Entity newEntity) {
        return super.setentity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        Entity old = getEntity();
        setentity(newPlayer);
        return (Player) old;
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
