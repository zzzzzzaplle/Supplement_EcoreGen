public class StopCell extends EntityCell {
    public StopCell() {}
    public StopCell(Position position) { super(position); }
    public StopCell(Position position, Player player) { super(position, player); }

    public Player setPlayer(Player player) {
        setEntity(player);
        return player;
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
