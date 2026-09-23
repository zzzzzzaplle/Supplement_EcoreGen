/**
 * Wall cell, not passable and rendered as a wall character.
 */
public class Wall extends Cell {

    public Wall() {
        super();
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}
