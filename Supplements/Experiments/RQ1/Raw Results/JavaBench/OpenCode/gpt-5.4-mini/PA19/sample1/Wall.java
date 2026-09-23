public class Wall extends Cell {
    public Wall() {
        super();
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    public int getCoordRow() {
        return coord.row;
    }

    public int getCoordCol() {
        return coord.col;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public char toSingleChar() {
        return 'W';
    }
}
