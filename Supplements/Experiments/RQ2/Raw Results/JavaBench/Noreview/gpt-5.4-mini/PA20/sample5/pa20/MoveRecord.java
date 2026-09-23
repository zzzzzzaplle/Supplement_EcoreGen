package edu.pa20;

import java.util.StringJoiner;

/**
 * 适配 EMF 体系的 MoveRecord 数据载体类
 */
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
        
        // 安全调用 EMF 对象的 equals（我们在 Ecore 中已经为其注入了穿透校验的逻辑）
        boolean playerEq = (player == null ? that.player == null : player.equals(that.player));
        boolean moveEq = (move == null ? that.move == null : move.equals(that.move));
        
        return playerEq && moveEq;
    }

    @Override
    public MoveRecord clone() throws CloneNotSupportedException {
        MoveRecord cloned = (MoveRecord) super.clone();
        
        // 安全调用 EMF 对象的 clone（我们在 Ecore 中已经使用 EcoreUtil.copy 进行了底层替换）
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
                .add("player=" + (player != null ? player.getName() : "null")) // 适配 EMF Getter
                .add("move=" + (move != null ? move.toString() : "null"))
                .toString();
    }
}