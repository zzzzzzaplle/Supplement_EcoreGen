/**
 * Gem entity that can be collected by the player.
 */
public class Gem extends Entity {

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
