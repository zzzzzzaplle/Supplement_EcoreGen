import java.util.*;
import java.util.stream.*;

// Enum Color
enum Color {
    DEFAULT(""),
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

    @Override
    public String toString() {
        return ansiColor;
    }

    public String getAnsiColor() {
        return ansiColor;
    }

    public void setAnsiColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }
}

// Class Place
class Place {
    private int x;
    private int y;

    public Place() {
    }

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
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

    public int x() {
        return x;
    }

    public int y() {
        return y;
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

    @Override
    public Place clone() {
        return new Place(x, y);
    }
}

// Abstract class Player
abstract class Player {
    private String name;
    private int score;
    private Color color;

    public Player() {
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

// Class ConsolePlayer
class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // implementation not required in this task (simplified)
        return null;
    }
}

// Class RandomPlayer
class RandomPlayer extends Player {
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
        Random rand = new Random();
        return availableMoves[rand.nextInt(availableMoves.length)];
    }
}

// Abstract class Piece
abstract class Piece {
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
}

// Class Knight
class Knight extends Piece {
    public Knight() {
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
        List<Move> moves = new ArrayList<>();
        int[][] offsets = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        for (int[] off : offsets) {
            int dx = off[0];
            int dy = off[1];
            int nx = source.x() + dx;
            int ny = source.y() + dy;
            if (nx >= 0 && nx < game.getConfiguration().getSize() && ny >= 0 && ny < game.getConfiguration().getSize()) {
                Place dest = new Place(nx, ny);
                Move move = new Move(source, dest);
                // Apply all rules
                boolean valid = true;
                for (Rule rule : getRules(game)) {
                    if (!rule.validate(game, move)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    moves.add(move);
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    private List<Rule> getRules(Game game) {
        List<Rule> rules = new ArrayList<>();
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OutOfBoundaryRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()));
        rules.add(new KnightMoveRule());
        rules.add(new KnightBlockRule());
        return rules;
    }
}

// Class Archer
class Archer extends Piece {
    public Archer() {
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
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        // orthogonal directions
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            // non-capturing moves
            int steps = 1;
            while (true) {
                int nx = source.x() + dir[0] * steps;
                int ny = source.y() + dir[1] * steps;
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) break;
                Place dest = new Place(nx, ny);
                // check if path is clear for non-capture
                boolean pathClear = true;
                int checkSteps = 1;
                while (checkSteps < steps) {
                    int cx = source.x() + dir[0] * checkSteps;
                    int cy = source.y() + dir[1] * checkSteps;
                    if (game.getPiece(cx, cy) != null) {
                        pathClear = false;
                        break;
                    }
                    checkSteps++;
                }
                if (pathClear) {
                    Move move = new Move(source, dest);
                    boolean valid = true;
                    for (Rule rule : getRules(game)) {
                        if (!rule.validate(game, move)) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        moves.add(move);
                    }
                }
                steps++;
            }
            // capturing moves (exactly one piece in between)
            steps = 2;
            while (true) {
                int nx = source.x() + dir[0] * steps;
                int ny = source.y() + dir[1] * steps;
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) break;
                Place dest = new Place(nx, ny);
                // count pieces between source and dest (exclusive)
                int count = 0;
                for (int s = 1; s < steps; s++) {
                    int cx = source.x() + dir[0] * s;
                    int cy = source.y() + dir[1] * s;
                    if (game.getPiece(cx, cy) != null) {
                        count++;
                    }
                }
                if (count == 1) {
                    Move move = new Move(source, dest);
                    boolean valid = true;
                    for (Rule rule : getRules(game)) {
                        if (!rule.validate(game, move)) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        moves.add(move);
                    }
                }
                steps++;
            }
        }
        return moves.toArray(new Move[0]);
    }

    private List<Rule> getRules(Game game) {
        List<Rule> rules = new ArrayList<>();
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OutOfBoundaryRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()));
        rules.add(new ArcherMoveRule());
        return rules;
    }
}

// Class Move
class Move {
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

// Interface Rule
interface Rule {
    boolean validate(Game game, Move move);
    String getDescription();
}

// Class VacantRule
class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Piece piece = game.getPiece(source);
        return piece != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}

