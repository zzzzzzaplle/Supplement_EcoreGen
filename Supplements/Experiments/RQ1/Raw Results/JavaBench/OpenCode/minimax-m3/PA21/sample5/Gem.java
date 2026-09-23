public class Gem extends Entity {

    public Gem() {
    }

    public Gem(EntityCell owner) {
        super(owner);
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
