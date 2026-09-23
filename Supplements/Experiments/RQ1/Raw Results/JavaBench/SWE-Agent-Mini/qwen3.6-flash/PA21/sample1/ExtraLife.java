/**
 * An extra life entity that can be collected by the player.
 */
public class ExtraLife extends Entity {

    @Override
    public char toUnicodeChar() {
        return '\u2661';
    }

    @Override
    public char toASCIIChar() {
        return 'L';
    }
}
