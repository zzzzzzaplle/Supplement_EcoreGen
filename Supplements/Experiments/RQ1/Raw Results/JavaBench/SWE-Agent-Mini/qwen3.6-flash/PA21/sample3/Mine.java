/**
 * Mine entity that causes the player to lose a life when encountered.
 */
public class Mine extends Entity {

    /**
     * Creates a default Mine entity.
     */
    public Mine() {
    }

    @Override
    public char toUnicodeChar() {
        return '\u26A0';
    }

    @Override
    public char toASCIIChar() {
        return 'X';
    }
}
