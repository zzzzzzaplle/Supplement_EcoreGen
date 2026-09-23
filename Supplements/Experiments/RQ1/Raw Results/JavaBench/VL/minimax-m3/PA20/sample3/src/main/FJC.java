import java.util.ArrayList;
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
 * Represents a place (coordinate) on the game board.
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

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
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

    public Place getDestination() {
        return this.destination;
    }

    public void setSource(Place source) {
        this.source = source;
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
 * Color enumeration for players and pieces display.
 */
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

    private final String ansiColor;

    Color(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    public String getAnsiColor() {
        return this.ansiColor;
    }

    @Override
    public String toString() {
        return this.ansiColor;
    }
}

/**
 * Represents a player in the game.
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

    public int getScore() {
        return this.score;
    }

    public Color getColor() {
        return this.color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
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

/**
 * A player that takes input from the console.
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
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.print("Enter your move (e.g., a1->b2): ");
        String input = scanner.nextLine().trim();
        if (input.equals("exit") || input.equals("quit")) {
            return null;
        }
        String[] parts = input.split("->");
        if (parts.length != 2) {
            return null;
        }
        Place source = parsePlace(parts[0].trim());
        Place destination = parsePlace(parts[1].trim());
        if (source == null || destination == null) {
            return null;
        }
        return new Move(source, destination);
    }

    private Place parsePlace(String s) {
        if (s.length() < 2) return null;
        int x = s.charAt(0) - 'a';
        int y;
        try {
            y = Integer.parseInt(s.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        return new Place(x, y);
    }
}

/**
 * A player that selects a random available move.
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
 * Represents a piece on the board belonging to a player.
 */
abstract class Piece {
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
}

/**
 * Knight piece - moves in an L shape.
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

    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] offset : offsets) {
            int newX = source.x() + offset[0];
            int newY = source.y() + offset[1];
            if (newX >= 0 && newX < game.getConfiguration().getSize() &&
                    newY >= 0 && newY < game.getConfiguration().getSize()) {
                moves.add(new Move(source, new Place(newX, newY)));
            }
        }
        return moves.toArray(new Move[0]);
    }
}

/**
 * Archer piece - moves orthogonally with screen capture mechanics.
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

    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            // Non-capturing moves: clear path
            for (int dist = 1; dist < size; dist++) {
                int newX = source.x() + dir[0] * dist;
                int newY = source.y() + dir[1] * dist;
                if (newX < 0 || newX >= size || newY < 0 || newY >= size) break;
                Piece target = game.getPiece(newX, newY);
                if (target == null) {
                    moves.add(new Move(source, new Place(newX, newY)));
                } else {
                    // Check for screen capture: exactly one piece between source and dest
                    for (int dist2 = dist + 1; dist2 < size; dist2++) {
                        int farX = source.x() + dir[0] * dist2;
                        int farY = source.y() + dir[1] * dist2;
                        if (farX < 0 || farX >= size || farY < 0 || farY >= size) break;
                        Piece farPiece = game.getPiece(farX, farY);
                        if (farPiece != null) {
                            // Can capture if target is enemy
                            if (!target.getPlayer().equals(this.getPlayer())) {
                                moves.add(new Move(source, new Place(farX, farY)));
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}

/**
 * Interface for move validation rules.
 */
interface Rule {
    boolean validate(Game game, Move move);

    String getDescription();
}

/**
 * Validates that the source square is not vacant.
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
 * Validates that the destination does not contain a friendly piece.
 */
class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece dest = game.getPiece(move.getDestination());
        if (dest == null) return true;
        Piece source = game.getPiece(move.getSource());
        return !source.getPlayer().equals(dest.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}

/**
 * Validates that source and destination are within board limits.
 */
class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place src = move.getSource();
        Place dst = move.getDestination();
        return src.x() >= 0 && src.x() < size && src.y() >= 0 && src.y() < size &&
                dst.x() >= 0 && dst.x() < size && dst.y() >= 0 && dst.y() < size;
    }

    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

/**
 * Validates that source and destination are different.
 */
class NilMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        return !move.getSource().equals(move.getDestination());
    }

    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}

/**
 * Validates that no pieces are captured in the first N moves.
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
        Piece dest = game.getPiece(move.getDestination());
        if (dest == null) return true;
        Piece source = game.getPiece(move.getSource());
        return source.getPlayer().equals(dest.getPlayer());
    }

    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}

/**
 * Validates Knight movement pattern (L shape).
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

    public String getDescription() {
        return "knight move rule is violated";
    }
}

/**
 * Validates Knight blocking constraints.
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
        int blockX, blockY;
        int absDx = Math.abs(dx - sx);
        int absDy = Math.abs(dy - sy);
        if (absDx == 2 && absDy == 1) {
            blockX = (sx + dx) / 2;
            blockY = sy;
        } else if (absDx == 1 && absDy == 2) {
            blockX = sx;
            blockY = (sy + dy) / 2;
        } else {
            return true;
        }
        return game.getPiece(blockX, blockY) == null;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}

/**
 * Validates Archer movement and capture rules.
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
 * Configuration of the game including board size, players, and initial setup.
 */
class Configuration implements Cloneable {
    private int size;
    private Player[] players;
    private Piece[][] initialBoard;
    private Place centralPlace;
    private int numMovesProtection;

