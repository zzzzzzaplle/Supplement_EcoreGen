import java.util.*;
import java.util.stream.Collectors;

abstract class Game {
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

    public Piece getPiece(Place place) {
        return this.board[place.getX()][place.getY()];
    }

    public Piece getPiece(int x, int y) {
        return this.getPiece(new Place(x, y));
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

class JesonMor extends Game {
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

    public JesonMor() {
    }

    @Override
    public Player start() {
        while (true) {
            refreshOutput();
            Player current = this.currentPlayer;
            Move[] availableMoves = getAvailableMoves(current);
            if (availableMoves.length == 0) {
                // No moves available, determine winner by score
                Player player1 = configuration.getPlayers()[0];
                Player player2 = configuration.getPlayers()[1];
                if (player1.getScore() > player2.getScore()) {
                    return player1;
                } else if (player2.getScore() > player1.getScore()) {
                    return player2;
                } else {
                    return current;
                }
            }
            Move chosenMove = current.nextMove(this, availableMoves);
            if (chosenMove == null) {
                continue;
            }
            // Validate move using rules
            if (!validateMove(chosenMove)) {
                continue;
            }
            movePiece(chosenMove);
            Piece movedPiece = getPiece(chosenMove.getDestination());
            updateScore(current, movedPiece, chosenMove);
            this.numMoves++;
            Player winner = getWinner(current, movedPiece, chosenMove);
            if (winner != null) {
                refreshOutput();
                return winner;
            }
            // Switch player
            if (current.equals(configuration.getPlayers()[0])) {
                this.currentPlayer = configuration.getPlayers()[1];
            } else {
                this.currentPlayer = configuration.getPlayers()[0];
            }
        }
    }

    private boolean validateMove(Move move) {
        // Apply all rules
        List<Rule> rules = new ArrayList<>();
        rules.add(new VacantRule());
        rules.add(new OccupiedRule());
        rules.add(new OutOfBoundaryRule());
        rules.add(new NilMoveRule());
        rules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));
        Piece piece = getPiece(move.getSource());
        if (piece instanceof Knight) {
            rules.add(new KnightMoveRule());
            rules.add(new KnightBlockRule());
        } else if (piece instanceof Archer) {
            rules.add(new ArcherMoveRule());
        }
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        // Check if a Knight leaves central square
        if (lastPiece instanceof Knight && lastMove.getSource().equals(getCentralPlace()) && !lastMove.getDestination().equals(getCentralPlace())) {
            return lastPlayer;
        }
        // Check if only one player's pieces remain
        Player otherPlayer = null;
        for (Player p : configuration.getPlayers()) {
            if (!p.equals(lastPlayer)) {
                otherPlayer = p;
                break;
            }
        }
        boolean lastPlayerHasPieces = false;
        boolean otherPlayerHasPieces = false;
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = getPiece(x, y);
                if (p != null) {
                    if (p.getPlayer().equals(lastPlayer)) {
                        lastPlayerHasPieces = true;
                    } else {
                        otherPlayerHasPieces = true;
                    }
                }
            }
        }
        if (!otherPlayerHasPieces) {
            return lastPlayer;
        }
        if (!lastPlayerHasPieces) {
            return otherPlayer;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().getX() - move.getDestination().getX()) +
                Math.abs(move.getSource().getY() - move.getDestination().getY());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        board[move.getDestination().getX()][move.getDestination().getY()] = piece;
        board[move.getSource().getX()][move.getSource().getY()] = null;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    for (Move m : pieceMoves) {
                        if (validateMove(m)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}

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

abstract class Piece {
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
}

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
        int[][] offsets = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        for (int[] offset : offsets) {
            int dx = offset[0];
            int dy = offset[1];
            int nx = source.getX() + dx;
            int ny = source.getY() + dy;
            if (nx >= 0 && nx < game.getConfiguration().getSize() && ny >= 0 && ny < game.getConfiguration().getSize()) {
                Place dest = new Place(nx, ny);
                // Check blocking
                int blockX, blockY;
                if (Math.abs(dx) == 2) {
                    blockX = (source.getX() + dest.getX()) / 2;
                    blockY = source.getY();
                } else {
                    blockX = source.getX();
                    blockY = (source.getY() + dest.getY()) / 2;
                }
                Place blockPlace = new Place(blockX, blockY);
                if (game.getPiece(blockPlace) == null) {
                    moves.add(new Move(source, dest));
                }
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

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int x = source.getX() + dx;
            int y = source.getY() + dy;
            int screenCount = 0;
            while (x >= 0 && x < size && y >= 0 && y < size) {
                Place dest = new Place(x, y);
                Piece destPiece = game.getPiece(dest);
                if (screenCount == 0) {
                    if (destPiece == null) {
                        // Non-capturing move
                        moves.add(new Move(source, dest));
                    } else {
                        // First screen encountered
                        screenCount++;
                    }
                } else if (screenCount == 1) {
                    if (destPiece != null) {
                        // Capturing move over screen
                        moves.add(new Move(source, dest));
                        break; // Cannot jump over more than one piece
                    }
                }
                x += dx;
                y += dy;
            }
        }
        return moves.toArray(new Move[0]);
    }
}

abstract class Player {
    protected String name;
    protected int score;
    protected Color color;

    public Player() {
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
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // In a real implementation, this would read from console.
        // For simplicity, return null to indicate no move (will be handled by game loop).
        return null;
    }
}

class RandomPlayer extends Player {
    public RandomPlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves.length == 0) {
            return null;
        }
        Random rand = new Random();
        return availableMoves[rand.nextInt(availableMoves.length)];
    }
}

class Move {
    private Place source;
    private Place destination;

    public Move() {
    }

    public Move(Place source, Place destination) {
        this.source = source;
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
}

class Place {
    private int x;
    private int y;

    public Place() {
    }

    public Place(int x, int y) {
        this.x = x;
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
        return "(" + x + ", " + y + ")";
    }

    @Override
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
}

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
}

interface Rule {
    boolean validate(Game game, Move move);

    String getDescription();
}

class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        return piece != null;
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
        Piece destPiece = game.getPiece(move.getDestination());
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

class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place source = move.getSource();
        Place dest = move.getDestination();
        return source.getX() >= 0 && source.getX() < size &&
                source.getY() >= 0 && source.getY() < size &&
                dest.getX() >= 0 && dest.getX() < size &&
                dest.getY() >= 0 && dest.getY() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

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

class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule() {
    }

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() < numProtectedMoves) {
            Piece destPiece = game.getPiece(move.getDestination());
            return destPiece == null;
        }
        return true;
    }

    @Override
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
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getSource().getX() - move.getDestination().getX());
        int dy = Math.abs(move.getSource().getY() - move.getDestination().getY());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}

class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().getX() - move.getSource().getX();
        int dy = move.getDestination().getY() - move.getSource().getY();
        int blockX, blockY;
        if (Math.abs(dx) == 2) {
            blockX = (move.getSource().getX() + move.getDestination().getX()) / 2;
            blockY = move.getSource().getY();
        } else {
            blockX = move.getSource().getX();
            blockY = (move.getSource().getY() + move.getDestination().getY()) / 2;
        }
        Place blockPlace = new Place(blockX, blockY);
        return game.getPiece(blockPlace) == null;
    }

    @Override
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
        // TODO implementation
        return false;
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