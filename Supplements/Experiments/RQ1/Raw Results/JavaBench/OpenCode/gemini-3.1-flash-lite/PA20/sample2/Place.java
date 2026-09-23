public class Place implements Cloneable {
    private int x;
    private int y;

    public Place() {}
    public Place(int x, int y) { this.x = x; this.y = y; }

    @Override
    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return x == place.x && y == place.y;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