    public Configuration() {
        this.size = 3;
        this.players = new Player[2];
        this.initialBoard = new Piece[3][3];
        this.centralPlace = new Place(1, 1);
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

    public Player[] getPlayers() {
        return this.players;
    }

    public Piece[][] getInitialBoard() {
        return this.initialBoard;
    }

    public Place getCentralPlace() {
        return this.centralPlace;
    }

    public int getNumMovesProtection() {
        return this.numMovesProtection;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public void setInitialBoard(Piece[][] initialBoard) {
        this.initialBoard = initialBoard;
    }

    public void setCentralPlace(Place centralPlace) {
        this.centralPlace = centralPlace;
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
 * Abstract base class for the game.
 */
abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;

    public Game() {
        this.configuration = null;
        this.board = null;
        this.currentPlayer = null;
        this.numMoves = 0;
    }

    public Game(Configuration configuration) {
        this.configuration = configuration;
        this.board = new Piece[configuration.getSize()][];
        for (int i = 0; i < configuration.getSize(); i++) {
            this.board[i] = new Piece[configuration.getSize()];
            for (int j = 0; j < configuration.getSize(); j++) {
                this.board[i][j] = configuration.getInitialBoard()[i][j];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int getNumMoves() {
        return this.numMoves;
    }

    public Piece[][] getBoard() {
        return this.board;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
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

    public void setPiece(Place place, Piece piece) {
        this.board[place.x()][place.y()] = piece;
    }

    public Place getCentralPlace() {
        return this.configuration.getCentralPlace();
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
}

/**
 * The JesonMor game implementation.
 */
class JesonMor extends Game {
    private List<Rule> globalRules;
    private KnightMoveRule knightMoveRule;
    private KnightBlockRule knightBlockRule;
    private ArcherMoveRule archerMoveRule;

    public JesonMor() {
        super();
        this.globalRules = new ArrayList<>();
        this.globalRules.add(new VacantRule());
        this.globalRules.add(new OccupiedRule());
        this.globalRules.add(new OutOfBoundaryRule());
        this.globalRules.add(new NilMoveRule());
        this.globalRules.add(new FirstNMovesProtectionRule());
        this.knightMoveRule = new KnightMoveRule();
        this.knightBlockRule = new KnightBlockRule();
        this.archerMoveRule = new ArcherMoveRule();
    }

    public JesonMor(Configuration configuration) {
        super(configuration);
        this.globalRules = new ArrayList<>();
        this.globalRules.add(new VacantRule());
        this.globalRules.add(new OccupiedRule());
        this.globalRules.add(new OutOfBoundaryRule());
        this.globalRules.add(new NilMoveRule());
        FirstNMovesProtectionRule protectionRule = new FirstNMovesProtectionRule(configuration.getNumMovesProtection());
        this.globalRules.add(protectionRule);
        this.knightMoveRule = new KnightMoveRule();
        this.knightBlockRule = new KnightBlockRule();
        this.archerMoveRule = new ArcherMoveRule();
    }

    @Override
    public Player start() {
        Player winner = null;
        while (winner == null) {
            this.refreshOutput();
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                // No available moves: decide by score
                Player p1 = this.configuration.getPlayers()[0];
                Player p2 = this.configuration.getPlayers()[1];
                if (p1.getScore() > p2.getScore()) {
                    winner = p1;
                } else if (p2.getScore() > p1.getScore()) {
                    winner = p2;
                } else {
                    winner = this.currentPlayer;
                }
                break;
            }
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                break;
            }
            Piece piece = this.getPiece(move.getSource());
            this.movePiece(move);
            this.updateScore(this.currentPlayer, piece, move);
            this.numMoves++;
            winner = this.getWinner(this.currentPlayer, piece, move);
            // Switch to other player
            Player p1 = this.configuration.getPlayers()[0];
            Player p2 = this.configuration.getPlayers()[1];
            this.currentPlayer = (this.currentPlayer.equals(p1)) ? p2 : p1;
        }
        this.refreshOutput();
        System.out.println("Winner: " + winner.getColor() + winner.getName() + Color.DEFAULT);
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // No winner during protection phase
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        // Check condition 1: Knight leaves central square
        if (lastPiece instanceof Knight &&
                lastMove.getSource().equals(this.configuration.getCentralPlace()) &&
                !lastMove.getDestination().equals(this.configuration.getCentralPlace())) {
            return lastPlayer;
        }
        // Check condition 2: Only one player's pieces remain
        Player p1 = this.configuration.getPlayers()[0];
        Player p2 = this.configuration.getPlayers()[1];
        boolean p1HasPiece = false;
        boolean p2HasPiece = false;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p != null) {
                    if (p.getPlayer().equals(p1)) p1HasPiece = true;
                    if (p.getPlayer().equals(p2)) p2HasPiece = true;
                }
            }
        }
        if (p1HasPiece && !p2HasPiece) return p1;
        if (!p1HasPiece && p2HasPiece) return p2;
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getDestination().x() - move.getSource().x()) +
                Math.abs(move.getDestination().y() - move.getSource().y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        if (!validateMove(move)) {
            return;
        }
        Piece piece = this.getPiece(move.getSource());
        this.setPiece(move.getDestination(), piece);
        this.setPiece(move.getSource(), null);
    }

    private boolean validateMove(Move move) {
        // Apply global rules
        for (Rule rule : this.globalRules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        // Apply piece-specific rules
        Piece piece = this.getPiece(move.getSource());
        if (piece instanceof Knight) {
            if (!knightMoveRule.validate(this, move)) return false;
            if (!knightBlockRule.validate(this, move)) return false;
        } else if (piece instanceof Archer) {
            if (!archerMoveRule.validate(this, move)) return false;
        }
        return true;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<>();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Place place = new Place(x, y);
                Piece piece = this.getPiece(place);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, place);
                    for (Move move : pieceMoves) {
                        if (validateMove(move)) {
                            allMoves.add(move);
                        }
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }
}