import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Combined single-file implementation for the JesonMor game model.
 * All classes are package-private to satisfy single-file compilation constraints.
 */

/* =========================
 * Error Type
 * ========================= */

/**
 * Thrown when a configuration is invalid.
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
 * Core Domain Types
 * ========================= */

/**
 * Board coordinate. Coordinates are zero-based from the bottom-left corner.
 */
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
        if (!(o instanceof Place)) return false;
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

/**
 * A move from source to destination.
 */
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

/**
 * ANSI colors for console display.
 */
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
    }

    public String getAnsiColor() {
        return ansiColor;
    }

    public void setAnsiColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    @Override
    public String toString() {
        return ansiColor == null ? "" : ansiColor;
    }
}

/**
 * Player abstraction.
 */
abstract class Player implements Cloneable {
    protected String name;
    protected int score;
    protected Color color;

    public Player() {
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

/**
 * Console player that reads moves from stdin.
 */
class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                Move move = parseMove(line);
                boolean found = false;
                if (availableMoves != null) {
                    for (Move legal : availableMoves) {
                        if (move.equals(legal)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    System.out.println("Error: illegal move");
                    continue;
                }
                return move;
            } catch (Exception e) {
                System.out.println("Error: invalid input");
            }
        }
    }

    private Move parseMove(String input) {
        if (input == null || !input.contains("->")) {
            throw new IllegalArgumentException("bad format");
        }
        String[] parts = input.split("->");
        if (parts.length != 2) {
            throw new IllegalArgumentException("bad format");
        }
        Place src = parsePlace(parts[0].trim());
        Place dst = parsePlace(parts[1].trim());
        return new Move(src, dst);
    }

    private Place parsePlace(String token) {
        if (token.length() < 2) {
            throw new IllegalArgumentException("bad coordinate");
        }
        char col = Character.toLowerCase(token.charAt(0));
        int x = col - 'a';
        int y = Integer.parseInt(token.substring(1)) - 1;
        Place place = new Place();
        place.setX(x);
        place.setY(y);
        return place;
    }
}

/**
 * Random player that selects any legal move.
 */
class RandomPlayer extends Player {
    private Random random = new Random();

    public RandomPlayer() {
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

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}

/**
 * A piece on the board.
 */
abstract class Piece implements Cloneable {
    protected Player player;

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
}

/**
 * Knight piece.
 */
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
        List<Move> moves = new ArrayList<Move>();
        int[][] deltas = new int[][]{
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        Rule[] globalRules = new Rule[]{
                new VacantRule(),
                new NilMoveRule(),
                new OutOfBoundaryRule(),
                new OccupiedRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection())
        };
        Rule[] specificRules = new Rule[]{
                new KnightMoveRule(),
                new KnightBlockRule()
        };
        for (int[] d : deltas) {
            Place dest = new Place(source.getX() + d[0], source.getY() + d[1]);
            Move move = new Move(source, dest);
            boolean ok = true;
            for (Rule rule : globalRules) {
                if (!rule.validate(game, move)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                for (Rule rule : specificRules) {
                    if (!rule.validate(game, move)) {
                        ok = false;
                        break;
                    }
                }
            }
            if (ok) {
                moves.add(move);
            }
        }
        return moves.toArray(new Move[0]);
    }
}

