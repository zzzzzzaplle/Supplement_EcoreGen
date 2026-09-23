/**
 * Wall cell rendering.
 */
public class Wall extends Cell {

    public Wall() {
        super();
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}
