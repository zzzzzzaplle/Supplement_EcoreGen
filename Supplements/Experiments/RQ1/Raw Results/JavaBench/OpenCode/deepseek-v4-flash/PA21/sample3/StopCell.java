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
        Entity old = getEntity();
        if (newEntity instanceof Player || newEntity == null) {
            super.setentity(newEntity);
        }
        return old;
    }

    public Player setPlayer(Player newPlayer) {
        Player old = (Player) getEntity();
        setentity(newPlayer);
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