/**
 * Archer piece.
 */
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
        List<Move> moves = new ArrayList<Move>();
        Rule[] globalRules = new Rule[]{
                new VacantRule(),
                new NilMoveRule(),
                new OutOfBoundaryRule(),
                new OccupiedRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection())
        };
        Rule archerRule = new ArcherMoveRule();
        int size = game.getConfiguration().getSize();
        for (int x = 0; x < size; x++) {
            if (x != source.getX()) {
                Place dest = new Place(x, source.getY());
                Move move = new Move(source, dest);
                boolean ok = true;
                for (Rule rule : globalRules) {
                    if (!rule.validate(game, move)) {
                        ok = false;
                        break;
                    }
                }
                if (ok && archerRule.validate(game, move)) {
                    moves.add(move);
                }
            }
        }
        for (int y = 0; y < size; y++) {
            if (y != source.getY()) {
                Place dest = new Place(source.getX(), y);
                Move move = new Move(source, dest);
                boolean ok = true;
                for (Rule rule : globalRules) {
                    if (!rule.validate(game, move)) {
                        ok = false;
                        break;
                    }
                }
                if (ok && archerRule.validate(game, move)) {
                    moves.add(move);
                }
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

    public Configuration(int size, Player[] players, int numMovesProtection) {
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
        this.players = players;
        if (players.length != 2) {
            throw new InvalidConfigurationError("there must be exactly two players");
        }
        this.initialBoard = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.initialBoard[x] = new Piece[size];
            for (int y = 0; y < size; y++) {
                this.initialBoard[x][y] = null;
            }
        }
        this.centralPlace = new Place(size / 2, size / 2);
        if (numMovesProtection < 0) {
            throw new InvalidConfigurationError("number of moves with capture protection cannot be negative");
        }
        this.numMovesProtection = numMovesProtection;
    }

    public Configuration(int size, Player[] players) {
        this(size, players, 0);
    }

    public void addInitialPiece(Piece piece, Place place) {
        if (!piece.getPlayer().equals(this.players[0]) && !piece.getPlayer().equals(this.players[1])) {
            throw new InvalidConfigurationError("the player of the piece is unknown");
        }
        if (place.getX() >= this.size || place.getY() >= this.size) {
            throw new InvalidConfigurationError("the place" + place.toString() + " must be inside the gameboard");
        }
        if (place.equals(this.centralPlace)) {
            throw new InvalidConfigurationError("piece cannot be put at central place initially");
        }
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
 * Rules
 * ========================= */

interface Rule {
    boolean validate(Game game, Move move);

    String getDescription();
}

class VacantRule implements Rule {
    public VacantRule() {
    }

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
    public OccupiedRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        Piece destinationPiece = game.getPiece(move.getDestination());
        if (sourcePiece == null) {
            return true;
        }
        if (destinationPiece == null) {
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
    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place s = move.getSource();
        Place d = move.getDestination();
        return s.getX() >= 0 && s.getX() < size && s.getY() >= 0 && s.getY() < size
                && d.getX() >= 0 && d.getX() < size && d.getY() >= 0 && d.getY() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

class NilMoveRule implements Rule {
    public NilMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        return !move.getSource().equals(move.getDestination());
    }

    @Override
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
        if (game.getNumMoves() < this.numProtectedMoves) {
            return game.getPiece(move.getDestination()) == null;
        }
        return true;
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
    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int dx = Math.abs(move.getDestination().getX() - move.getSource().getX());
        int dy = Math.abs(move.getDestination().getY() - move.getSource().getY());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}

class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int sx = move.getSource().getX();
        int sy = move.getSource().getY();
        int dx = move.getDestination().getX();
        int dy = move.getDestination().getY();
        int adx = Math.abs(dx - sx);
        int ady = Math.abs(dy - sy);

        Place block;
        if (adx == 2 && ady == 1) {
            block = new Place(sx + Integer.signum(dx - sx), sy);
        } else if (adx == 1 && ady == 2) {
            block = new Place(sx, sy + Integer.signum(dy - sy));
        } else {
            return true;
        }
        return game.getPiece(block) == null;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}

class ArcherMoveRule implements Rule {
    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        int sx = move.getSource().getX();
        int sy = move.getSource().getY();
        int dx = move.getDestination().getX();
        int dy = move.getDestination().getY();
        boolean orthogonal = (sx == dx && sy != dy) || (sy == dy && sx != dx);
        if (!orthogonal) {
            return false;
        }
        int stepX = Integer.compare(dx, sx);
        int stepY = Integer.compare(dy, sy);
        int x = sx + stepX;
        int y = sy + stepY;
        int count = 0;
        while (x != dx || y != dy) {
            if (game.getPiece(x, y) != null) {
                count++;
            }
            x += stepX;
            y += stepY;
        }
        if (game.getPiece(move.getDestination()) == null) {
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
        for (Player player :
                this.configuration.getPlayers()) {
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
}

/**
 * JesonMor game implementation.
 */
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
        while (true) {
            this.refreshOutput();
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                Player p0 = this.configuration.getPlayers()[0];
                Player p1 = this.configuration.getPlayers()[1];
                if (p0.getScore() > p1.getScore()) return p0;
                if (p1.getScore() > p0.getScore()) return p1;
                return this.currentPlayer;
            }
            Move chosen = this.currentPlayer.nextMove(this, availableMoves);
            if (chosen == null) {
                continue;
            }
            Piece lastPiece = this.getPiece(chosen.getSource());
            Player lastPlayer = this.currentPlayer;
            this.movePiece(chosen);
            this.updateScore(lastPlayer, lastPiece, chosen);
            this.numMoves++;
            Player winner = this.getWinner(lastPlayer, lastPiece, chosen);
            if (winner != null) {
                this.refreshOutput();
                return winner;
            }
            this.currentPlayer = this.configuration.getPlayers()[0].equals(this.currentPlayer)
                    ? this.configuration.getPlayers()[1]
                    : this.configuration.getPlayers()[0];
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }

        if (lastPiece instanceof Knight && lastMove.getSource().equals(this.getCentralPlace())) {
            return lastPlayer;
        }

        boolean player0HasPiece = false;
        boolean player1HasPiece = false;
        Player p0 = this.configuration.getPlayers()[0];
        Player p1 = this.configuration.getPlayers()[1];
        for (int x = 0; x < this.configuration.getSize(); x++) {
            for (int y = 0; y < this.configuration.getSize(); y++) {
                Piece piece = this.board[x][y];
                if (piece != null) {
                    if (piece.getPlayer().equals(p0)) player0HasPiece = true;
                    if (piece.getPlayer().equals(p1)) player1HasPiece = true;
                }
            }
        }
        if (player0HasPiece && !player1HasPiece) return p0;
        if (player1HasPiece && !player0HasPiece) return p1;
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dist = Math.abs(move.getSource().getX() - move.getDestination().getX())
                + Math.abs(move.getSource().getY() - move.getDestination().getY());
        player.setScore(player.getScore() + dist);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = this.getPiece(move.getSource());
        this.board[move.getDestination().getX()][move.getDestination().getY()] = piece;
        this.board[move.getSource().getX()][move.getSource().getY()] = null;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> all = new ArrayList<Move>();
        for (int x = 0; x < this.configuration.getSize(); x++) {
            for (int y = 0; y < this.configuration.getSize(); y++) {
                Piece piece = this.board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    if (pieceMoves != null) {
                        Collections.addAll(all, pieceMoves);
                    }
                }
            }
        }
        return all.toArray(new Move[0]);
    }
}