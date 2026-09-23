public class ExtraLife extends Entity {

    public ExtraLife() {
        super();
    }

    public ExtraLife(EntityCell owner) {
        super(owner);
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
