import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeration for piece/player colors.
 */
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

    private String ansiColor;

    Color() {
        switch (this) {
            case BLACK: this.ansiColor = "\033[0;30m"; break;
            case RED: this.ansiColor = "\033[0;31m"; break;
            case GREEN: this.ansiColor = "\033[0;32m"; break;
            case YELLOW: this.ansiColor = "\033[0;33m"; break;
            case BLUE: this.ansiColor = "\033[0;34m"; break;
            case PURPLE: this.ansiColor = "\033[0;35m"; break;
            case CYAN: this.ansiColor = "\033[0;36m"; break;
            case WHITE: this.ansiColor = "\033[0;37m"; break;
            default: this.ansiColor = "\033[0m"; break;
        }
    }

    public String toString() {
        return ansiColor;
    }
}

/**
 * Represents a coordinate on the board.
 */
class Place implements Cloneable {
    private int x;
    private int y;

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int y() {
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
    public Place clone() {
        try {
            return (Place) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * Represents a move from one place to another.
 */
class Move implements Cloneable {
    private Place source;
    private Place destination;

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
        return new java.util.StringJoiner(", ", Move.class.getSimpleName() + "[", "]")
                .add("source=" + source)
                .add("destination=" + destination)
                .toString();
    }

    @Override
    public Move clone() {
        try {
            Move cloned = (Move) super.clone();
            cloned.source = this.source.clone();
            cloned.destination = this.destination.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
 * Rule: Source must not be vacant.
 */
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

/**
 * Rule: Destination cannot contain a friendly piece.
 */
class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece == null) return true; // Handled by VacantRule, but safe to return true here
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece == null) return true;
        return !destPiece.getPlayer().equals(sourcePiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}

/**
 * Rule: Destination must be within board boundaries.
 */
class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place dest = move.getDestination();
        return dest.x() >= 0 && dest.x() < game.getSize() &&
               dest.y() >= 0 && dest.y() < game.getSize();
    }

    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

/**
 * Rule: Source and destination must be different.
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
 * Rule: Prevents capturing pieces during the first N moves.
 */
class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() < numProtectedMoves) {
            Piece sourcePiece = game.getPiece(move.getSource());
            Piece destPiece = game.getPiece(move.getDestination());
            // If capturing, it is invalid during protection phase
            if (sourcePiece != null && destPiece != null && 
                !sourcePiece.getPlayer().equals(destPiece.getPlayer())) {
                return false;
            }
        }
        return true;
    }

    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}

/**
 * Rule: Validates Knight move shape (L-shape).
 */
class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) return true;

        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}

/**
 * Rule: Validates that Knight is not blocked by another piece.
 */
class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) return true;

        Place source = move.getSource();
        Place dest = move.getDestination();
        
        int dx = dest.x() - source.x();
        int dy = dest.y() - source.y();
        
        int blockX, blockY;
        
        // If moving 2 horizontally, block is at (source.x + dx/2, source.y)
        // If moving 1 horizontally, block is at (source.x, source.y + dy/2)
        if (Math.abs(dx) == 2) {
            blockX = source.x() + dx / 2;
            blockY = source.y();
        } else {
            blockX = source.x();
            blockY = source.y() + dy / 2;
        }
        
        Place blockPlace = new Place(blockX, blockY);
        Piece blocker = game.getPiece(blockPlace);
        
        return blocker == null;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}

/**
 * Rule: Validates Archer move (orthogonal path and screen for capture).
 */
