import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Single-file implementation of the JesonMor core model, rules, and game logic.
 * All types are package-private as required by the task constraints.
 */

/* =========================
 * Exceptions
 * ========================= */

class InvalidConfigurationError extends Error {
    public InvalidConfigurationError(String message) {
        super(message);
    }
}

/* =========================
 * Enums
 * ========================= */

enum Color {
    DEFAULT("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m");

    private String ansiColor;

    Color(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    Color() {
        this("\u001B[0m");
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
 * Utility data classes
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
        return "(" + x + "," + y + ")";
    }
}

class Move implements Cloneable {
    private Place source;
    private Place destination;

    public Move() {
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

/* =========================
 * Player hierarchy
 * ========================= */

abstract class Player implements Cloneable {
    protected String name;
    protected int score;
    protected Color color;

    public Player() {
        this("", 0, Color.DEFAULT);
    }

    public Player(String name, Color color) {
        this(name, 0, color);
    }

    public Player(String name, int score, Color color) {
        this.name = name;
        this.score = score;
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
            System.out.print(getName() + ", enter move (source->destination): ");
            String input = scanner.nextLine();
            try {
                Move move = parseMove(input, game);
                if (availableMoves != null) {
                    for (Move legal : availableMoves) {
                        if (legal.equals(move)) {
                            return move;
                        }
                    }
                }
                System.out.println("Error: move is not legal.");
            } catch (RuntimeException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private Move parseMove(String input, Game game) {
        if (input == null) {
            throw new RuntimeException("invalid input");
        }
        String trimmed = input.trim();
        String[] parts = trimmed.split("->");
        if (parts.length != 2) {
            throw new RuntimeException("invalid input format");
        }
        Place source = parsePlace(parts[0].trim(), game);
        Place destination = parsePlace(parts[1].trim(), game);
        if (game.getPiece(source) == null) {
            throw new RuntimeException("source has no piece");
        }
        return new Move(source, destination);
    }

    private Place parsePlace(String token, Game game) {
        if (token.length() < 2) {
            throw new RuntimeException("invalid coordinate");
        }
        char columnChar = Character.toLowerCase(token.charAt(0));
        if (columnChar < 'a') {
            throw new RuntimeException("invalid coordinate");
        }
        int x = columnChar - 'a';
        int y;
        try {
            y = Integer.parseInt(token.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid coordinate");
        }
        int size = game.getConfiguration().getSize();
        if (x < 0 || y < 0 || x >= size || y >= size) {
            throw new RuntimeException("coordinates out of bounds");
        }
        return new Place(x, y);
    }
}

class RandomPlayer extends Player {
    private Random random = new Random();

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
        return availableMoves[random.nextInt(availableMoves.length)];
    }
}

/* =========================
 * Piece hierarchy
 * ========================= */

abstract class Piece implements Cloneable {
    private Player player;

    public Piece() {
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

    @Override
    public Piece clone() throws CloneNotSupportedException {
        return (Piece) super.clone();
    }
}

class Knight extends Piece {
    public Knight() {
    }

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> moves = new ArrayList<Move>();
        int[][] deltas = new int[][]{
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] d : deltas) {
            Place dest = new Place(source.getX() + d[0], source.getY() + d[1]);
            Move move = new Move(clonePlace(source), dest);
            if (new VacantRule().validate(game, move)
                    && new NilMoveRule().validate(game, move)
                    && new OutOfBoundaryRule().validate(game, move)
                    && new OccupiedRule().validate(game, move)
                    && new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()).validate(game, move)
                    && new KnightMoveRule().validate(game, move)
                    && new KnightBlockRule().validate(game, move)) {
                moves.add(move);
            }
        }
        return moves.toArray(new Move[0]);
    }

    private Place clonePlace(Place source) {
        return new Place(source.getX(), source.getY());
    }
}

class Archer extends Piece {
    public Archer() {
    }

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> moves = new ArrayList<Move>();
        int size = game.getConfiguration().getSize();
        for (int x = 0; x < size; x++) {
            if (x == source.getX()) continue;
            Place dest = new Place(x, source.getY());
            Move move = new Move(new Place(source.getX(), source.getY()), dest);
            if (new VacantRule().validate(game, move)
                    && new NilMoveRule().validate(game, move)
                    && new OutOfBoundaryRule().validate(game, move)
                    && new OccupiedRule().validate(game, move)
                    && new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()).validate(game, move)
                    && new ArcherMoveRule().validate(game, move)) {
                moves.add(move);
            }
        }
        for (int y = 0; y < size; y++) {
            if (y == source.getY()) continue;
            Place dest = new Place(source.getX(), y);
            Move move = new Move(new Place(source.getX(), source.getY()), dest);
            if (new VacantRule().validate(game, move)
                    && new NilMoveRule().validate(game, move)
                    && new OutOfBoundaryRule().validate(game, move)
                    && new OccupiedRule().validate(game, move)
                    && new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()).validate(game, move)
                    && new ArcherMoveRule().validate(game, move)) {
                moves.add(move);
            }
        }
        return moves.toArray(new Move[0]);
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
 * Game hierarchy
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

