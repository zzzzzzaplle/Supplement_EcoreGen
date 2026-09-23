/**
 * A mine entity that causes the player to die when stepped on.
 */
public class Mine extends Entity {
    public Mine() {
    public Mine(Position position) {
        super(position);
    }
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
