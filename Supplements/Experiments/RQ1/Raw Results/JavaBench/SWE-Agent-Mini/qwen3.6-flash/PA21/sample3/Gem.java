/**
 * Gem entity that can be collected by the player for scoring points.
 */
public class Gem extends Entity {

    /**
     * Creates a default Gem entity.
     */
    public Gem() {
    }

    @Override
    public char toUnicodeChar() {
        return '\u25C7';
    }

    @Override
    public char toASCIIChar() {
        return '*';
    }
}
