public class Gem extends Entity {

    public Gem() {
    }

    public Gem(final EntityCell owner) {
        setOwner(owner);
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
