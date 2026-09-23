import java.util.Random;

public class RandomPlayer extends Player {
    private transient Random random;
    
    public RandomPlayer() {
        this.random = new Random();
    }
    
    public RandomPlayer(String name, Color color) {
        this();
        this.name = name;
        this.color = color;
        this.score = 0;
    }
    
    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        
        int index = random.nextInt(availableMoves.length);
        try {
            return (Move) availableMoves[index].clone();
        } catch (CloneNotSupportedException e) {
            return availableMoves[index];
        }
    }
    
    @Override
    public Player clone() throws CloneNotSupportedException {
        RandomPlayer cloned = (RandomPlayer) super.clone();
        cloned.random = new Random();
        return cloned;
    }
}
