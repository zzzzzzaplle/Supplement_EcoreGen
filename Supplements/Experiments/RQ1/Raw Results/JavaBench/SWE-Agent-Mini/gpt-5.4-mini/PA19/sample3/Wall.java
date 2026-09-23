public class Wall extends Cell {
    public Wall() {
    }

    public Wall(Coordinate coord) {
        this.coord = coord;
    }

    public char toSingleChar() {
        return '#';
    }

    public Coordinate getCoord() { return coord; }
    public void setCoord(Coordinate coord) { this.coord = coord; }
}
