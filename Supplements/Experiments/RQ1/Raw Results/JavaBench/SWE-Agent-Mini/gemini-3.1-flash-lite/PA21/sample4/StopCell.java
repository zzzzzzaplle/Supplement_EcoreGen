public class StopCell extends EntityCell {
    public StopCell() {}
    public StopCell(Position position) { setPosition(position); }
    public StopCell(Position position, Player player) { setPosition(position); setEntity(player); }

    public Entity setentity(Entity newEntity) { return super.setentity(newEntity); }
    public Player setPlayer(Player newPlayer) { setEntity(newPlayer); return newPlayer; }

    /*---SNIPPET: StopCell.java---*/
    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '#';
    }
    /*---END SNIPPET---*/
}
