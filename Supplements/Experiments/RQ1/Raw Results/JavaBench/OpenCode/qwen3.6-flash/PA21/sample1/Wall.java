public class Wall extends Cell {

    public Wall() {
    }

    public Wall(final Position position) {
        super(position);
    }

    public void setPosition(Position position) {
        this.position = position;
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
