public class Player extends Entity {
    public Player() {}
    /*---SNIPPET: Player.java---*/
    @Override
    public char toUnicodeChar() {
        return '\u25EF';
    }

    @Override
    public char toASCIIChar() {
        return '@';
    }
    /*---END SNIPPET---*/
}