class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        
        Place source = move.getSource();
        Place dest = move.getDestination();
        
        // Archer moves orthogonally
        if (source.x() != dest.x() && source.y() != dest.y()) {
            return false;
        }
        
        int dx = dest.x() - source.x();
        int dy = dest.y() - source.y();
        
        int stepsX = (dx == 0) ? 0 : (dx / Math.abs(dx));
        int stepsY = (dy == 0) ? 0 : (dy / Math.abs(dx)); // Wait, if dy!=0, dx must be 0. So stepsX=0.
        // Correction:
        if (dx == 0) {
            // Vertical move
            for (int i = 1; i < Math.abs(dy); i++) {
                Place p = new Place(source.x(), source.y() + stepsY * i); // stepsY is sign of dy
                // Actually simpler loop:
                int y = source.y() + (dy > 0 ? 1 : -1) * i;
                Piece p = game.getPiece(source.x(), y);
                if (p != null) {
                    // If non-capturing move, any piece blocks
                    if (game.getPiece(dest) == null) {
                        return false;
                    }
                    // If capturing move, exactly one piece (the screen) is allowed in between
                    // If we found a piece in between, it must be the ONLY piece in between
                    // And the destination must be occupied by an opponent
                    // But wait, if there are multiple pieces in between, it's invalid
                    // So if we found a piece, and there are more steps, it's invalid?
                    // Actually, standard Cannon capture: exactly one piece between source and dest.
                    // So if we find a piece, we continue checking. If we find another piece before dest, invalid.
                    // If we reach dest and found exactly one piece in between, valid.
                }
            }
            // Let's implement strictly:
            int piecesInBetween = 0;
            if (dx == 0) {
                 int startY = Math.min(source.y(), dest.y()) + 1;
                 int endY = Math.max(source.y(), dest.y());
                 for (int y = startY; y < endY; y++) {
                     if (game.getPiece(source.x(), y) != null) {
                         piecesInBetween++;
                     }
                 }
            } else {
                 int startX = Math.min(source.x(), dest.x()) + 1;
                 int endX = Math.max(source.x(), dest.x());
                 for (int x = startX; x < endX; x++) {
                     if (game.getPiece(x, source.y()) != null) {
                         piecesInBetween++;
                     }
                 }
            }
            
            Piece destPiece = game.getPiece(dest);
            if (destPiece == null) {
                // Non-capturing move: path must be clear
                return piecesInBetween == 0;
            } else {
                // Capturing move: exactly one piece in between (the screen)
                return piecesInBetween == 1;
            }
        }
        
        return false;
    }
    
    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}

/**
 * Abstract base class for pieces.
 */
abstract class Piece implements Cloneable {
    private Player player;

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
    public Piece clone() {
        try {
            Piece cloned = (Piece) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * Knight piece.
 */
class Knight extends Piece {
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
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};
        
        for (int i = 0; i < 8; i++) {
            int nx = source.x() + dx[i];
            int ny = source.y() + dy[i];
            if (nx >= 0 && nx < game.getSize() && ny >= 0 && ny < game.getSize()) {
                Place dest = new Place(nx, ny);
                moves.add(new Move(source, dest));
            }
        }
        return moves.toArray(new Move[0]);
    }
}

/**
 * Archer piece.
 */
class Archer extends Piece {
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
        int size = game.getSize();
        
        // Check 4 directions
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        
        for (int[] dir : dirs) {
            int nx = source.x() + dir[0];
            int ny = source.y() + dir[1];
            
            while (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                Place dest = new Place(nx, ny);
                Piece destPiece = game.getPiece(dest);
                
                if (destPiece == null) {
                    // Empty square: valid move if path is clear (handled by rule later, but we generate candidate)
                    // Actually, for archer, empty squares are valid destinations if path is clear.
                    // We add it as a candidate.
                    moves.add(new Move(source, dest));
                } else {
                    // Occupied square
                    // Non-capturing: cannot move here
                    // Capturing: can jump over if exactly one piece before this? 
                    // No, standard cannon: jump over exactly one piece to capture.
                    // So if we hit a piece, it could be a screen for a capture further.
                    // But we can stop generating moves in this direction after hitting a piece?
                    // No, we can jump over it.
                    // So we add this square? No, we can't land here unless it's an opponent and we have a screen.
                    // But we don't know if there's a screen behind us yet.
                    // Actually, the rule validation handles the specifics. We just need to generate potential moves.
                    // However, standard implementation: generate all squares until end of board? 
                    // No, that's too many.
                    // Let's generate all squares until end of board, let rules filter.
                    // Or generate until first piece, then one more?
                    // To be safe and simple, generate all squares in direction.
                    moves.add(new Move(source, dest));
                    
                    // If it's an opponent piece, we might capture it if there is a screen.
                    // If it's a friendly piece, we can't capture.
                    // We continue looking for more pieces to act as screens?
                    // Yes.
                }
                
                nx += dir[0];
                ny += dir[1];
            }
        }
        return moves.toArray(new Move[0]);
    }
}

