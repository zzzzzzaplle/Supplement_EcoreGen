public class Alive extends Valid {
    private java.util.List<Position> collectedGems;
    private java.util.List<Position> collectedExtraLives;

    public Alive() {
    }

    public java.util.List<Position> getCollectedGems() {
        return collectedGems;
    }

    public void setCollectedGems(java.util.List<Position> collectedGems) {
        this.collectedGems = collectedGems;
    }

    public java.util.List<Position> getCollectedExtraLives() {
        return collectedExtraLives;
    }

    public void setCollectedExtraLives(java.util.List<Position> collectedExtraLives) {
        this.collectedExtraLives = collectedExtraLives;
    }
}
