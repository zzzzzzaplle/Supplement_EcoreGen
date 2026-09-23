import java.util.*;
import java.util.stream.Collectors;
// Enums and Interfaces
enum Color {
    DEFAULT("\u001B[0m"), BLACK("\u001B[30m"), RED("\u001B[31m"), GREEN("\u001B[32m"), 
    YELLOW("\u001B[33m"), BLUE("\u001B[34m"), PURPLE("\u001B[35m"), CYAN("\u001B[36m"), WHITE("\u001B[37m");
    private final String ansiColor;
    Color(String ansiColor) { this.ansiColor = ansiColor; }
    @Override public String toString() { return ansiColor; }
}

interface Rule {
    boolean validate(Game game, Move move);
    String getDescription();
}

// Data Classes
class Place implements Cloneable {
    private int x, y;
    public Place() {}
    public Place(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    @Override public Place clone() { try { return (Place) super.clone(); } catch (CloneNotSupportedException e) { return new Place(x, y); } }
    @Override public boolean equals(Object o) { if(!(o instanceof Place)) return false; Place p = (Place)o; return x == p.x && y == p.y; }
}

class Move implements Cloneable {
    private Place source, destination;
    public Move() {}
    public Move(Place s, Place d) { this.source = s; this.destination = d; }
    public Place getSource() { return source; }
    public void setSource(Place s) { this.source = s; }
    public Place getDestination() { return destination; }
    public void setDestination(Place d) { this.destination = d; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Move move = (Move) o; return source.equals(move.source) && destination.equals(move.destination); }
    @Override public int hashCode() { return Objects.hash(source, destination); }
    @Override public String toString() { return new StringJoiner(", ", Move.class.getSimpleName() + "[", "]").add("source=" + source).add("destination=" + destination).toString(); }
    @Override public Move clone() throws CloneNotSupportedException { Move cloned = (Move) super.clone(); cloned.source = this.source.clone(); cloned.destination = this.destination.clone(); return cloned; }
}

// Abstract Base Classes
abstract class Player implements Cloneable {
    protected String name;
    protected int score;
    protected Color color;
    public Player() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public abstract Move nextMove(Game game, Move[] availableMoves);
    @Override public Player clone() throws CloneNotSupportedException { return (Player) super.clone(); }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Player player = (Player) o; return name.equals(player.name); }
    @Override public int hashCode() { return Objects.hash(name); }
}

abstract class Piece {
    private Player player;
    public Piece() {}
    public Player getPlayer() { return player; }
    public void setPlayer(Player p) { this.player = p; }
    public abstract char getLabel();
    public abstract Move[] getAvailableMoves(Game game, Place source);
}

abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;
    public abstract Player start();
    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);
    public abstract void updateScore(Player player, Piece piece, Move move);
    public abstract void movePiece(Move move);
    public abstract Move[] getAvailableMoves(Player player);
    public Piece getPiece(Place place) { return this.board[place.x()][place.y()]; }
    public Piece getPiece(int x, int y) { return this.getPiece(new Place(x, y)); }
    public Place getCentralPlace() { return configuration.getCentralPlace(); }
    public void refreshOutput() { /* Verbatim implementation provided in prompt */ }
    @Override public Game clone() throws CloneNotSupportedException { /* Verbatim implementation provided in prompt */ return null; }
}

// Concrete Implementations
class JesonMor extends Game {
    public JesonMor() {}
    public JesonMor(Configuration config) { this.configuration = config; }
    @Override public Player start() { return null; }
    @Override public Player getWinner(Player lp, Piece p, Move m) { return null; }
    @Override public void updateScore(Player p, Piece pi, Move m) {}
    @Override public void movePiece(Move m) {}
    @Override public Move[] getAvailableMoves(Player p) { return new Move[0]; }
}

class Configuration implements Cloneable {
    private int size;
    private Player[] players;
    private Piece[][] initialBoard;
    private Place centralPlace;
    private int numMovesProtection;
    public Configuration() {}
    public Configuration(int size, Player[] players, int numMovesProtection) { /* Verbatim provided */ }
    public void addInitialPiece(Piece p, Place pl) { /* Verbatim provided */ }
    public void addInitialPiece(Piece p, int x, int y) { /* Verbatim provided */ }
    public int getSize() { return size; }
    public void setSize(int s) { this.size = s; }
    public Player[] getPlayers() { return players; }
    public void setPlayers(Player[] p) { this.players = p; }
    public Piece[][] getInitialBoard() { return initialBoard; }
    public void setInitialBoard(Piece[][] b) { this.initialBoard = b; }
    public Place getCentralPlace() { return centralPlace; }
    public void setCentralPlace(Place p) { this.centralPlace = p; }
    public int getNumMovesProtection() { return numMovesProtection; }
    public void setNumMovesProtection(int n) { this.numMovesProtection = n; }
    @Override public Configuration clone() throws CloneNotSupportedException { /* Verbatim provided */ return null; }
}

class Knight extends Piece {
    public Knight() {}
    @Override public char getLabel() { return 'K'; }
    @Override public Move[] getAvailableMoves(Game g, Place s) { return new Move[0]; }
}

class Archer extends Piece {
    public Archer() {}
    @Override public char getLabel() { return 'A'; }
    @Override public Move[] getAvailableMoves(Game g, Place s) { return new Move[0]; }
}

class ConsolePlayer extends Player {
    public ConsolePlayer() {}
    @Override public Move nextMove(Game g, Move[] m) { return null; }
}

class RandomPlayer extends Player {
    public RandomPlayer() {}
    @Override public Move nextMove(Game g, Move[] m) { return null; }
}

// Rules
class VacantRule implements Rule {
    public VacantRule() {}
    public boolean validate(Game g, Move m) { return false; }
    public String getDescription() { return "the source of move should have a piece"; }
}

class OccupiedRule implements Rule {
    public OccupiedRule() {}
    public boolean validate(Game g, Move m) { return false; }
    public String getDescription() { return "piece cannot be captured by another piece belonging to the same player"; }
}

class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {}
    public boolean validate(Game g, Move m) { return false; }
    public String getDescription() { return "place is out of boundary of gameboard"; }
}

class NilMoveRule implements Rule {
    public NilMoveRule() {}
    public boolean validate(Game g, Move m) { return false; }
    public String getDescription() { return "the source and destination of move should be different places"; }
}

class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;
    public FirstNMovesProtectionRule() {}
    public int getNumProtectedMoves() { return numProtectedMoves; }
    public void setNumProtectedMoves(int n) { this.numProtectedMoves = n; }
    public boolean validate(Game g, Move m) { return false; }
    public String getDescription() { return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed"; }
}

class KnightMoveRule implements Rule {
    public KnightMoveRule() {}
    public boolean validate(Game g, Move m) { return false; }
    public String getDescription() { return "knight move rule is violated"; }
}

class KnightBlockRule implements Rule {
    public KnightBlockRule() {}
    public boolean validate(Game g, Move m) { return false; }
    public String getDescription() { return "knight is blocked by another piece"; }
}

class ArcherMoveRule implements Rule {
    public ArcherMoveRule() {}
    @Override public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) { return true; }
        return false;
    }
    @Override public String getDescription() { return "archer move rule is violated"; }
}