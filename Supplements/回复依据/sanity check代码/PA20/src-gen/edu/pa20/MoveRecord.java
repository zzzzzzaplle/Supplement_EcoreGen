package edu.pa20;

import java.util.StringJoiner;

public class MoveRecord implements Cloneable {
    private Player player;
    private Move move;

    public MoveRecord(Player player, Move move) {
        this.player = player;
        this.move = move;
    }

    public Player getPlayer() {
        return player;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveRecord that = (MoveRecord) o;
        
        boolean playerEq = (player == null ? that.player == null : player.equals(that.player));
        boolean moveEq = (move == null ? that.move == null : move.equals(that.move));
        
        return playerEq && moveEq;
    }

    @Override
    public MoveRecord clone() throws CloneNotSupportedException {
        MoveRecord cloned = (MoveRecord) super.clone();
        
        if (this.player != null) {
            cloned.player = this.player.clone();
        }
        if (this.move != null) {
            cloned.move = this.move.clone();
        }
        return cloned;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MoveRecord.class.getSimpleName() + "[", "]")
                .add("player=" + (player != null ? player.getName() : "null"))
                .add("move=" + (move != null ? move.toString() : "null"))
                .toString();
    }
}