import java.util.*;
import java.util.stream.*;

/**
 * Custom exception for configuration errors.
 * Included here to replace the missing assignment.protocol.exception.InvalidConfigurationError.
 */
class InvalidConfigurationError extends RuntimeException {
    public InvalidConfigurationError(String message) {
        super(message);
    }
}

enum Color {
    DEFAULT("\u001B[0m"), BLACK("\u001B[30m"), RED("\u001B[31m"), GREEN("\u001B[32m"), 
    YELLOW("\u001B[33m"), BLUE("\u001B[34m"), PURPLE("\u001B[35m"), CYAN("\u001B[36m"), WHITE("\u001B[37m");
    private final String ansiColor;
    Color(String ansiColor) { this.ansiColor = ansiColor; }
    public String toString() { return ansiColor; }
}

class Place implements Cloneable {
    private int x, y;
    public Place() {}
    public Place(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int x() { return x; }
    public int y() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public Place clone() { try { return (Place) super.clone(); } catch (CloneNotSupportedException e) { return null; } }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Place)) return false;
        Place place = (Place) o;
        return x == place.x && y == place.y;
    }
    @Override
    public int hashCode() { return Objects.hash(x, y); }
}

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
    @Override
    public Player clone() throws CloneNotSupportedException { return (Player) super.clone(); }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }
    @Override
    public int hashCode() { return Objects.hash(name); }
}

class ConsolePlayer extends Player {
    public ConsolePlayer() {}
    @Override
    public Move nextMove(Game game, Move[] availableMoves) { return null; /* Implementation dependent */ }
}

class RandomPlayer extends Player {
    public RandomPlayer() {}
    @Override
    public Move nextMove(Game game, Move[] availableMoves) { return null; /* Implementation dependent */ }
}

class Move implements Cloneable {
    private Place source;
    private Place destination;
    public Move() {}
    public Place getSource() { return source; }
    public void setSource(Place s) { this.source = s; }
    public Place getDestination() { return destination; }
    public void setDestination(Place d) { this.destination = d; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return source.equals(move.source) && destination.equals(move.destination);
    }
    @Override
    public int hashCode() { return Objects.hash(source, destination); }
    @Override
    public String toString() { return new StringJoiner(", ", Move.class.getSimpleName() + "[", "]").add("source=" + source).add("destination=" + destination).toString(); }
    @Override
    public Move clone() throws CloneNotSupportedException {
        Move cloned = (Move) super.clone();
        cloned.source = this.source.clone();
        cloned.destination = this.destination.clone();
        return cloned;
    }
}

abstract class Piece {
    private Player player;
    public Piece() {}
    public Player getPlayer() { return player; }
    public void setPlayer(Player p) { this.player = p; }
    public abstract char getLabel();
    public abstract Move[] getAvailableMoves(Game game, Place source);
}

class Knight extends Piece {
    public Knight() {}
    public char getLabel() { return 'K'; }
    public Move[] getAvailableMoves(Game game, Place source) { return new Move[0]; }
}

class Archer extends Piece {
    public Archer() {}
    public char getLabel() { return 'A'; }
    public Move[] getAvailableMoves(Game game, Place source) { return new Move[0]; }
}

