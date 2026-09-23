/**
 * A cell representing a wall (border).
 */
public class Wall extends Cell {

    public Wall() {
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    /**
     * Returns the wall character.
     *
     * @return the wall character
     */
    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}