// Class OccupiedRule
class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        Piece srcPiece = game.getPiece(source);
        Piece destPiece = game.getPiece(dest);
        if (srcPiece == null) return false; // handled by VacantRule
        if (destPiece == null) return true;
        return !srcPiece.getPlayer().equals(destPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}

// Class OutOfBoundaryRule
class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        int size = game.getConfiguration().getSize();
        return source.x() >= 0 && source.x() < size && source.y() >= 0 && source.y() < size &&
               dest.x() >= 0 && dest.x() < size && dest.y() >= 0 && dest.y() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

// Class NilMoveRule
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

// Class FirstNMovesProtectionRule
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
        // If we are still in protection phase, capturing is not allowed
        if (game.getNumMoves() < numProtectedMoves) {
            Place dest = move.getDestination();
            Piece destPiece = game.getPiece(dest);
            return destPiece == null; // cannot capture
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}

// Class KnightMoveRule
class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
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

// Class KnightBlockRule
class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        if (absDx == 2 && absDy == 1) {
            // blocking square at ((source.x + destination.x)/2, source.y)
            int blockX = (move.getSource().x() + move.getDestination().x()) / 2;
            int blockY = move.getSource().y();
            return game.getPiece(blockX, blockY) == null;
        } else if (absDx == 1 && absDy == 2) {
            // blocking square at (source.x, (source.y + destination.y)/2)
            int blockX = move.getSource().x();
            int blockY = (move.getSource().y() + move.getDestination().y()) / 2;
            return game.getPiece(blockX, blockY) == null;
        }
        return true; // not a valid knight move anyway, but other rules handle that
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}

// Class ArcherMoveRule
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

// Class Configuration
class Configuration {
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

    public void addInitialPiece(Piece piece, Place place) {
        if (!piece.getPlayer().equals(this.players[0]) && !piece.getPlayer().equals(this.players[1])) {
            throw new InvalidConfigurationError("the player of the piece is unknown");
        }
        if (place.x() >= this.size || place.y() >= this.size) {
            throw new InvalidConfigurationError("the place" + place.toString() + " must be inside the gameboard");
        }
        if (place.equals(this.centralPlace)) {
            throw new InvalidConfigurationError("piece cannot be put at central place initially");
        }
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
            System.arraycopy(this.initialBoard[i], 0, cloned.initialBoard[i], 0, this.size);
        }
        cloned.centralPlace = this.centralPlace.clone();
        return cloned;
    }
}

// Abstract class Game
abstract class Game {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;

    public Game() {
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

    public Piece getPiece(Place place) {
        return this.board[place.x()][place.y()];
    }

    public Piece getPiece(int x, int y) {
        return this.getPiece(new Place(x, y));
    }

    public void setPiece(Place place, Piece piece) {
        this.board[place.x()][place.y()] = piece;
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

// Class JesonMor
class JesonMor extends Game {
    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.board = new Piece[configuration.getSize()][configuration.getSize()];
        // copy initial board
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
        // Game loop (simplified for code generation)
        // In a real implementation this would loop, but we just return the first player for now
        return currentPlayer;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (numMoves < configuration.getNumMovesProtection()) {
            return null;
        }
        // Check condition 1: Knight leaving central square
        if (lastPiece instanceof Knight && lastMove.getSource().equals(configuration.getCentralPlace())
                && !lastMove.getDestination().equals(configuration.getCentralPlace())) {
            return lastPlayer;
        }
        // Check condition 2: only one player's pieces remain
        Set<Player> playersWithPieces = new HashSet<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = getPiece(x, y);
                if (p != null) {
                    playersWithPieces.add(p.getPlayer());
                }
            }
        }
        if (playersWithPieces.size() == 1) {
            return playersWithPieces.iterator().next();
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getDestination().x() - move.getSource().x())
                + Math.abs(move.getDestination().y() - move.getSource().y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        Piece piece = getPiece(source);
        setPiece(dest, piece);
        setPiece(source, null);
        numMoves++;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] moves = piece.getAvailableMoves(this, new Place(x, y));
                    Collections.addAll(allMoves, moves);
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }
}

// Class InvalidConfigurationError
class InvalidConfigurationError extends Error {
    public InvalidConfigurationError(String message) {
        super(message);
    }
}