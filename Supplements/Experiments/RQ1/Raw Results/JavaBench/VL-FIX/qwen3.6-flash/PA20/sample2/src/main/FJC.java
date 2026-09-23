import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
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

    public abstract void movePiece(Move move) throws CloneNotSupportedException;

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
        return this.board[place.x()][place.y()];
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
        return this.configuration.getCentralPlace();
    }
}

class JesonMor extends Game {
    private java.util.Scanner scanner;

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.board = new Piece[configuration.getSize()][];
        for (int i = 0; i < configuration.getSize(); i++) {
            this.board[i] = new Piece[configuration.getSize()];
            for (int j = 0; j < configuration.getSize(); j++) {
                this.board[i][j] = null;
            }
        }
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                if (configuration.getInitialBoard()[x][y] != null) {
                    this.board[x][y] = configuration.getInitialBoard()[x][y];
                }
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
        this.scanner = new java.util.Scanner(System.in);
    }

    @Override
    public Player start() {
        while (true) {
            this.refreshOutput();
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves.length == 0) {
                // No moves available, decide winner by score
                break;
            }
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            try {
                this.movePiece(move);
            } catch (CloneNotSupportedException e) {
                // Should not happen in normal flow
                e.printStackTrace();
                break;
            }
            this.currentPlayer = (this.currentPlayer.equals(configuration.getPlayers()[0])) ? configuration.getPlayers()[1] : configuration.getPlayers()[0];
            
            Player winner = this.getWinner(this.currentPlayer, null, null);
            if (winner != null) {
                break;
            }
        }
        this.refreshOutput();
        return this.getWinner(null, null, null);
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // If within protection moves, no winner can be declared via knight or capture
        if (this.numMoves <= this.configuration.getNumMovesProtection()) {
            return null;
        }

        // Check if a Knight left the central square
        if (lastMove != null && lastPiece != null) {
            if (lastPiece instanceof Knight) {
                if (lastMove.getSource().equals(this.getCentralPlace()) && !lastMove.getDestination().equals(this.getCentralPlace())) {
                    return lastPiece.getPlayer();
                }
            }
        }

        // Check if only one player has pieces remaining
        Player player1 = this.configuration.getPlayers()[0];
        Player player2 = this.configuration.getPlayers()[1];
        boolean p1HasPieces = false;
        boolean p2HasPieces = false;

        for (int x = 0; x < this.configuration.getSize(); x++) {
            for (int y = 0; y < this.configuration.getSize(); y++) {
                Piece piece = this.board[x][y];
                if (piece != null) {
                    if (piece.getPlayer().equals(player1)) {
                        p1HasPieces = true;
                    } else if (piece.getPlayer().equals(player2)) {
                        p2HasPieces = true;
                    }
                }
            }
        }

        if (!p1HasPieces) {
            return player2;
        }
        if (!p2HasPieces) {
            return player1;
        }

        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        if (player == null || piece == null || move == null) {
            return;
        }
        int scoreIncrease = Math.abs(move.getSource().x() - move.getDestination().x()) +
                Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + scoreIncrease);
    }

    @Override
    public void movePiece(Move move) throws CloneNotSupportedException {
        // Validate rules
        List<Rule> rules = getRules();
        boolean valid = true;
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                valid = false;
                break;
            }
        }

        if (!valid) {
            return;
        }

        Piece piece = this.getPiece(move.getSource());
        if (piece == null) {
            return;
        }

        // Execute move
        this.setPiece(move.getSource(), null);
        this.setPiece(move.getDestination(), piece);

        // Update score
        this.updateScore(this.currentPlayer, piece, move);
        this.numMoves++;
    }

    private void setPiece(Place place, Piece piece) {
        this.board[place.x()][place.y()] = piece;
    }

    private List<Rule> getRules() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OutOfBoundaryRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()));

        // We need to know which player is moving to check specific rules later if needed, 
        // but piece specific rules depend on the piece type at source.
        // Since validate takes Game and Move, we can check the piece inside the rule.
        // However, KnightMoveRule etc are generic. We add them all, they check instanceof inside.
        rules.add(new KnightMoveRule());
        rules.add(new KnightBlockRule());
        rules.add(new ArcherMoveRule());

        return rules;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        int size = this.configuration.getSize();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    for (Move m : pieceMoves) {
                        // Filter by global rules
                        List<Rule> rules = getRules();
                        boolean valid = true;
                        for (Rule rule : rules) {
                            if (!rule.validate(this, m)) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
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

abstract class Piece {
    protected Player player;

    public Piece() {
    }

    public char getLabel() {
        return ' ';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        // Default implementation returns empty. Subclasses override.
        return new Move[0];
    }

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

    @Override
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getConfiguration().getSize();
        List<Move> moves = new ArrayList<>();
        
        // L-shape offsets
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};

        for (int i = 0; i < 8; i++) {
            int destX = source.x() + dx[i];
            int destY = source.y() + dy[i];

            if (destX >= 0 && destX < size && destY >= 0 && destY < size) {
                Place dest = new Place(destX, destY);
                // Check blocking condition for Knight
                if (isNotBlocked(game, source, dest)) {
                    moves.add(new Move(source, dest));
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    private boolean isNotBlocked(Game game, Place source, Place destination) {
        // If moving 2 horizontally, 1 vertically
        if (Math.abs(destination.x() - source.x()) == 2 && Math.abs(destination.y() - source.y()) == 1) {
            int blockX = (source.x() + destination.x()) / 2;
            int blockY = source.y();
            return game.getPiece(blockX, blockY) == null;
        }
        // If moving 1 horizontally, 2 vertically
        if (Math.abs(destination.x() - source.x()) == 1 && Math.abs(destination.y() - source.y()) == 2) {
            int blockX = source.x();
            int blockY = (source.y() + destination.y()) / 2;
            return game.getPiece(blockX, blockY) == null;
        }
        return true;
    }
}

class Archer extends Piece {
    public Archer() {
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getConfiguration().getSize();
        List<Move> moves = new ArrayList<>();

        // 4 directions: Up, Down, Left, Right
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        for (int d = 0; d < 4; d++) {
            int step = 1;
            while (true) {
                int destX = source.x() + dx[d] * step;
                int destY = source.y() + dy[d] * step;

                if (destX < 0 || destX >= size || destY < 0 || destY >= size) {
                    break;
                }

                Place dest = new Place(destX, destY);
                Piece targetPiece = game.getPiece(dest);

                if (targetPiece == null) {
                    // Vacant square, can move here
                    moves.add(new Move(source, dest));
                } else {
                    // Occupied square
                    // If it's the first piece encountered:
                    // - If it's an enemy, we can capture it (jump over it)
                    // - If it's a friend, we cannot move here or beyond
                    // If it's not the first piece (i.e., we've jumped over one):
                    // - We can capture the second piece if it's an enemy
                    // - Otherwise path is blocked

                    // Check if this is the first piece in this direction
                    // We need to check if there was any piece between source and dest-1
                    boolean hasScreen = false;
                    int checkStep = 1;
                    while (checkStep < step) {
                        int checkX = source.x() + dx[d] * checkStep;
                        int checkY = source.y() + dy[d] * checkStep;
                        if (game.getPiece(checkX, checkY) != null) {
                            hasScreen = true;
                            break;
                        }
                        checkStep++;
                    }

                    if (!hasScreen) {
                        // No screen yet.
                        // If enemy, can capture (add move)
                        // If friend, cannot move here (and blocked)
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            moves.add(new Move(source, dest));
                            // Can potentially jump over this one to capture next
                        } else {
                            // Blocked by friend
                            break;
                        }
                    } else {
                        // Has screen.
                        // If enemy, can capture
                        // If friend, blocked
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            moves.add(new Move(source, dest));
                        }
                        // Either way, path is blocked beyond this piece
                        break;
                    }
                }
                step++;
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
    private java.util.Scanner scanner;

    public ConsolePlayer() {
        this.scanner = new java.util.Scanner(System.in);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        while (true) {
            System.out.print("Enter move (e.g., a1->b2): ");
            String input = scanner.nextLine().trim();
            
            // Parse input
            String[] parts = input.split("->");
            if (parts.length != 2) {
                System.out.println("Invalid format. Please use source->destination.");
                continue;
            }
            
            String srcStr = parts[0].trim();
            String destStr = parts[1].trim();
            
            Place source = parseCoordinate(srcStr);
            Place destination = parseCoordinate(destStr);
            
            if (source == null || destination == null) {
                System.out.println("Invalid coordinates.");
                continue;
            }
            
            // Find matching move
            for (Move m : availableMoves) {
                if (m.getSource().equals(source) && m.getDestination().equals(destination)) {
                    return m;
                }
            }
            
            System.out.println("Move not available. Try again.");
        }
    }

    private Place parseCoordinate(String coord) {
        if (coord.length() < 2) return null;
        char colChar = coord.charAt(0);
        String rowStr = coord.substring(1);
        
        int col = colChar - 'a';
        try {
            int row = Integer.parseInt(rowStr) - 1;
            return new Place(col, row);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

class RandomPlayer extends Player {
    private java.util.Random random;

    public RandomPlayer() {
        this.random = new java.util.Random();
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves.length == 0) {
            return null;
        }
        int index = random.nextInt(availableMoves.length);
        return availableMoves[index];
    }
}

class Move {
    protected Place source;
    protected Place destination;

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
    protected int x;
    protected int y;

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
        return x == place.x &&
                y == place.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
    }
}

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

    private String ansiColor = "";

    public String toString() {
        if (this == BLACK) return "\u001b[30m";
        if (this == RED) return "\u001b[31m";
        if (this == GREEN) return "\u001b[32m";
        if (this == YELLOW) return "\u001b[33m";
        if (this == BLUE) return "\u001b[34m";
        if (this == PURPLE) return "\u001b[35m";
        if (this == CYAN) return "\u001b[36m";
        if (this == WHITE) return "\u001b[37m";
        return "\u001b[0m"; // Reset/Default
    }
}

interface Rule {
    public boolean validate(Game game, Move move);
    public String getDescription();
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
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece == null) {
            return true;
        }
        // Cannot move to a square occupied by a piece of the same player
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece != null && destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
            return false;
        }
        return true;
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
        return move.getSource().x() >= 0 && move.getSource().x() < size &&
               move.getSource().y() >= 0 && move.getSource().y() < size &&
               move.getDestination().x() >= 0 && move.getDestination().x() < size &&
               move.getDestination().y() >= 0 && move.getDestination().y() < size;
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
    protected int numProtectedMoves;

    public FirstNMovesProtectionRule() {
    }

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        // If within protection moves, check if it's a capture
        if (game.getNumMoves() < numProtectedMoves) {
            Piece destPiece = game.getPiece(move.getDestination());
            if (destPiece != null) {
                // It's a capture, which is not allowed
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
        
        // L shape: 2 and 1
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
        
        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();
        
        // Check blocking condition
        if (Math.abs(destX - srcX) == 2 && Math.abs(destY - srcY) == 1) {
            int blockX = (srcX + destX) / 2;
            int blockY = srcY;
            return game.getPiece(blockX, blockY) == null;
        }
        
        if (Math.abs(destX - srcX) == 1 && Math.abs(destY - srcY) == 2) {
            int blockX = srcX;
            int blockY = (srcY + destY) / 2;
            return game.getPiece(blockX, blockY) == null;
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
        return false;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}

// Helper class for invalid configuration
class InvalidConfigurationError extends RuntimeException {
    public InvalidConfigurationError(String message) {
        super(message);
    }
}