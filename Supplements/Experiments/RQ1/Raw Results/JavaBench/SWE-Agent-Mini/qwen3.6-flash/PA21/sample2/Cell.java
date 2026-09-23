import java.util.Objects;

public abstract class Cell implements BoardElement {
    private Position position;

    public Cell() {
    }

    public Cell(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = Objects.requireNonNull(position);
    }
}
