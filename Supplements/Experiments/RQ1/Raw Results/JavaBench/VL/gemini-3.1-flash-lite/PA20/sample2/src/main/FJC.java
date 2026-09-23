import java.util.*;
import java.util.stream.Collectors;

class InvalidConfigurationError extends Error {
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

class Place {
    private int x, y;
    public Place() {}
    public Place(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    public Place clone() { return new Place(x, y); }
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
    @Override
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
    public Move nextMove(Game game, Move[] availableMoves) { return null; }
}

class RandomPlayer extends Player {
    public RandomPlayer() {}
    @Override
    public Move nextMove(Game game, Move[] availableMoves) { return null; }
}

class Move {
    private Place source;
    private Place destination;
    public Move() {}
    public Place getSource() { return source; }
    public void setSource(Place source) { this.source = source; }
    public Place getDestination() { return destination; }
    public void setDestination(Place destination) { this.destination = destination; }
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
    public String toString() {
        return new StringJoiner(", ", Move.class.getSimpleName() + "[", "]")
                .add("source=" + source)
                .add("destination=" + destination)
                .toString();
    }
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
    public void setPlayer(Player player) { this.player = player; }
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

class Configuration {
    private int size;
    private Player[] players;
    private Piece[][] initialBoard;
    private Place centralPlace;
    private int numMovesProtection;
    public Configuration() {}
    public Configuration(int size, Player[] players, int numMovesProtection) {
        if (size < 3 || size % 2 != 1 || size > 25) throw new InvalidConfigurationError("Invalid size");
        this.size = size;
        this.players = players;
        if (players.length != 2) throw new InvalidConfigurationError("Two players required");
        this.initialBoard = new Piece[size][size];
        this.centralPlace = new Place(size / 2, size / 2);
        this.numMovesProtection = numMovesProtection;
    }
    public Configuration(int size, Player[] players) { this(size, players, 0); }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public Player[] getPlayers() { return players; }
    public void setPlayers(Player[] players) { this.players = players; }
    public Piece[][] getInitialBoard() { return initialBoard; }
    public void setInitialBoard(Piece[][] initialBoard) { this.initialBoard = initialBoard; }
    public Place getCentralPlace() { return centralPlace; }
    public void setCentralPlace(Place centralPlace) { this.centralPlace = centralPlace; }
    public int getNumMovesProtection() { return numMovesProtection; }
    public void setNumMovesProtection(int numMovesProtection) { this.numMovesProtection = numMovesProtection; }
    public void addInitialPiece(Piece piece, Place place) {
        if (place.x() >= size || place.y() >= size || place.equals(centralPlace)) throw new InvalidConfigurationError("Invalid placement");
        initialBoard[place.x()][place.y()] = piece;
    }
    public void addInitialPiece(Piece piece, int x, int y) { addInitialPiece(piece, new Place(x, y)); }
    public Configuration clone() throws CloneNotSupportedException {
        Configuration cloned = (Configuration) super.clone();
        cloned.players = this.players.clone();
        cloned.initialBoard = new Piece[size][size];
        for(int i=0; i<size; i++) System.arraycopy(this.initialBoard[i], 0, cloned.initialBoard[i], 0, size);
        cloned.centralPlace = this.centralPlace.clone();
        return cloned;
    }
}

abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;
    public Game() {}
    public abstract Player start();
    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);
    public abstract void updateScore(Player player, Piece piece, Move move);
    public abstract void movePiece(Move move);
    public abstract Move[] getAvailableMoves(Player player);
    public Configuration getConfiguration() { return configuration; }
    public void setConfiguration(Configuration configuration) { this.configuration = configuration; }
    public Piece[][] getBoard() { return board; }
    public void setBoard(Piece[][] board) { this.board = board; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(Player currentPlayer) { this.currentPlayer = currentPlayer; }
    public int getNumMoves() { return numMoves; }
    public void setNumMoves(int numMoves) { this.numMoves = numMoves; }
    public Place getCentralPlace() { return configuration.getCentralPlace(); }
    public Piece getPiece(Place place) { return this.board[place.x()][place.y()]; }
    public Piece getPiece(int x, int y) { return this.getPiece(new Place(x, y)); }
    public void refreshOutput() {
        int size = this.configuration.getSize();
        ArrayList<List<String>> contents = new ArrayList<List<String>>();
        for (int row = size - 1; row >= 0; row--) {
            ArrayList<String> rowContent = new ArrayList<String>();
            for (int col = 0; col < size; col++) {
                Piece piece = this.getPiece(col, row);
                if (piece == null) {
                    if (this.getCentralPlace().equals(new Place(col, row))) rowContent.add("x");
                    else rowContent.add(".");
                } else {
                    Player player = piece.getPlayer();
                    rowContent.add(String.format("%s%c%s", player.getColor(), piece.getLabel(), Color.DEFAULT));
                }
            }
            contents.add(rowContent);
        }
        ArrayList<String> xCoordinates = new ArrayList<String>();
        for (int i = 0; i < size; i++) xCoordinates.add(String.valueOf((char) ('a' + i)));
        Collections.reverse(contents);
        System.out.print("\u001b[2J"); System.out.flush();
        System.out.println("\n### COMP3021 Programming Assignment 1 ###\n");
        System.out.println("Total Moves: " + this.numMoves);
        for (Player player : this.configuration.getPlayers()) System.out.printf("%s%s%s score: %d\n", player.getColor(), player.getName(), Color.DEFAULT, player.getScore());
    }
    @Override
    public Game clone() throws CloneNotSupportedException {
        Game cloned = (Game) super.clone();
        cloned.configuration = this.configuration.clone();
        cloned.board = new Piece[configuration.getSize()][configuration.getSize()];
        for (int i = 0; i < this.configuration.getSize(); i++) System.arraycopy(this.board[i], 0, cloned.board[i], 0, this.configuration.getSize());
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
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() { return "the source of move should have a piece"; }
}

class OccupiedRule implements Rule {
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() { return "piece cannot be captured by another piece belonging to the same player"; }
}

class OutOfBoundaryRule implements Rule {
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() { return "place is out of boundary of gameboard"; }
}

class NilMoveRule implements Rule {
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() { return "the source and destination of move should be different places"; }
}

class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;
    public int getNumProtectedMoves() { return numProtectedMoves; }
    public void setNumProtectedMoves(int numProtectedMoves) { this.numProtectedMoves = numProtectedMoves; }
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() { return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed"; }
}

class KnightMoveRule implements Rule {
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() { return "knight move rule is violated"; }
}

class KnightBlockRule implements Rule {
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() { return "knight is blocked by another piece"; }
}

class ArcherMoveRule implements Rule {
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) return true;
        return false;
    }
    public String getDescription() { return "archer move rule is violated"; }
}