/**
 * Abstract base class for players.
 */
abstract class Player implements Cloneable {
    private String name;
    private int score;
    private Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.score = 0;
        this.color = color;
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
        Player cloned = (Player) super.clone();
        return cloned;
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

/**
 * Player that takes input from console.
 */
class ConsolePlayer extends Player {
    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        // This method will be called by the game loop.
        // In a real scenario, it would read from System.in.
        // For now, we assume the game loop handles the I/O or this is a stub.
        // However, the requirements say "prompt the player".
        // Since we cannot easily interact in a headless test, we will implement a basic scanner.
        
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String input = "";
        while (true) {
            System.out.print(this.getName() + " (" + this.getColor() + "), enter move (e.g., a1->b2): ");
            if (scanner.hasNextLine()) {
                input = scanner.nextLine().trim();
            } else {
                continue;
            }
            
            // Parse input
            if (input.contains("->")) {
                String[] parts = input.split("->");
                if (parts.length == 2) {
                    String srcStr = parts[0].trim();
                    String destStr = parts[1].trim();
                    
                    Place src = parseCoordinate(srcStr, game.getSize());
                    Place dest = parseCoordinate(destStr, game.getSize());
                    
                    if (src != null && dest != null) {
                        Move candidate = new Move(src, dest);
                        // Check if this move is in available moves
                        for (Move m : availableMoves) {
                            if (m.equals(candidate)) {
                                return m;
                            }
                        }
                        System.out.println("Invalid move. Please try again.");
                    } else {
                        System.out.println("Invalid coordinates. Please try again.");
                    }
                } else {
                    System.out.println("Invalid format. Please use source->destination.");
                }
            } else {
                System.out.println("Invalid format. Please use source->destination.");
            }
        }
    }
    
