/**
 * ExtraLife entity that can be collected by the player to gain an additional life.
 */
public class ExtraLife extends Entity {

    /**
     * Creates a default ExtraLife entity.
     */
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
