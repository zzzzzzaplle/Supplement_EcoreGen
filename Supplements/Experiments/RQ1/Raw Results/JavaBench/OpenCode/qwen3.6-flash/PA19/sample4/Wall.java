public class Wall extends Cell {
    public Wall(Coordinate coord) {
        super(coord);
    }
    public Wall() {
    }
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}
