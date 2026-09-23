import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JesonMor extends Game {

    private ArrayList<Rule> rules;

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.board = new Piece[configuration.getSize()][];
        for (int x = 0; x < configuration.getSize(); x++) {
            this.board[x] = new Piece[configuration.getSize()];
            for (int y = 0; y < configuration.getSize(); y++) {
                this.board[x][y] = null;
            }
        }
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = configuration.getInitialBoard()[x][y];
                if (piece != null) {
                    this.board[x][y] = piece;
                }
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;

        int boardSize = configuration.getSize();
        this.rules = new ArrayList<>();
        this.rules.add(new VacantRule());
        this.rules.add(new NilMoveRule());
        this.rules.add(new OutOfBoundaryRule(boardSize));
        this.rules.add(new OccupiedRule());
        this.rules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));
    }

    public Player start() {
        refreshOutput();
        Player player1 = configuration.getPlayers()[0];
        Player player2 = configuration.getPlayers()[1];
        while (true) {
            Move[] availables = getAvailableMoves(this.currentPlayer);
            if (availables.length == 0) {
                return this.getWinner(this.currentPlayer, null, null);
            }
            Move move = this.currentPlayer.nextMove(this, availables);
            if (move != null) {
                this.movePiece(move);
                this.updateScore(this.currentPlayer, this.getPiece(move.getSource()), move);
                this.currentPlayer = this.currentPlayer.equals(player1) ? player2 : player1;
                refreshOutput();
            }
        }
    }

    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < configuration.getNumMovesProtection()) {
            return this.configuration.getPlayers()[0];
        }

        int player1Pieces = 0;
        int player2Pieces = 0;
        int size = this.configuration.getSize();
        Player player1 = this.configuration.getPlayers()[0];
        Player player2 = this.configuration.getPlayers()[1];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null) {
                    if (piece.getPlayer().equals(player1)) {
                        player1Pieces++;
                    } else if (piece.getPlayer().equals(player2)) {
                        player2Pieces++;
                    }
                }
            }
        }

        if (lastPiece != null && lastPiece instanceof Knight && lastMove != null) {
            Place source = lastMove.getSource();
            Place destination = lastMove.getDestination();
            if (this.configuration.getCentralPlace().equals(source) && !this.configuration.getCentralPlace().equals(destination)) {
                return this.currentPlayer;
            }
        }

        if (player1Pieces == 0 && player2Pieces == 0) {
            if (player1.getScore() > player2.getScore()) {
                return player1;
            } else if (player2.getScore() > player1.getScore()) {
                return player2;
            } else {
                return this.currentPlayer;
            }
        }

        if (player1Pieces == 0) {
            return player2;
        }

        if (player2Pieces == 0) {
            return player1;
        }

        return this.currentPlayer;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();
        int manhattanDistance = Math.abs(sourceX - destX) + Math.abs(sourceY - destY);
        player.setScore(player.getScore() + manhattanDistance);
    }

    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece piece = this.board[source.x()][source.y()];
        this.board[destination.x()][destination.y()] = piece;
        this.board[source.x()][source.y()] = null;
        this.numMoves++;
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> allMoves = new ArrayList<>();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place place = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, place);
                    for (Move mv : pieceMoves) {
                        boolean valid = true;
                        for (Rule rule : this.rules) {
                            if (rule instanceof KnightMoveRule && !(piece instanceof Knight)) {
                                continue;
                            }
                            if (rule instanceof KnightBlockRule && !(piece instanceof Knight)) {
                                continue;
                            }
                            if (rule instanceof ArcherMoveRule && !(piece instanceof Archer)) {
                                continue;
                            }
                            if (!rule.validate(this, mv)) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
                            allMoves.add(mv);
                        }
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }

    public Piece getPiece(Place place) {
        return this.board[place.x()][place.y()];
    }

    public Piece getPiece(int x, int y) {
        return this.getPiece(new Place(x, y));
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
}
