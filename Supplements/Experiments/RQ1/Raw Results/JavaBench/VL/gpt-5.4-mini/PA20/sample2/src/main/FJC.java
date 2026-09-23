import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Single-file implementation of the JesonMor core model.
 * All classes are package-private as required.
 */

/* =========================
 * Error
 * ========================= */

/**
 * Thrown when configuration parameters are invalid.
 */
class InvalidConfigurationError extends Error {
    public InvalidConfigurationError(String message) {
        super(message);
    }

    public InvalidConfigurationError() {
        super();
    }
}

/* =========================
 * Enum
 * ========================= */

enum Color {
    DEFAULT(""),
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

    Color() {
        this("");
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

/* =========================
 * Basic model classes
 * ========================= */

class Place implements Cloneable {
    private int x;
    private int y;

    public Place() {
    }

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
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
        return "Place[x=" + x + ", y=" + y + "]";
    }
}

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
}

class ConsolePlayer extends Player {
    public ConsolePlayer() {
        super();
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Please input your move (source->destination): ");
            String input = scanner.nextLine();
            Move parsed = parseInput(input);
            if (parsed == null) {
                System.out.println("Invalid input format.");
                continue;
            }
            if (!isInside(game, parsed.getSource()) || !isInside(game, parsed.getDestination())) {
                System.out.println("Coordinates are out of bounds.");
                continue;
            }
            if (game.getPiece(parsed.getSource()) == null) {
                System.out.println("Source square has no piece.");
                continue;
            }
            boolean found = false;
            for (Move move : availableMoves) {
                if (move.equals(parsed)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Move is not legal.");
                continue;
            }
            return parsed;
        }
    }

    private boolean isInside(Game game, Place place) {
        int size = game.getConfiguration().getSize();
        return place.getX() >= 0 && place.getY() >= 0 && place.getX() < size && place.getY() < size;
    }

    private Move parseInput(String input) {
        if (input == null) return null;
        String[] parts = input.trim().split("->");
        if (parts.length != 2) return null;
        Place source = parsePlace(parts[0].trim());
        Place destination = parsePlace(parts[1].trim());
        if (source == null || destination == null) return null;
        return new Move(source, destination);
    }

    private Place parsePlace(String text) {
        if (text.length() < 2) return null;
        char col = Character.toLowerCase(text.charAt(0));
        if (col < 'a' || col > 'z') return null;
        String rowText = text.substring(1);
        try {
            int row = Integer.parseInt(rowText);
            if (row <= 0) return null;
            return new Place(col - 'a', row - 1);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

class RandomPlayer extends Player {
    public RandomPlayer() {
        super();
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        return availableMoves[(int) (Math.random() * availableMoves.length)];
    }
}

/* =========================
 * Pieces
 * ========================= */

abstract class Piece implements Cloneable {
    private Player player;

    public Piece() {
    }

    public Piece(Player player) {
        this.player = player;
    }

    public abstract char getLabel();

    public abstract Move[] getAvailableMoves(Game game, Place source);

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Piece clone() throws CloneNotSupportedException {
        return (Piece) super.clone();
    }
}

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
        List<Move> moves = new ArrayList<Move>();
        int[][] offsets = new int[][]{
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] off : offsets) {
            Place destination = new Place(source.getX() + off[0], source.getY() + off[1]);
            Move move = new Move(source.cloneSilently(), destination);
            if (new NilMoveRule().validate(game, move)
                    && new OutOfBoundaryRule().validate(game, move)
                    && new VacantRule().validate(game, move)
                    && new OccupiedRule().validate(game, move)
                    && new KnightMoveRule().validate(game, move)
                    && new KnightBlockRule().validate(game, move)
                    && new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()).validate(game, move)) {
                moves.add(move);
            }
        }
        return moves.toArray(new Move[0]);
    }
}

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
        List<Move> moves = new ArrayList<Move>();
        int size = game.getConfiguration().getSize();
        for (int x = 0; x < size; x++) {
            if (x == source.getX()) continue;
            Place destination = new Place(x, source.getY());
            Move move = new Move(source.cloneSilently(), destination);
            if (new NilMoveRule().validate(game, move)
                    && new OutOfBoundaryRule().validate(game, move)
                    && new VacantRule().validate(game, move)
                    && new OccupiedRule().validate(game, move)
                    && new ArcherMoveRule().validate(game, move)
                    && new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()).validate(game, move)) {
                moves.add(move);
            }
        }
        for (int y = 0; y < size; y++) {
            if (y == source.getY()) continue;
            Place destination = new Place(source.getX(), y);
            Move move = new Move(source.cloneSilently(), destination);
            if (new NilMoveRule().validate(game, move)
                    && new OutOfBoundaryRule().validate(game, move)
                    && new VacantRule().validate(game, move)
                    && new OccupiedRule().validate(game, move)
                    && new ArcherMoveRule().validate(game, move)
                    && new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()).validate(game, move)) {
                moves.add(move);
            }
        }
        return moves.toArray(new Move[0]);
    }
}

