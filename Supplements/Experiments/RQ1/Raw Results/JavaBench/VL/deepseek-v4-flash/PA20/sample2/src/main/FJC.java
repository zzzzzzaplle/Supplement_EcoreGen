import java.util.*;
import java.util.stream.Collectors;

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
        return configuration.getCentralPlace();
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

    public abstract Player start();

    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);

    public abstract void updateScore(Player player, Piece piece, Move move);

    public abstract void movePiece(Move move);

    public abstract Move[] getAvailableMoves(Player player);
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
            Player player = this.currentPlayer;
            Move[] availableMoves = getAvailableMoves(player);
            if (availableMoves.length == 0) {
                // No available moves for current player
                Player opponent = (player.equals(configuration.getPlayers()[0])) ? configuration.getPlayers()[1] : configuration.getPlayers()[0];
                if (player.getScore() > opponent.getScore()) {
                    return player;
                } else if (opponent.getScore() > player.getScore()) {
                    return opponent;
                } else {
                    return player; // scores equal, current player wins
                }
            }
            Move move = player.nextMove(this, availableMoves);
            if (move == null) {
                continue;
            }
            // Validate move through rules
            List<Rule> rules = createRules();
            boolean valid = true;
            for (Rule rule : rules) {
                if (!rule.validate(this, move)) {
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                continue;
            }
            Piece piece = getPiece(move.getSource());
            movePiece(move);
            numMoves++;
            updateScore(player, piece, move);
            Player winner = getWinner(player, piece, move);
            if (winner != null) {
                refreshOutput();
                return winner;
            }
            // Switch player
            if (currentPlayer.equals(configuration.getPlayers()[0])) {
                currentPlayer = configuration.getPlayers()[1];
            } else {
                currentPlayer = configuration.getPlayers()[0];
            }
        }
    }

    private List<Rule> createRules() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OutOfBoundaryRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));
        rules.add(new KnightMoveRule());
        rules.add(new KnightBlockRule());
        rules.add(new ArcherMoveRule());
        return rules;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        // Check if a Knight left the central square
        if (lastPiece instanceof Knight) {
            Place central = configuration.getCentralPlace();
            if (lastMove.getSource().equals(central) && !lastMove.getDestination().equals(central)) {
                return lastPlayer;
            }
        }
        // Check if only one player's pieces remain
        Player opponent = (lastPlayer.equals(configuration.getPlayers()[0])) ? configuration.getPlayers()[1] : configuration.getPlayers()[0];
        boolean opponentPiecesExist = false;
        boolean lastPlayerPiecesExist = false;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = getPiece(x, y);
                if (p != null) {
                    if (p.getPlayer().equals(opponent)) {
                        opponentPiecesExist = true;
                    }
                    if (p.getPlayer().equals(lastPlayer)) {
                        lastPlayerPiecesExist = true;
                    }
                }
            }
        }
        if (!opponentPiecesExist && lastPlayerPiecesExist) {
            return lastPlayer;
        }
        if (!lastPlayerPiecesExist && opponentPiecesExist) {
            return opponent;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().x() - move.getDestination().x()) + Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        setPiece(move.getDestination(), piece);
        setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    for (Move move : pieceMoves) {
                        // Apply global rules
                        List<Rule> rules = new ArrayList<>();
                        rules.add(new VacantRule());
                        rules.add(new NilMoveRule());
                        rules.add(new OutOfBoundaryRule());
                        rules.add(new OccupiedRule());
                        rules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));
                        if (piece instanceof Knight) {
                            rules.add(new KnightMoveRule());
                            rules.add(new KnightBlockRule());
                        } else if (piece instanceof Archer) {
                            rules.add(new ArcherMoveRule());
                        }
                        boolean valid = true;
                        for (Rule rule : rules) {
                            if (!rule.validate(this, move)) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
                            moves.add(move);
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

class Knight extends Piece {
    public Knight() {
    }

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int[][] offsets = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        for (int[] offset : offsets) {
            int destX = source.x() + offset[0];
            int destY = source.y() + offset[1];
            if (destX >= 0 && destX < game.getConfiguration().getSize() && destY >= 0 && destY < game.getConfiguration().getSize()) {
                Place dest = new Place(destX, destY);
                moves.add(new Move(source, dest));
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

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int x = source.x() + dir[0];
            int y = source.y() + dir[1];
            while (x >= 0 && x < game.getConfiguration().getSize() && y >= 0 && y < game.getConfiguration().getSize()) {
                Place dest = new Place(x, y);
                moves.add(new Move(source, dest));
                x += dir[0];
                y += dir[1];
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

class ConsolePlayer extends Player {
    public ConsolePlayer() {
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // In a real implementation, this would read from console.
        // For simplicity, return null to indicate no valid input.
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

class Place {
    private int x;
    private int y;

    public Place() {
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
        return "(" + x + ", " + y + ")";
    }

    @Override
    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
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

    public String getAnsiColor() {
        return ansiColor;
    }

    @Override
    public String toString() {
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
        if (game.getPiece(move.getSource()) == null) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}

class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece == null) {
            return true;
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece == null) {
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
        if (source.x() < 0 || source.x() >= size || source.y() < 0 || source.y() >= size) {
            return false;
        }
        if (dest.x() < 0 || dest.x() >= size || dest.y() < 0 || dest.y() >= size) {
            return false;
        }
        return true;
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
        if (game.getNumMoves() < numProtectedMoves) {
            // Check if destination has a piece (capture attempt)
            if (game.getPiece(move.getDestination()) != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}

class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        // Knight moves in L shape: (2,1) or (1,2)
        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
            return true;
        }
        return false;
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
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        if (absDx == 2 && absDy == 1) {
            // blocking square at ((source.x + destination.x)/2, source.y)
            int blockX = (move.getSource().x() + move.getDestination().x()) / 2;
            int blockY = move.getSource().y();
            if (game.getPiece(blockX, blockY) != null) {
                return false;
            }
        } else if (absDx == 1 && absDy == 2) {
            // blocking square at (source.x, (source.y + destination.y)/2)
            int blockX = move.getSource().x();
            int blockY = (move.getSource().y() + move.getDestination().y()) / 2;
            if (game.getPiece(blockX, blockY) != null) {
                return false;
            }
        }
        return true;
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
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        // Must be orthogonal
        if (dx != 0 && dy != 0) {
            return false;
        }
        int stepX = (dx == 0) ? 0 : (dx > 0 ? 1 : -1);
        int stepY = (dy == 0) ? 0 : (dy > 0 ? 1 : -1);
        int x = move.getSource().x() + stepX;
        int y = move.getSource().y() + stepY;
        int piecesBetween = 0;
        while (x != move.getDestination().x() || y != move.getDestination().y()) {
            if (game.getPiece(x, y) != null) {
                piecesBetween++;
            }
            x += stepX;
            y += stepY;
        }
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece == null) {
            // Non-capturing move: path must be clear
            return piecesBetween == 0;
        } else {
            // Capturing move: exactly one piece between (the screen)
            return piecesBetween == 1;
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