    public Place getCentralPlace() {
        return configuration.getCentralPlace();
    }

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
        for (Player player : this.configuration.getPlayers()) {
            System.out.printf("%s%s%s score: %d\n", player.getColor(), player.getName(), Color.DEFAULT,
                    player.getScore());
        }
        System.out.println();
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
            if (this.configuration.getSize() >= 0)
                System.arraycopy(this.board[i], 0, cloned.board[i], 0, this.configuration.getSize());
        }
        cloned.currentPlayer = currentPlayer == null ? null : currentPlayer.clone();
        return cloned;
    }
}

class JesonMor extends Game {
    public JesonMor() {
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
            Move[] legalMoves = getAvailableMoves(currentPlayer);
            if (legalMoves == null || legalMoves.length == 0) {
                Player winner = null;
                if (configuration.getPlayers()[0].getScore() > configuration.getPlayers()[1].getScore()) {
                    winner = configuration.getPlayers()[0];
                } else if (configuration.getPlayers()[1].getScore() > configuration.getPlayers()[0].getScore()) {
                    winner = configuration.getPlayers()[1];
                } else {
                    winner = currentPlayer;
                }
                return winner;
            }
            Move chosen = currentPlayer.nextMove(this, legalMoves);
            if (chosen == null) {
                continue;
            }
            Piece piece = getPiece(chosen.getSource());
            movePiece(chosen);
            updateScore(currentPlayer, piece, chosen);
            numMoves++;
            refreshOutput();
            Player winner = getWinner(currentPlayer, piece, chosen);
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
        if (lastPiece instanceof Knight && lastMove.getSource().equals(configuration.getCentralPlace())
                && !lastMove.getDestination().equals(configuration.getCentralPlace())) {
            return lastPlayer;
        }
        boolean p0 = false;
        boolean p1 = false;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
                if (piece != null) {
                    if (piece.getPlayer().equals(configuration.getPlayers()[0])) p0 = true;
                    if (piece.getPlayer().equals(configuration.getPlayers()[1])) p1 = true;
                }
            }
        }
        if (p0 && !p1) return configuration.getPlayers()[0];
        if (!p0 && p1) return configuration.getPlayers()[1];
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getSource().getX() - move.getDestination().getX());
        int dy = Math.abs(move.getSource().getY() - move.getDestination().getY());
        player.setScore(player.getScore() + dx + dy);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = board[move.getSource().getX()][move.getSource().getY()];
        board[move.getDestination().getX()][move.getDestination().getY()] = piece;
        board[move.getSource().getX()][move.getSource().getY()] = null;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
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
        Piece source = game.getPiece(move.getSource());
        Piece destination = game.getPiece(move.getDestination());
        if (source == null || destination == null) {
            return true;
        }
        return !source.getPlayer().equals(destination.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}

class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place s = move.getSource();
        Place d = move.getDestination();
        return s.getX() >= 0 && s.getY() >= 0 && d.getX() >= 0 && d.getY() >= 0
                && s.getX() < size && s.getY() < size && d.getX() < size && d.getY() < size;
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

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() >= numProtectedMoves) {
            return true;
        }
        Piece source = game.getPiece(move.getSource());
        Piece destination = game.getPiece(move.getDestination());
        return destination == null || source == null || destination.getPlayer().equals(source.getPlayer());
    }

    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }

    public int getNumProtectedMoves() {
        return numProtectedMoves;
    }

    public void setNumProtectedMoves(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
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
        int dx = move.getDestination().getX();
        int dy = move.getDestination().getY();
        int blockX = sx + (dx - sx) / 2;
        int blockY = sy + (dy - sy) / 2;
        if (Math.abs(dx - sx) == 2) {
            blockY = sy;
        } else {
            blockX = sx;
        }
        return game.getPiece(blockX, blockY) == null;
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
        if (sx != dx && sy != dy) {
            return false;
        }
        int stepX = Integer.compare(dx, sx);
        int stepY = Integer.compare(dy, sy);
        int x = sx + stepX;
        int y = sy + stepY;
        int between = 0;
        while (x != dx || y != dy) {
            if (game.getPiece(x, y) != null) {
                between++;
            }
            x += stepX;
            y += stepY;
        }
        Piece destination = game.getPiece(move.getDestination());
        if (destination == null) {
            return between == 0;
        } else {
            return between == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}

/* =========================
 * Minor helper for internal cloning usage
 * ========================= */

class MoveRecord {
    private Player player;
    private Move move;

    public MoveRecord() {
    }

    public MoveRecord(Player player, Move move) {
        this.player = player;
        this.move = move;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}