/**
 * Player entity representing the human-controlled character.
 */
public class Player extends Entity {

    /**
     * Creates a default Player entity.
     */
    public Player() {
    }

    @Override
    public char toUnicodeChar() {
        return '\u25EF';
    }

    @Override
    public char toASCIIChar() {
        return '@';
    }
}
