import java.util.*;

public class RandomPlayer extends Player {
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        Random random = new Random();
        return availableMoves[random.nextInt(availableMoves.length)];
    }
    
    public RandomPlayer() {
    }
}
