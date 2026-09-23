public class StopCell extends EntityCell {
    public StopCell() {
    }

    public StopCell(Position position) {
        super(position);
    }

    public StopCell(Position position, Player newPlayer) {
        super(position, newPlayer);
    }

    public Entity setentity(Entity newEntity) {
        setEntity(newEntity);
        return newEntity;
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
