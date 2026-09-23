class Box extends Entity {
    private int playerId;

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public Box() {
        this.playerId = 0;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
