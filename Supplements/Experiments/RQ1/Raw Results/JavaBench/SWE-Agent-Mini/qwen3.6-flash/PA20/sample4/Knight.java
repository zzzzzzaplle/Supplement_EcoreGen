public class Knight extends Piece implements Cloneable {
    public Knight() {
    }

    public char getLabel() {
        return 'K';
    }

    @Override
    public Knight clone() throws CloneNotSupportedException {
        return (Knight) super.clone();
    }
}
