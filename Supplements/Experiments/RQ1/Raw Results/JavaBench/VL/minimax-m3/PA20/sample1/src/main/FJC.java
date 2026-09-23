import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Custom error class for invalid configuration parameters.
 */
class InvalidConfigurationError extends Error {
    public InvalidConfigurationError(String message) {
        super(message);
    }
}

/**
 * Represents a coordinate on the board (x = column, y = row, both 0-based from bottom-left).
 */
class Place implements Cloneable {
    private int x;
    private int y;

    public Place() {
        this.x = 0;
        this.y = 0;
    }

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return x == place.x && y == place.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Place.class.getSimpleName() + "[", "]")
                .add("x=" + x)
                .add("y=" + y)
                .toString();
    }

    @Override
    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
    }
}

/**
 * Represents a move from a source place to a destination place.
 */
class Move implements Cloneable {
    private Place source;
    private Place destination;

    public Move() {
        this.source = new Place();
        this.destination = new Place();
    }

    public Move(Place source, Place destination) {
        this.source = source;
        this.destination = destination;
    }

    public Place getSource() {
        return this.source;
    }

    public void setSource(Place source) {
        this.source = source;
    }

    public Place getDestination() {
        return this.destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return source.equals(move.source) &&
                destination.equals(move.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Move.class.getSimpleName() + "[", "]")
                .add("source=" + source)
                .add("destination=" + destination)
                .toString();
    }

    @Override
    public Move clone() throws CloneNotSupportedException {
        Move cloned = (Move) super.clone();
        cloned.source = this.source.clone();
        cloned.destination = this.destination.clone();
        return cloned;
    }
}

/**
 * Enum representing colors for the players and pieces.
 */
enum Color {
    DEFAULT,
    BLACK,
    RED,
    GREEN,
    YELLOW,
    BLUE,
    PURPLE,
    CYAN,
    WHITE;

    private String ansiColor;

    Color() {
        this.ansiColor = "";
    }

    public String getAnsiColor() {
        return this.ansiColor;
    }

    public void setAnsiColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    @Override
    public String toString() {
        return this.ansiColor;
    }
}

/**
 * Abstract class representing a player in the game.
 */
abstract class Player implements Cloneable {
    protected String name;
    protected int score;
    protected Color color;

    public Player() {
        this.name = "";
        this.score = 0;
        this.color = Color.DEFAULT;
    }

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract Move nextMove(Game game, Move[] availableMoves);

    @Override
    public Player clone() throws CloneNotSupportedException {
        return (Player) super.clone();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

/**
 * A player that makes moves via console input.
 */
class ConsolePlayer extends Player {

    public ConsolePlayer() {
        super();
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // TODO: Implement console input for move selection
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        return availableMoves[0];
    }
}

/**
 * A player that makes random moves.
 */
class RandomPlayer extends Player {
    private java.util.Random random;

    public RandomPlayer() {
        super();
        this.random = new java.util.Random();
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
        this.random = new java.util.Random();
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        return availableMoves[random.nextInt(availableMoves.length)];
    }
}

/**
 * Abstract class representing a piece on the board.
 */
abstract class Piece implements Cloneable {
    private Player player;

    public Piece() {
        this.player = null;
    }

    public Piece(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract char getLabel();

    public abstract Move[] getAvailableMoves(Game game, Place source);

    @Override
    public Piece clone() throws CloneNotSupportedException {
        return (Piece) super.clone();
    }
}

/**
 * Knight piece - moves in L-shape with blocking constraints.
 */
class Knight extends Piece {

    public Knight() {
        super();
    }

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        // TODO: Implement knight move generation
        return new Move[0];
    }
}

/**
 * Archer piece - moves orthogonally with cannon-like capture mechanic.
 */
class Archer extends Piece {

    public Archer() {
        super();
    }

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        // TODO: Implement archer move generation
        return new Move[0];
    }
}

/**
 * Configuration class containing all game initialization parameters.
 */
class Configuration implements Cloneable {
    private int size;
    private Player[] players;
    private Piece[][] initialBoard;
    private Place centralPlace;
    private int numMovesProtection;

    public Configuration() {
        this.size = 3;
        this.players = new Player[0];
        this.initialBoard = new Piece[0][];
        this.centralPlace = new Place();
        this.numMovesProtection = 0;
    }

    /**
     * Constructor of configuration
     *
     * @param size size of the gameboard.
     * @param players an array of two players in the game, the first player should move first when game starts.
     * @param numMovesProtection the first number of moves where capturing pieces is not allowed
     */
    public Configuration(int size, Player[] players, int numMovesProtection) {
        // validate size
        if (size < 3) {
            throw new InvalidConfigurationError("size of gameboard must be at least 3");
        }
        if (size % 2 != 1) {
            throw new InvalidConfigurationError("size of gameboard must be an odd number");
        }
        if (size > 25) {
            throw new InvalidConfigurationError("size of gameboard is at most 25");
        }
        this.size = size;
        // We only have 2 players
        this.players = players;
        if (players.length != 2) {
            throw new InvalidConfigurationError("there must be exactly two players");
        }
        // initialize map of the game board by putting every place null (meaning no piece)
        this.initialBoard = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.initialBoard[x] = new Piece[size];
            for (int y = 0; y < size; y++) {
                this.initialBoard[x][y] = null;
            }
        }
        // calculate the central place
        this.centralPlace = new Place(size / 2, size / 2);

