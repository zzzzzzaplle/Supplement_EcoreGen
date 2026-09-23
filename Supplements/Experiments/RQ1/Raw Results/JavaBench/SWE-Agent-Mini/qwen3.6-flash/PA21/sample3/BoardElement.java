/**
 * Interface for board elements that can be rendered as characters.
 */
public interface BoardElement {
    /**
     * Returns the Unicode character representation of this board element.
     */
    char toUnicodeChar();

    /**
     * Returns the ASCII character representation of this board element.
     */
    char toASCIIChar();
}
