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


    public Player setPlayer(Player newPlayer) {
        Player old = null;
        Entity current = getEntity();
        if (current instanceof Player) {
            old = (Player) current;
        }
        setentity(newPlayer);
        return old;
    }

    public Entity setentity(Entity newEntity) {
        return super.setentity(newEntity);
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
