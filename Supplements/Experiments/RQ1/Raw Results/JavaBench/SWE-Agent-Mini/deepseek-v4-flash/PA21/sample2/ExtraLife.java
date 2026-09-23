/**
 * Extra life entity that can be collected by the player.
 */
public class ExtraLife extends Entity {

    public ExtraLife() {
    }

    @Override
    public char toUnicodeChar() {
        return '\u2661';
    }

    @Override
    public char toASCIIChar() {
        return 'L';
    }
}
