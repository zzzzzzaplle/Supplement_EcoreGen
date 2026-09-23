import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * All classes are intentionally package-private to satisfy the single-file compilation constraint.
 * This file contains the full JesonMor model, rules, and supporting types.
 */

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

    public Piece getPiece(Place place) {
        return this.board[place.getX()][place.getY()];
    }

    public Piece getPiece(int x, int y) {
        Place place = new Place();
        place.setX(x);
        place.setY(y);
        return this.getPiece(place);
    }

    public Place getCentralPlace() {
        return this.configuration.getCentralPlace();
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
        this.board = configuration.getInitialBoardClone();
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
    }

    public Player start() {
        this.currentPlayer = this.configuration.getPlayers()[0];
        while (true) {
            this.refreshOutput();
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                return this.getWinner(this.currentPlayer, null, null);
            }
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            Piece piece = this.getPiece(move.getSource());
            if (piece == null) {
                continue;
            }
            this.movePiece(move);
            this.updateScore(this.currentPlayer, piece, move);
            this.numMoves++;
            Player winner = this.getWinner(this.currentPlayer, piece, move);
            if (winner != null) {
                return winner;
            }
            this.currentPlayer = this.getNextPlayer(this.currentPlayer);
        }
    }

    private Player getNextPlayer(Player player) {
        Player[] players = this.configuration.getPlayers();
        if (players[0].equals(player)) {
            return players[1];
        }
        return players[0];
    }

    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        int size = this.configuration.getSize();
        Player[] players = this.configuration.getPlayers();
        boolean player0Exists = false;
        boolean player1Exists = false;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null) {
                    if (piece.getPlayer().equals(players[0])) {
                        player0Exists = true;
                    } else if (piece.getPlayer().equals(players[1])) {
                        player1Exists = true;
                    }
                }
            }
        }
        if (lastPiece instanceof Knight && lastMove != null && lastMove.getSource() != null
                && lastMove.getSource().equals(this.configuration.getCentralPlace())
                && !lastMove.getDestination().equals(this.configuration.getCentralPlace())) {
            return lastPlayer;
        }
        if (player0Exists && !player1Exists) {
            return players[0];
        }
        if (!player0Exists && player1Exists) {
            return players[1];
        }
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().getX() - move.getDestination().getX())
                + Math.abs(move.getSource().getY() - move.getDestination().getY());
        player.setScore(player.getScore() + distance);
    }

    public void movePiece(Move move) {
        Piece piece = this.board[move.getSource().getX()][move.getSource().getY()];
        this.board[move.getDestination().getX()][move.getDestination().getY()] = piece;
        this.board[move.getSource().getX()][move.getSource().getY()] = null;
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int x = 0; x < this.configuration.getSize(); x++) {
            for (int y = 0; y < this.configuration.getSize(); y++) {
                Piece piece = this.board[x][y];
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
}

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

    public Piece[][] getInitialBoardClone() {
        Piece[][] cloned = this.initialBoard.clone();
        for (int i = 0; i < this.size; i++) {
            cloned[i] = this.initialBoard[i].clone();
            System.arraycopy(this.initialBoard[i], 0, cloned[i], 0, this.size);
        }
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
        Rule[] rules = new Rule[]{new VacantRule(), new NilMoveRule(), new OutOfBoundaryRule(), new OccupiedRule(), new KnightMoveRule(), new KnightBlockRule()};
        int[][] deltas = new int[][]{
                {1, 2}, {2, 1}, {-1, 2}, {-2, 1}, {1, -2}, {2, -1}, {-1, -2}, {-2, -1}
        };
        for (int[] d : deltas) {
            Move move = new Move(source.clone(), new Place(source.getX() + d[0], source.getY() + d[1]));
            boolean ok = true;
            for (Rule rule : rules) {
                if (!rule.validate(game, move)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                moves.add(move);
            }
        }
        return moves.toArray(new Move[0]);
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
        Rule[] rules = new Rule[]{new VacantRule(), new NilMoveRule(), new OutOfBoundaryRule(), new OccupiedRule(), new ArcherMoveRule()};
        int size = game.getConfiguration().getSize();
        for (int x = 0; x < size; x++) {
            if (x != source.getX()) {
                Move move = new Move(source.clone(), new Place(x, source.getY()));
                boolean ok = true;
                for (Rule rule : rules) {
                    if (!rule.validate(game, move)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    moves.add(move);
                }
            }
        }
        for (int y = 0; y < size; y++) {
            if (y != source.getY()) {
                Move move = new Move(source.clone(), new Place(source.getX(), y));
                boolean ok = true;
                for (Rule rule : rules) {
                    if (!rule.validate(game, move)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    moves.add(move);
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}

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

class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Input your move: ");
            String input = scanner.nextLine().trim();
            Move parsed = parseMove(game, input);
            if (parsed == null) {
                System.out.println("Error: invalid move format");
                continue;
            }
            boolean legal = false;
            for (Move move : availableMoves) {
                if (move.equals(parsed)) {
                    legal = true;
                    break;
                }
            }
            if (!legal) {
                System.out.println("Error: move is not legal");
                continue;
            }
            return parsed;
        }
    }

    private Move parseMove(Game game, String input) {
        try {
            String[] parts = input.split("->");
            if (parts.length != 2) return null;
            Place source = parsePlace(game, parts[0]);
            Place destination = parsePlace(game, parts[1]);
            if (source == null || destination == null) return null;
            return new Move(source, destination);
        } catch (Exception e) {
            return null;
        }
    }

    private Place parsePlace(Game game, String token) {
        token = token.trim().toLowerCase();
        if (token.length() < 2) return null;
        char colChar = token.charAt(0);
        if (colChar < 'a' || colChar >= 'a' + game.getConfiguration().getSize()) return null;
        int x = colChar - 'a';
        int y;
        try {
            y = Integer.parseInt(token.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (y < 0 || y >= game.getConfiguration().getSize()) return null;
        return new Place(x, y);
    }
}

class RandomPlayer extends Player {
    public RandomPlayer() {
    }

    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        return availableMoves[(int) (Math.random() * availableMoves.length)];
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

    Color() {
        this.ansiColor = "";
    }

    public String getAnsiColor() {
        return ansiColor;
    }

    public void setAnsiColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    public String toString() {
        return this.ansiColor;
    }
}

interface Rule {
    boolean validate(Game game, Move move);
    String getDescription();
}

class VacantRule implements Rule {
    public VacantRule() {
    }

    public boolean validate(Game game, Move move) {
        return game.getPiece(move.getSource()) != null;
    }

    public String getDescription() {
        return "the source of move should have a piece";
    }
}

class OccupiedRule implements Rule {
    public OccupiedRule() {
    }

    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        Piece destinationPiece = game.getPiece(move.getDestination());
        if (sourcePiece == null || destinationPiece == null) {
            return true;
        }
        return !sourcePiece.getPlayer().equals(destinationPiece.getPlayer());
    }

    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}

class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {
    }

    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        return move.getSource().getX() >= 0 && move.getSource().getX() < size
                && move.getSource().getY() >= 0 && move.getSource().getY() < size
                && move.getDestination().getX() >= 0 && move.getDestination().getX() < size
                && move.getDestination().getY() >= 0 && move.getDestination().getY() < size;
    }

    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

class NilMoveRule implements Rule {
    public NilMoveRule() {
    }

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

    public boolean validate(Game game, Move move) {
        int dx = Math.abs(move.getSource().getX() - move.getDestination().getX());
        int dy = Math.abs(move.getSource().getY() - move.getDestination().getY());
        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}

class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    public boolean validate(Game game, Move move) {
        int sx = move.getSource().getX();
        int sy = move.getSource().getY();
        int dx = move.getDestination().getX();
        int dy = move.getDestination().getY();
        int stepX = dx - sx;
        int stepY = dy - sy;
        int blockX = sx;
        int blockY = sy;
        if (Math.abs(stepX) == 2 && Math.abs(stepY) == 1) {
            blockX = sx + stepX / 2;
            blockY = sy;
        } else if (Math.abs(stepX) == 1 && Math.abs(stepY) == 2) {
            blockX = sx;
            blockY = sy + stepY / 2;
        }
        return game.getPiece(blockX, blockY) == null;
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
        boolean orthogonal = sx == dx || sy == dy;
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
        Piece destinationPiece = game.getPiece(move.getDestination());
        if (destinationPiece == null) {
            return count == 0;
        } else {
            return count == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}

class InvalidConfigurationError extends Error {
    public InvalidConfigurationError(String message) {
        super(message);
    }
}