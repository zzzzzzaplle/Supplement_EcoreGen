public class StopCell extends EntityCell {
    public StopCell() {
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Entity entity) {
        super(position, entity);
    }

    public Entity setentity(Entity newEntity) {
        return super.setentity(newEntity);
    }

    public Player setPlayer(Player newPlayer) {
        setEntity(newPlayer);
        return newPlayer;
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
