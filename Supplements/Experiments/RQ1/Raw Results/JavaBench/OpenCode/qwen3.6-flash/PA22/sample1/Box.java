class Box extends Entity {

    private int playerId;

    public Box() {
        this(0);
    }

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
