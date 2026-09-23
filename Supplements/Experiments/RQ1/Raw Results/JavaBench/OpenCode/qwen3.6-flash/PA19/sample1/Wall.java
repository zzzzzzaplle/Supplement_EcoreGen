/**
 * A wall cell forming the outer border of the map.
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