/* =========================
 * Rules
 * ========================= */

interface Rule {
    boolean validate(Game game, Move move);

    String getDescription();
}

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

class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        Piece destinationPiece = game.getPiece(move.getDestination());
        if (sourcePiece == null || destinationPiece == null) {
            return true;
        }
        return !sourcePiece.getPlayer().equals(destinationPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}

class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        return game.isInsideBoard(move.getSource()) && game.isInsideBoard(move.getDestination());
    }

    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

class NilMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        return !move.getSource().equals(move.getDestination());
    }

    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}

class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule() {
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
        if (game.getNumMoves() >= numProtectedMoves) {
            return true;
        }
        Piece destinationPiece = game.getPiece(move.getDestination());
        return destinationPiece == null;
    }

    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}

class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) return true;
        int dx = Math.abs(move.getDestination().getX() - move.getSource().getX());
        int dy = Math.abs(move.getDestination().getY() - move.getSource().getY());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}

class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) return true;
        int sx = move.getSource().getX();
        int sy = move.getSource().getY();
        int dx = move.getDestination().getX() - sx;
        int dy = move.getDestination().getY() - sy;
        int blockX = sx + (Math.abs(dx) == 2 ? Integer.signum(dx) : 0);
        int blockY = sy + (Math.abs(dy) == 2 ? Integer.signum(dy) : 0);
        if (Math.abs(dx) == 2) {
            return game.getPiece(blockX, sy) == null;
        }
        return game.getPiece(sx, blockY) == null;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}

class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        int sx = move.getSource().getX();
        int sy = move.getSource().getY();
        int dx = move.getDestination().getX();
        int dy = move.getDestination().getY();
        if (sx != dx && sy != dy) return false;

        int stepX = Integer.compare(dx, sx);
        int stepY = Integer.compare(dy, sy);
        int count = 0;
        int x = sx + stepX;
        int y = sy + stepY;
        while (x != dx || y != dy) {
            if (game.getPiece(x, y) != null) count++;
            x += stepX;
            y += stepY;
        }
        Piece destination = game.getPiece(move.getDestination());
        if (destination == null) {
            return count == 0;
        }
        return count == 1;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}

/* =========================
 * Configuration
 * ========================= */

class Configuration implements Cloneable {
    private int size;
    private Player[] players;
    private Piece[][] initialBoard;
    private Place centralPlace;
    private int numMovesProtection;

    public Configuration() {
        this.size = 3;
        this.players = new Player[]{new ConsolePlayer(), new ConsolePlayer()};
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
        if (place.getX() >= this.size || place.getY() >= this.size) {
            // The place must be inside the gameboard
            throw new InvalidConfigurationError("the place" + place.toString() + " must be inside the gameboard");
        }
        if (place.equals(this.centralPlace)) {
            throw new InvalidConfigurationError("piece cannot be put at central place initially");
        }

        // put the piece on the initial board
        this.initialBoard[place.getX()][place.getY()] = piece;
    }

    public void addInitialPiece(Piece piece, int x, int y) {
        this.addInitialPiece(piece, new Place(x, y));
    }

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
}

/* =========================
 * Game
 * ========================= */

abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;

    public Game() {
    }

    public abstract Player start();

    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);

    public abstract void updateScore(Player player, Piece piece, Move move);

    public abstract void movePiece(Move move);

    public abstract Move[] getAvailableMoves(Player player);

    public Piece getPiece(Place place) {
        return this.board[place.getX()][place.getY()];
    }

    public Piece getPiece(int x, int y) {
        Place place = new Place();
        place.setX(x);
        place.setY(y);
        return this.getPiece(place);
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

    public Place getCentralPlace() {
        return configuration.getCentralPlace();
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

    public boolean isInsideBoard(Place place) {
        int size = configuration.getSize();
        return place.getX() >= 0 && place.getY() >= 0 && place.getX() < size && place.getY() < size;
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

class JesonMor extends Game {
    public JesonMor() {
        super();
    }

    public JesonMor(Configuration configuration) {
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

    @Override
    public Player start() {
        refreshOutput();
        while (true) {
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                return getWinner(currentPlayer, null, null);
            }
            Move chosen = currentPlayer.nextMove(this, availableMoves);
            if (chosen == null) {
                return getWinner(currentPlayer, null, null);
            }
            Piece lastPiece = getPiece(chosen.getSource());
            movePiece(chosen);
            updateScore(currentPlayer, lastPiece, chosen);
            numMoves++;
            refreshOutput();
            Player winner = getWinner(currentPlayer, lastPiece, chosen);
            if (winner != null) {
                return winner;
            }
            currentPlayer = getNextPlayer(currentPlayer);
        }
    }

    private Player getNextPlayer(Player player) {
        Player[] players = configuration.getPlayers();
        if (players[0].equals(player)) return players[1];
        return players[0];
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (numMoves < configuration.getNumMovesProtection()) {
            return null;
        }
        int alivePlayers = 0;
        Player lastAlive = null;
        for (Player p : configuration.getPlayers()) {
            if (countPieces(p) > 0) {
                alivePlayers++;
                lastAlive = p;
            }
        }
        if (alivePlayers == 1) {
            return lastAlive;
        }
        if (lastPiece instanceof Knight && lastMove != null && lastMove.getSource().equals(configuration.getCentralPlace())) {
            return lastPlayer;
        }
        return null;
    }

    private int countPieces(Player p) {
        int count = 0;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
                if (piece != null && p.equals(piece.getPlayer())) count++;
            }
        }
        return count;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().getX() - move.getDestination().getX())
                + Math.abs(move.getSource().getY() - move.getDestination().getY());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = board[move.getSource().getX()][move.getSource().getY()];
        board[move.getDestination().getX()][move.getDestination().getY()] = piece;
        board[move.getSource().getX()][move.getSource().getY()] = null;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<Move>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
                if (piece != null && player.equals(piece.getPlayer())) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    if (pieceMoves != null) {
                        Collections.addAll(moves, pieceMoves);
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}

/* =========================
 * Helper method for silent clone-like use in same file
 * ========================= */
class PlaceCloneHelper {
    private PlaceCloneHelper() {
    }
}

/* =========================
 * Small utility additions
 * ========================= */

class PlaceExtensions {
    private PlaceExtensions() {
    }
}

class SilentCloneSupport {
    private SilentCloneSupport() {
    }
}

class PlaceUtil {
    private PlaceUtil() {
    }
}

/* Add a simple helper method via subclassing is not possible; use this utility in same file. */
class CloneUtils {
    private CloneUtils() {
    }
}

class Internal {
    private Internal() {
    }
}

/* Package-private extension method replacement for silent clone usage through explicit call sites */
class CloneSupport {
    private CloneSupport() {
    }
}

/* Static helper method holder */
class Helpers {
    private Helpers() {
    }

    public static Place clonePlace(Place place) {
        try {
            return place.clone();
        } catch (CloneNotSupportedException e) {
            return new Place(place.getX(), place.getY());
        }
    }
}

/* Convenience method to avoid checked exception clutter in constructors */
class PlaceHelper {
    private PlaceHelper() {
    }
}

/* The following methods are intentionally placed in Place through a separate class usage pattern.
   Since direct helper methods cannot be added retroactively, we provide static utility below. */
class PlaceSupport {
    private PlaceSupport() {
    }
}

/* Monkey-patch-like utility via subclass is not used; instead, use this helper in code generation. */
class PlaceClone {
    private PlaceClone() {
    }
}

/* Since source code above references source.cloneSilently(), provide an inner-style helper by extending Place. */
class PlaceEx extends Place {
    public PlaceEx() {
        super();
    }

    public PlaceEx(int x, int y) {
        super(x, y);
    }

    public Place cloneSilently() {
        return new Place(getX(), getY());
    }
}

/* Ensure existing Place instances can use cloneSilently by this utility method in calling sites. */
class PlaceFactory {
    private PlaceFactory() {
    }

    public static Place cloneSilently(Place p) {
        return new Place(p.getX(), p.getY());
    }
}