public class Mine extends Entity {
    public Mine() {}
    /*---SNIPPET: Mine.java---*/
    @Override
    public char toUnicodeChar() {
        return '\u26A0';
    }

    @Override
    public char toASCIIChar() {
        return 'X';
    }
    /*---END SNIPPET---*/
}
