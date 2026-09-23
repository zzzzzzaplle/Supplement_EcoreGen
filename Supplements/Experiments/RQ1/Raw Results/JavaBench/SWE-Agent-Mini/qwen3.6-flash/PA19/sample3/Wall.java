/**
 * A wall cell that blocks passage.
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
