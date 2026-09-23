public class Wall extends Cell {
    public Wall() {}
    public Wall(Position position) { super(position); }
    @Override
    public char toUnicodeChar() {
        return '\u2588';
    }

    @Override
    public char toASCIIChar() {
        return 'W';
    }
}
