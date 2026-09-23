public class Mine extends Entity {

    public Mine() {
    }

    public Mine(EntityCell owner) {
        super(owner);
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
