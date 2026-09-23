/**
 * Interface for board elements that can be represented as characters.
 */
public interface BoardElement {
    /**
     * Returns the Unicode character representation of this board element.
     *
     * @return The Unicode character.
     */
    public char toUnicodeChar();

    /**
     * Returns the ASCII character representation of this board element.
     *
     * @return The ASCII character.
     */
    public char toASCIIChar();
}
