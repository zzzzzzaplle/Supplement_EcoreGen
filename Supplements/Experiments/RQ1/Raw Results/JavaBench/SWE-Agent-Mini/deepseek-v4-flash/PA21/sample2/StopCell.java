/**
 * A special cell that can only contain a Player entity.
 */
public class StopCell extends EntityCell {

    public StopCell() {
    }

    public StopCell(final Position position) {
        super(position);
    }

    public StopCell(final Position position, final Player player) {
        super(position, player);
    }

    public void setEntity(final Entity newEntity) {
        super.setentity(newEntity);
    }

    public Entity setentity(final Entity newEntity) {
        return super.setentity(newEntity);
    }

    public Player setPlayer(final Player newPlayer) {
        final Entity oldEntity = getEntity();
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
