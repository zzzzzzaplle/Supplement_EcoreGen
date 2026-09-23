/**
 * Interface for elements that can be displayed on a board.
 */
public interface BoardElement {
    /**
     * Returns the Unicode character representation.
     *
     * @return Unicode character for this board element.
     */
    char toUnicodeChar();

    /**
     * Returns the ASCII character representation.
     *
     * @return ASCII character for this board element.
     */
    char toASCIIChar();
}
