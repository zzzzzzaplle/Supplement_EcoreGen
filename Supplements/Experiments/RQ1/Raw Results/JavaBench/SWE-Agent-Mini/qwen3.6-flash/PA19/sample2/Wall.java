public class Wall extends Cell {

    public Wall() {
    }

    public Wall(Coordinate coord) {
        super.coord = coord;
    }

    @Override
    public char toSingleChar() {
        return '#';
    }
}
