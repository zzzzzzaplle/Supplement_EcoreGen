public class Wall extends Cell {
    public Wall() {
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return 'W';
    }
}
