import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

// ===================== Place =====================
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

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
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

// ===================== Color =====================
enum Color {
    DEFAULT("\u001b[0m"),
    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    PURPLE("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m");

    private String ansiColor;

    Color(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    public String getAnsiColor() {
        return ansiColor;
    }

    public void setAnsiColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    @Override
    public String toString() {
        return ansiColor;
    }
}

// ===================== Move =====================
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
        return source;
    }

    public void setSource(Place source) {
        this.source = source;
    }

    public Place getDestination() {
        return destination;
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

// ===================== Player =====================
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract Move nextMove(Game game, Move[] availableMoves);

    @Override
    public Player clone() throws CloneNotSupportedException {
        return (Player) super.clone();
    }

    @Override
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

// ===================== ConsolePlayer =====================
class ConsolePlayer extends Player {

    public ConsolePlayer() {
        super();
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // TODO implementation
        return null;
    }
}

// ===================== RandomPlayer =====================
class RandomPlayer extends Player {

    public RandomPlayer() {
        super();
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // TODO implementation
        return null;
    }
}

// ===================== Piece =====================
abstract class Piece {
    private Player player;

    public Piece() {
        this.player = null;
    }

    public Piece(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract char getLabel();

    public abstract Move[] getAvailableMoves(Game game, Place source);
}

// ===================== Knight =====================
class Knight extends Piece {

    public Knight() {
        super();
    }

    public Knight(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        // TODO implementation
        return new Move[0];
    }
}

// ===================== Archer =====================
class Archer extends Piece {

    public Archer() {
        super();
    }

    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        // TODO implementation
        return new Move[0];
    }
}

// ===================== Configuration =====================
class Configuration implements Cloneable {
    private int size;
    private Player[] players;
    private Piece[][] initialBoard;
    private Place centralPlace;
    private int numMovesProtection;

    public Configuration() {
        this.size = 0;
        this.players = new Player[0];
        this.initialBoard = new Piece[0][];
        this.centralPlace = new Place();
        this.numMovesProtection = 0;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public Piece[][] getInitialBoard() {
        return initialBoard;
    }

    public void setInitialBoard(Piece[][] initialBoard) {
        this.initialBoard = initialBoard;
    }

    public Place getCentralPlace() {
        return centralPlace;
    }

    public void setCentralPlace(Place centralPlace) {
        this.centralPlace = centralPlace;
    }

    public int getNumMovesProtection() {
        return numMovesProtection;
    }

    public void setNumMovesProtection(int numMovesProtection) {
        this.numMovesProtection = numMovesProtection;
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

// ===================== Game =====================
abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;

    public Game() {
        this.configuration = null;
        this.board = new Piece[0][];
        this.currentPlayer = null;
        this.numMoves = 0;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public abstract Player start();

    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);

    public abstract void updateScore(Player player, Piece piece, Move move);

    public abstract void movePiece(Move move);

    public abstract Move[] getAvailableMoves(Player player);

    public Piece getPiece(Place place) {
        return this.board[place.x()][place.y()];
    }

    public Piece getPiece(int x, int y) {
        return this.getPiece(new Place(x, y));
    }

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

    public Place getCentralPlace() {
        return this.configuration.getCentralPlace();
    }
}

// ===================== JesonMor =====================
class JesonMor extends Game {

    public JesonMor() {
        super();
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Player start() {
        // TODO implementation
        return null;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // TODO implementation
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // TODO implementation
    }

    @Override
    public void movePiece(Move move) {
        // TODO implementation
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        // TODO implementation
        return new Move[0];
    }
}

// ===================== Rule =====================
interface Rule {
    boolean validate(Game game, Move move);

    String getDescription();
}

// ===================== VacantRule =====================
class VacantRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // TODO implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}

// ===================== OccupiedRule =====================
class OccupiedRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // TODO implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}

// ===================== OutOfBoundaryRule =====================
class OutOfBoundaryRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // TODO implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

// ===================== NilMoveRule =====================
class NilMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // TODO implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}

// ===================== FirstNMovesProtectionRule =====================
class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule() {
        this.numProtectedMoves = 0;
    }

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    public int getNumProtectedMoves() {
        return numProtectedMoves;
    }

    public void setNumProtectedMoves(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        // TODO implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}

// ===================== KnightMoveRule =====================
class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // TODO implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}

// ===================== KnightBlockRule =====================
class KnightBlockRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // TODO implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}

// ===================== ArcherMoveRule =====================
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

// ===================== InvalidConfigurationError =====================
class InvalidConfigurationError extends Error {
    public InvalidConfigurationError() {
        super();
    }

    public InvalidConfigurationError(String message) {
        super(message);
    }
}