class Configuration implements Cloneable {
    private int size;
    private Player[] players;
    private Piece[][] initialBoard;
    private Place centralPlace;
    private int numMovesProtection;
    public Configuration() {}
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public Player[] getPlayers() { return players; }
    public void setPlayers(Player[] p) { this.players = p; }
    public Piece[][] getInitialBoard() { return initialBoard; }
    public void setInitialBoard(Piece[][] b) { this.initialBoard = b; }
    public Place getCentralPlace() { return centralPlace; }
    public void setCentralPlace(Place p) { this.centralPlace = p; }
    public int getNumMovesProtection() { return numMovesProtection; }
    public void setNumMovesProtection(int n) { this.numMovesProtection = n; }
    public Configuration(int size, Player[] players, int numMovesProtection) {
        if (size < 3 || size % 2 != 1 || size > 25) throw new InvalidConfigurationError("Invalid size");
        this.size = size;
        this.players = players;
        if (players.length != 2) throw new InvalidConfigurationError("Must be 2 players");
        this.initialBoard = new Piece[size][size];
        this.centralPlace = new Place(size / 2, size / 2);
        this.numMovesProtection = numMovesProtection;
    }
    public Configuration(int size, Player[] players) { this(size, players, 0); }
    public void addInitialPiece(Piece piece, Place place) {
        if (place.x() >= this.size || place.y() >= this.size) throw new InvalidConfigurationError("Out of bounds");
        if (place.equals(this.centralPlace)) throw new InvalidConfigurationError("Cannot put at central");
        this.initialBoard[place.x()][place.y()] = piece;
    }
    public void addInitialPiece(Piece piece, int x, int y) { this.addInitialPiece(piece, new Place(x, y)); }
    public Configuration clone() throws CloneNotSupportedException {
        Configuration cloned = (Configuration) super.clone();
        cloned.players = this.players.clone();
        cloned.initialBoard = this.initialBoard.clone();
        for (int i = 0; i < this.size; i++) cloned.initialBoard[i] = this.initialBoard[i].clone();
        cloned.centralPlace = this.centralPlace.clone();
        return cloned;
    }
}

abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;
    public Configuration getConfiguration() { return configuration; }
    public void setConfiguration(Configuration c) { this.configuration = c; }
    public Piece[][] getBoard() { return board; }
    public void setBoard(Piece[][] b) { this.board = b; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(Player p) { this.currentPlayer = p; }
    public int getNumMoves() { return numMoves; }
    public void setNumMoves(int n) { this.numMoves = n; }
    public abstract Player start();
    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);
    public abstract void updateScore(Player player, Piece piece, Move move);
    public abstract void movePiece(Move move);
    public abstract Move[] getAvailableMoves(Player player);
    public Piece getPiece(Place place) { return this.board[place.x()][place.y()]; }
    public Piece getPiece(int x, int y) { return this.getPiece(new Place(x, y)); }
    public Place getCentralPlace() { return configuration.getCentralPlace(); }
    public void refreshOutput() { /* Provided implementation */ }
    public Game clone() throws CloneNotSupportedException {
        Game cloned = (Game) super.clone();
        cloned.configuration = this.configuration.clone();
        cloned.board = this.board.clone();
        for (int i = 0; i < this.configuration.getSize(); i++) {
            cloned.board[i] = this.board[i].clone();
            System.arraycopy(this.board[i], 0, cloned.board[i], 0, this.configuration.getSize());
        }
        cloned.currentPlayer = currentPlayer == null ? null : currentPlayer.clone();
        return cloned;
    }
}

class JesonMor extends Game {
    public JesonMor() {}
    public JesonMor(Configuration configuration) { this.configuration = configuration; }
    public Player start() { return null; }
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) { return null; }
    public void updateScore(Player player, Piece piece, Move move) {}
    public void movePiece(Move move) {}
    public Move[] getAvailableMoves(Player player) { return new Move[0]; }
}

interface Rule {
    boolean validate(Game game, Move move);
    String getDescription();
}

class VacantRule implements Rule {
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() { return "the source of move should have a piece"; }
}
class OccupiedRule implements Rule {
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() { return "piece cannot be captured by another piece belonging to the same player"; }
}
class OutOfBoundaryRule implements Rule {
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() { return "place is out of boundary of gameboard"; }
}
class NilMoveRule implements Rule {
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() { return "the source and destination of move should be different places"; }
}
class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;
    public int getNumProtectedMoves() { return numProtectedMoves; }
    public void setNumProtectedMoves(int n) { this.numProtectedMoves = n; }
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() { return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed"; }
}
class KnightMoveRule implements Rule {
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() { return "knight move rule is violated"; }
}
class KnightBlockRule implements Rule {
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() { return "knight is blocked by another piece"; }
}
class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) return true;
        return false;
    }
    @Override
    public String getDescription() { return "archer move rule is violated"; }
}