    private Place parseCoordinate(String coord, int size) {
        if (coord.length() < 2) return null;
        char colChar = coord.charAt(0);
        if (colChar < 'a' || colChar >= 'a' + size) return null;
        
        try {
            int row = Integer.parseInt(coord.substring(1));
            int x = colChar - 'a';
            int y = row - 1;
            if (x >= 0 && x < size && y >= 0 && y < size) {
                return new Place(x, y);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }
}

/**
 * Player that makes random moves.
 */
class RandomPlayer extends Player {
    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            return null;
        }
        // Simple random selection
        int index = (int) (Math.random() * availableMoves.length);
        return availableMoves[index];
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

    public Game(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        // Initialize board from configuration
        this.board = new Piece[configuration.getSize()][];
        for (int i = 0; i < configuration.getSize(); i++) {
            this.board[i] = new Piece[configuration.getSize()];
            for (int j = 0; j < configuration.getSize(); j++) {
                this.board[i][j] = null;
            }
        }
        // Copy initial board
        Piece[][] initialBoard = configuration.getInitialBoard();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                if (initialBoard[x][y] != null) {
                    this.board[x][y] = initialBoard[x][y];
                }
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
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
        return configuration.getCentralPlace();
    }
    
    public int getSize() {
        return configuration.getSize();
    }
}

/**
 * Implementation of the JesonMor game.
 */
class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        // Initialize rules
        List<Rule> rules = new ArrayList<>();
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OutOfBoundaryRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));

        while (true) {
            refreshOutput();
            
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            
            if (availableMoves == null || availableMoves.length == 0) {
                // No moves available, determine winner by score
                break;
            }
            
            Move move = currentPlayer.nextMove(this, availableMoves);
            
            if (move == null) {
                continue; // Should not happen if availableMoves is not empty
            }
            
            // Validate move
            boolean valid = true;
            for (Rule rule : rules) {
                if (!rule.validate(this, move)) {
                    valid = false;
                    System.out.println("Invalid move: " + rule.getDescription());
                    break;
                }
                
                // Check piece-specific rules
                Piece piece = getPiece(move.getSource());
                if (piece instanceof Knight) {
                    if (rule instanceof KnightMoveRule) {
                        if (!rule.validate(this, move)) {
                            valid = false;
                            System.out.println("Invalid move: " + rule.getDescription());
                            break;
                        }
                    }
                    if (rule instanceof KnightBlockRule) {
                        if (!rule.validate(this, move)) {
                            valid = false;
                            System.out.println("Invalid move: " + rule.getDescription());
                            break;
                        }
                    }
                } else if (piece instanceof Archer) {
                    if (rule instanceof ArcherMoveRule) {
                        if (!rule.validate(this, move)) {
                            valid = false;
                            System.out.println("Invalid move: " + rule.getDescription());
                            break;
                        }
                    }
                }
            }
            
            if (valid) {
                movePiece(move);
                updateScore(currentPlayer, getPiece(move.getSource()), move);
                numMoves++;
                
                // Check win condition
                Player winner = getWinner(currentPlayer, getPiece(move.getSource()), move);
                if (winner != null) {
                    refreshOutput();
                    System.out.println("Game Over! Winner: " + winner.getName());
                    return winner;
                }
                
                // Switch player
                Player[] players = configuration.getPlayers();
                for (int i = 0; i < players.length; i++) {
                    if (players[i].equals(currentPlayer)) {
                        currentPlayer = players[(i + 1) % players.length];
                        break;
                    }
                }
            }
        }
        
        // Game ended because no moves available
        Player p1 = configuration.getPlayers()[0];
        Player p2 = configuration.getPlayers()[1];
        if (p1.getScore() > p2.getScore()) {
            System.out.println("Game Over! Winner: " + p1.getName());
            return p1;
        } else if (p2.getScore() > p1.getScore()) {
            System.out.println("Game Over! Winner: " + p2.getName());
            return p2;
        } else {
            // Scores equal, current player wins (as per requirement: "if scores are equal, the current player wins")
            // But wait, if no moves, it's the current player's turn? 
            // Requirement: "if a player has no available moves on their turn, the game ends and the winner is decided by score comparison"
            // "if scores are equal, the current player wins"
            System.out.println("Game Over! Winner: " + currentPlayer.getName() + " (Draw tie-breaker)");
            return currentPlayer;
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // No winner in protection phase
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        
        // Check Knight leaving central place
        Place centralPlace = getCentralPlace();
        if (lastPiece instanceof Knight) {
            if (lastMove.getSource().equals(centralPlace) && !lastMove.getDestination().equals(centralPlace)) {
                return lastPlayer;
            }
        }
        
        // Check if only one player's pieces remain
        int player1Count = 0;
        int player2Count = 0;
        Player[] players = configuration.getPlayers();
        
        for (int x = 0; x < getSize(); x++) {
            for (int y = 0; y < getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null) {
                    if (piece.getPlayer().equals(players[0])) {
                        player1Count++;
                    } else if (piece.getPlayer().equals(players[1])) {
                        player2Count++;
                    }
                }
            }
        }
        
        if (player1Count == 0 && player2Count > 0) {
            return players[1];
        } else if (player2Count == 0 && player1Count > 0) {
            return players[0];
        }
        
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        int distance = Math.abs(source.x() - dest.x()) + Math.abs(source.y() - dest.y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        
        Piece piece = getPiece(source);
        setPiece(source, null);
        setPiece(dest, piece);
    }
    
    private void setPiece(Place place, Piece piece) {
        this.board[place.x()][place.y()] = piece;
    }
    
    private void setPiece(int x, int y, Piece piece) {
        this.board[x][y] = piece;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<>();
        for (int x = 0; x < getSize(); x++) {
            for (int y = 0; y < getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] moves = piece.getAvailableMoves(this, new Place(x, y));
                    if (moves != null) {
                        for (Move m : moves) {
                            allMoves.add(m);
                        }
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }
}

/**
 * Configuration for the game.
 */
class Configuration implements Cloneable {
    private int size;
    private Player[] players;
    private Piece[][] initialBoard;
    private Place centralPlace;
    private int numMovesProtection;

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
 * Custom error class for invalid configurations.
 */
class InvalidConfigurationError extends Error {
    public InvalidConfigurationError(String message) {
        super(message);
    }
}