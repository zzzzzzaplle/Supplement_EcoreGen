public class Player extends Entity {

    public Player() {
    }

    public Player(final EntityCell owner) {
        setOwner(owner);
    }

    @Override
    public char toUnicodeChar() {
        return '\u25EF';
    }

    @Override
    public char toASCIIChar() {
        return '@';
    }
}
