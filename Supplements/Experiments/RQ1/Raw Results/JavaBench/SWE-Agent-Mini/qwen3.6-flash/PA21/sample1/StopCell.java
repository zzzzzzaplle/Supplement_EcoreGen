/**
 * A special cell that can contain a Player entity.
 */
public class StopCell extends EntityCell {

    /**
     * Creates a new StopCell with the specified position and entity.
     *
     * @param position The position of this cell.
     * @param entity   The entity contained in this cell.
     */
    public StopCell(Position position, Entity entity) {
        super(position, entity);
    }
    public StopCell() {
    }
    /**
     * Creates a new StopCell with the specified position and no entity.
     *
     * @param position The position of this cell.
     */
    public StopCell(Position position) {
        super(position);
    }

  @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '#';
    }

    /**
     * Sets the entity in this cell.
     *
     * @param newEntity The new entity.
     * @return The previous entity.
     */
    @Override
    public Entity setentity(Entity newEntity) {
        return super.setentity(newEntity);
    }

    public void setEntity(Entity newEntity) {
        super.setentity(newEntity);
    }

    /**
     * Sets the player in this cell.
     *
     * @param newPlayer The new player.
     * @return The previous player.
     */
    public Player setPlayer(Player newPlayer) {
        return (Player) setentity(newPlayer);
    }
}
