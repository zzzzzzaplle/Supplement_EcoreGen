public class Wall extends Cell {
    public Wall() {}
    public Wall(Position pos) { setPosition(pos); }

    /*---SNIPPET: Wall.java---*/
    @Override
    public char toUnicodeChar() {
        return '\u2588';
    }

    @Override
    public char toASCIIChar() {
        return 'W';
    }
    /*---END SNIPPET---*/
}
