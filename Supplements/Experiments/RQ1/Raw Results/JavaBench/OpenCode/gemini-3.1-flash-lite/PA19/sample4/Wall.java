public class Wall extends Cell {
    public Wall() {}

    public Wall(Coordinate coord) {
        this.coord = coord;
    }

    @Override
    public char toSingleChar() {
        return 'W';
    }
}