        if (numMovesProtection < 0) {
            throw new InvalidConfigurationError("number of moves with capture protection cannot be negative");
        }
        this.numMovesProtection = numMovesProtection;
    }

    public Configuration(int size, Player[] players) {
        this(size, players, 0);
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public Piece[][] getInitialBoard() {
        return this.initialBoard;
    }

    public void setInitialBoard(Piece[][] initialBoard) {
        this.initialBoard = initialBoard;
    }

    public Place getCentralPlace() {
        return this.centralPlace;
    }

    public void setCentralPlace(Place centralPlace) {
        this.centralPlace = centralPlace;
    }

    public int getNumMovesProtection() {
        return this.numMovesProtection;
    }

    public void setNumMovesProtection(int numMovesProtection) {
        this.numMovesProtection = numMovesProtection;
    }

    /**
     * Add piece to the initial gameboard.
     * The player that this piece belongs to will be automatically added into the configuration.
     *
     * @param piece piece to be added
     * @param place place to put the piece
     */
    public void addInitialPiece(Piece piece, Place place) {
        if (!piece.getPlayer().equals(this.players[0]) && !piece.getPlayer().equals(this.players[1])) {
            throw new InvalidConfigurationError("the player of the piece is unknown");
        }
        if (place.x() >= this.size || place.y() >= this.size) {
            // The place must be inside the gameboard
            throw new InvalidConfigurationError("the place" + place.toString() + " must be inside the gameboard");
        }
        if (place.equals(this.centralPlace)) {
            throw new InvalidConfigurationError("piece cannot be put at central place initially");
        }

        // put the piece on the initial board
        this.initialBoard[place.x()][place.y()] = piece;
    }

    public void addInitialPiece(Piece piece, int x, int y) {
        this.addInitialPiece(piece, new Place(x, y));
    }

    @Override
    public Configuration clone() throws CloneNotSupportedException {
        Configuration cloned = (Configuration) super.clone();
        cloned.players = this.players.clone();
        for (int i = 0; i < this.players.length; i++) {
            cloned.players[i] = this.players[i].clone();
        }
        cloned.initialBoard = this.initialBoard.clone();
        for (int i = 0; i < this.size; i++) {
            cloned.initialBoard[i] = this.initialBoard[i].clone();
            // no need to deep copy piece
            System.arraycopy(this.initialBoard[i], 0, cloned.initialBoard[i], 0, this.size);
        }
        cloned.centralPlace = this.centralPlace.clone();
        return cloned;
    }
}

/**
 * Interface for game rules.
 */
interface Rule {
    boolean validate(Game game, Move move);

    String getDescription();
}

/**
 * Rule: source must not be vacant.
 */
class VacantRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        return game.getPiece(move.getSource()) != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}

/**
 * Rule: destination cannot contain a friendly piece.
 */
class OccupiedRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        Piece destPiece = game.getPiece(move.getDestination());
        if (sourcePiece == null) {
            return true;
        }
        if (destPiece == null) {
            return true;
        }
        return !sourcePiece.getPlayer().equals(destPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}

/**
 * Rule: source and destination must be within board limits.
 */
class OutOfBoundaryRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        return sx >= 0 && sx < size && sy >= 0 && sy < size
                && dx >= 0 && dx < size && dy >= 0 && dy < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

/**
 * Rule: source and destination must differ.
 */
class NilMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        return !move.getSource().equals(move.getDestination());
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}

/**
 * Rule: no piece can be captured during the first N moves.
 */
class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule() {
        this.numProtectedMoves = 0;
    }

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    public int getNumProtectedMoves() {
        return this.numProtectedMoves;
    }

    public void setNumProtectedMoves(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() >= this.numProtectedMoves) {
            return true;
        }
        Piece destPiece = game.getPiece(move.getDestination());
        return destPiece == null;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}

/**
 * Rule: Knight must move in L-shape.
 */
class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}

/**
 * Rule: Knight can be blocked by an adjacent piece.
 */
class KnightBlockRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        int absDx = Math.abs(dx - sx);
        int absDy = Math.abs(dy - sy);
        if (!((absDx == 2 && absDy == 1) || (absDx == 1 && absDy == 2))) {
            return true;
        }
        int blockX, blockY;
        if (absDx == 2) {
            blockX = (sx + dx) / 2;
            blockY = sy;
        } else {
            blockX = sx;
            blockY = (sy + dy) / 2;
        }
        return game.getPiece(blockX, blockY) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}

/**
 * Rule: Archer must move orthogonally.
 */
class ArcherMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        // TODO implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}

/**
 * Abstract class representing the game itself.
 */
abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;

    public Game() {
        this.configuration = new Configuration();
        this.board = new Piece[0][];
        this.currentPlayer = null;
        this.numMoves = 0;
    }

    public Game(Configuration configuration) {
        this.configuration = configuration;
        this.board = new Piece[configuration.getSize()][configuration.getSize()];
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Piece[][] getBoard() {
        return this.board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getNumMoves() {
        return this.numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public Piece getPiece(Place place) {
        return this.board[place.x()][place.y()];
    }

    public Piece getPiece(int x, int y) {
        return this.getPiece(new Place(x, y));
    }

    public void setPiece(Place place, Piece piece) {
        this.board[place.x()][place.y()] = piece;
    }

    public Place getCentralPlace() {
        return this.configuration.getCentralPlace();
    }

    public abstract Player start();

    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);

    public abstract void updateScore(Player player, Piece piece, Move move);

    public abstract void movePiece(Move move);

    public abstract Move[] getAvailableMoves(Player player);

    public void refreshOutput() {
        int size = this.configuration.getSize();
        ArrayList<List<String>> contents = new ArrayList<List<String>>();
        for (int row = size - 1; row >= 0; row--) {
            ArrayList<String> rowContent = new ArrayList<String>();
            for (int col = 0; col < size; col++) {
                Piece piece = this.getPiece(col, row);
                if (piece == null) {
                    if (this.getCentralPlace().equals(new Place(col, row))) {
                        rowContent.add("x");
                    } else {
                        rowContent.add(".");
                    }
                } else {
                    Player player = piece.getPlayer();
                    rowContent.add(String.format("%s%c%s",
                            player.getColor(),
                            piece.getLabel(),
                            Color.DEFAULT));
                }
            }
            contents.add(rowContent);
        }
        ArrayList<String> xCoordinates = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            xCoordinates.add(String.valueOf((char) ('a' + i)));
        }
        Collections.reverse(contents);

        // clear screen
        System.out.print("\u001b[2J");
        System.out.flush();

        System.out.println();
        System.out.println("### COMP3021 Programming Assignment 1 ###");
        System.out.println();
        System.out.println("Guide: to move a piece, input the coordinate of source and the destination.");
        System.out.println("For example: a1->b2 means to move the piece at 'a1' to 'b2'");
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            System.out.println();
            System.out.println("Notice: first " + this.configuration.getNumMovesProtection() + " moves are not allowed to" +
                    " capture pieces or win the game.");
        }
        System.out.println();
        System.out.println("Total Moves: " + this.numMoves);
        // print scores of players
        for (Player player :
                this.configuration.getPlayers()) {
            System.out.printf("%s%s%s score: %d\n", player.getColor(), player.getName(), Color.DEFAULT,
                    player.getScore());
        }
        System.out.println();
        // print the gameboard
        int leftPadding = 8;
        StringBuilder paddingSpaceBuilder = new StringBuilder();
        paddingSpaceBuilder.append(" ".repeat(leftPadding));
        System.out.printf("%s%s\n",
                paddingSpaceBuilder.toString(),
                xCoordinates.parallelStream()
                        .collect(Collectors.joining(" ")));
        StringBuilder borderBuilder = new StringBuilder();
        borderBuilder.append("-".repeat(Math.max(0, contents.get(0).size() * 2 - 1)));
        System.out.printf("%s%s\n",
                paddingSpaceBuilder.toString(),
                borderBuilder.toString());
        for (int row = contents.size() - 1; row >= 0; row--) {
            System.out.printf("%" + (leftPadding - 1) + "d|%s|%d\n",
                    row + 1,
                    contents.get(row).parallelStream().map(Object::toString).collect(Collectors.joining(" ")),
                    row + 1);
        }
        System.out.printf("%s%s\n",
                paddingSpaceBuilder.toString(),
                borderBuilder.toString());
        System.out.printf("%s%s\n",
                paddingSpaceBuilder.toString(),
                xCoordinates.parallelStream()
                        .collect(Collectors.joining(" ")));
        System.out.println();
    }

    @Override
    public Game clone() throws CloneNotSupportedException {
        Game cloned = (Game) super.clone();
        cloned.configuration = this.configuration.clone();
        cloned.board = this.board.clone();
        for (int i = 0; i < this.configuration.getSize(); i++) {
            cloned.board[i] = this.board[i].clone();
            // no need to deep copy pieces
            if (this.configuration.getSize() >= 0)
                System.arraycopy(this.board[i], 0, cloned.board[i], 0, this.configuration.getSize());
        }
        cloned.currentPlayer = currentPlayer == null ? null : currentPlayer.clone();
        return cloned;
    }
}

/**
 * Concrete implementation of the JesonMor game.
 */
class JesonMor extends Game {

    public JesonMor() {
        super();
    }

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        // TODO: Implement game loop
        return null;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // TODO: Implement winner determination
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // TODO: Implement score update based on Manhattan distance
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = this.getPiece(move.getSource());
        this.setPiece(move.getDestination(), piece);
        this.setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        // TODO: Implement available moves generation
        return new Move[0];
    }
}