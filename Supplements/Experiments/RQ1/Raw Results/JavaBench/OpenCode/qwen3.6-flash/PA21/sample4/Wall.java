public class Wall extends Cell {

    public Wall(Position position) {
        super(position);
    }

    public Wall() {
        super(null);
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
