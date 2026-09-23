public class Player extends Entity {

    public Player() {
        super();
    }

    public Player(EntityCell owner) {
        super(owner);
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
