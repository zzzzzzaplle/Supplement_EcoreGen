/**
 * Represents a wall cell on the map border.
 */
public class Wall extends Cell {

    public Wall() {
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}
