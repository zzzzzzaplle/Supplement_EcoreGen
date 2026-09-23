/**
 * The player entity controlled by the user.
 */
public class Player extends Entity {
    public Player() {
    }
    public Player(Position position) {
        super(position);
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
