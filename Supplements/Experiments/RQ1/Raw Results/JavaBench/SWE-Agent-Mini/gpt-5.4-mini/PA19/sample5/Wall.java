public class Wall extends Cell {
    public Wall() {
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}
