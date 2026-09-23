public class Gem extends Entity {
    public Gem() {}
    /*---SNIPPET: Gem.java---*/
    @Override
    public char toUnicodeChar() {
        return '\u25C7';
    }

    @Override
    public char toASCIIChar() {
        return '*';
    }
    /*---END SNIPPET---*/
}
