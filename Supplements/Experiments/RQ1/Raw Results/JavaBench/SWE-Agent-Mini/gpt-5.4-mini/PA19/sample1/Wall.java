public class Wall extends Cell {
    public Wall() {
    }

    public Wall(Coordinate coord) {
        this.coord = coord;
    }

    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}
