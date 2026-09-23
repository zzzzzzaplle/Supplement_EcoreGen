/**
 * Interface for elements that can be rendered as a single character on the map.
 */
public interface MapElement {

    /**
     * Returns the single character representation of this map element.
     *
     * @return a char representing this element
     */
    char toSingleChar();
}
