/**
 * A wall cell that blocks movement.
 */
public class Wall extends Cell {

    /**
     * Creates a new Wall with the specified position.
     *
     * @param position The position of this wall.
     */
    public Wall(Position position) {
        super(position);
    }
    
    public Wall() {
    }
    @Override
    public char toUnicodeChar() {
        return '\u2588';
    }

    @Override
    public char toASCIIChar() {
        return 'W';
    }
}
