import java.util.Objects;

public class Place implements Cloneable {
    private int xCoord;
    private int yCoord;

    public Place() {
    }

    public Place(int x, int y) {
        this.xCoord = x;
        this.yCoord = y;
    }

    public int x() {
        return this.xCoord;
    }

    public int y() {
        return this.yCoord;
    }

    public int getX() {
        return this.xCoord;
    }

    public void setX(int x) {
        this.xCoord = x;
    }

    public int getY() {
        return this.yCoord;
    }

    public void setY(int y) {
        this.yCoord = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return xCoord == place.xCoord && yCoord == place.yCoord;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xCoord, yCoord);
    }

    @Override
    public String toString() {
        return "(" + xCoord + ", " + yCoord + ")";
    }

    @Override
    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
    }
}
