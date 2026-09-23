public class StopCell extends EntityCell {
    public StopCell() {}
    public Entity setEntity(Entity newEntity) { return null; }
    public Player setPlayer(Player newPlayer) { return null; }
    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '#';
    }
}
