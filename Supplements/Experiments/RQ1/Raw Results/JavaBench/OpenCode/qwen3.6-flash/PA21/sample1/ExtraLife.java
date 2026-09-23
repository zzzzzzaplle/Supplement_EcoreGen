public class ExtraLife extends Entity {

    public ExtraLife() {
    }

    public ExtraLife(final EntityCell owner) {
        setOwner(owner);
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
