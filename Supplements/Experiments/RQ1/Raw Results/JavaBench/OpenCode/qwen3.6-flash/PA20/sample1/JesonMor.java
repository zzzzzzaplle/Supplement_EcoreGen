import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.stream.Collectors;

public class JesonMor extends Game {

    private Rule[] rules;

    public JesonMor() {}

    public JesonMor(Configuration configuration) {
        super(configuration);
        this.rules = new Rule[]{
            new VacantRule(),
            new NilMoveRule(),
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new FirstNMovesProtectionRule(),
            new KnightMoveRule(),
            new KnightBlockRule(),
            new ArcherMoveRule()
        };
    }

    public Player start() {
        while (true) {
            refreshOutput();
            Move[] availableMoves = getAvailableMoves(currentPlayer);

            if (availableMoves.length == 0) {
                refreshOutput();
                System.out.println("Player " + currentPlayer.getName() + " has no available moves.");
                Player winner;
                Player otherPlayer = currentPlayer == configuration.getPlayers()[0] ?
                    configuration.getPlayers()[1] : configuration.getPlayers()[0];
                if (currentPlayer.getScore() > otherPlayer.getScore()) {
                    winner = currentPlayer;
                } else if (currentPlayer.getScore() < otherPlayer.getScore()) {
                    winner = otherPlayer;
                } else {
                    winner = currentPlayer;
                }
                System.out.println("Winner is " + winner.getName() + " with score " + winner.getScore());
                return winner;
            }

            Move selectedMove = currentPlayer.nextMove(this, availableMoves);
            if (selectedMove == null) {
                continue;
            }

            if (validateMove(selectedMove)) {
                movePiece(selectedMove);
                updateScore(currentPlayer, null, selectedMove);
                Piece movedPiece = getPiece(selectedMove.getDestination());

                Player winner = getWinner(currentPlayer, movedPiece, selectedMove);
                if (winner != null) {
                    refreshOutput();
                    System.out.println("Winner is " + winner.getName() + " with score " + winner.getScore());
                    return winner;
                }

                numMoves++;
                currentPlayer = currentPlayer == configuration.getPlayers()[0] ?
                    configuration.getPlayers()[1] : configuration.getPlayers()[0];
            }
        }
    }

    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (numMoves < configuration.getNumMovesProtection()) {
            return null;
        }

        // Check if a Knight left the central square
        Place central = getCentralPlace();
        if (lastMove != null) {
            if (lastMove.getSource().equals(central) && !lastMove.getDestination().equals(central)) {
                if (lastPiece instanceof Knight) {
                    return lastPlayer;
                }
            }
        }

        // Check if only one player's pieces remain
        Player player1 = configuration.getPlayers()[0];
        Player player2 = configuration == null || configuration.getPlayers().length < 2 ? null : configuration.getPlayers()[1];
        if (player2 != null) {
            int p1Pieces = countPiecesForPlayer(player1);
            int p2Pieces = countPiecesForPlayer(player2);

            if (p2Pieces == 0 && p1Pieces > 0) {
                return player1;
            }
            if (p1Pieces == 0 && p2Pieces > 0) {
                return player2;
            }
        }

        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        if (move != null) {
            int manhattanDistance = Math.abs(move.getSource().x() - move.getDestination().x()) +
                Math.abs(move.getSource().y() - move.getDestination().y());
            player.setScore(player.getScore() + manhattanDistance);
        }
    }

    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        setPiece(move.getDestination(), piece);
        setPiece(move.getSource(), null);
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> allAvailable = new ArrayList<>();
        int size = configuration.getSize();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer() != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    for (Move m : pieceMoves) {
                        if (isMoveValidForPlayer(m, player)) {
                            allAvailable.add(m);
                        }
                    }
                }
            }
        }

        return allAvailable.toArray(new Move[0]);
    }

    private boolean isMoveValidForPlayer(Move move, Player player) {
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }

        // Apply piece-specific rules
        Piece pieceAtSource = this.getPiece(move.getSource());
        if (pieceAtSource instanceof Knight) {
            if (!(rules[5] instanceof KnightMoveRule) || !((KnightMoveRule) rules[5]).validate(this, move)) {
                return false;
            }
            if (!(rules[6] instanceof KnightBlockRule) || !((KnightBlockRule) rules[6]).validate(this, move)) {
                return false;
            }
        }
        if (pieceAtSource instanceof Archer) {
            if (!(rules[7] instanceof ArcherMoveRule) || !((ArcherMoveRule) rules[7]).validate(this, move)) {
                return false;
            }
        }

        return true;
    }

    private boolean validateMove(Move move) {
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                refreshOutput();
                System.out.println("Invalid move: " + rule.getDescription());
                System.out.println("Please try again.");
                return false;
            }
        }
        return true;
    }

    private int countPiecesForPlayer(Player player) {
        int count = 0;
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer() != null && piece.getPlayer().equals(player)) {
                    count++;
                }
            }
        }
        return count;
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

    public Piece getPiece(Place place) {
        return this.board[place.x()][place.y()];
    }

    public Piece getPiece(int x, int y) {
        return this.getPiece(new Place(x, y));
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
