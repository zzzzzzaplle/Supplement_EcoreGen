import java.util.ArrayList;

public class JesonMor extends Game {

    public JesonMor() {
        super();
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        this.board = new Piece[configuration.getSize()][];
        for (int x = 0; x < configuration.getSize(); x++) {
            this.board[x] = new Piece[configuration.getSize()];
            for (int y = 0; y < configuration.getSize(); y++) {
                this.board[x][y] = null;
            }
        }
        // Copy initial board
        for (int x = 0; x < configuration.getSize(); x++) {
            System.arraycopy(configuration.getInitialBoard()[x], 0, this.board[x], 0, configuration.getSize());
        }
        this.currentPlayer = configuration.getPlayers()[0];
    }

    public Player start() {
        while (true) {
            Player winner = tryPlayTurn();
            if (winner != null) {
                return winner;
            }
        }
    }

    private Player tryPlayTurn() {
        Move[] availableMoves = getAvailableMoves(currentPlayer);

        if (availableMoves == null || availableMoves.length == 0) {
            return getWinnerByScore();
        }

        Move chosenMove = currentPlayer.nextMove(this, availableMoves);

        if (chosenMove == null) {
            return null;
        }

        if (isValidMove(chosenMove)) {
            movePiece(chosenMove);
            updateScore(currentPlayer, getPiece(chosenMove.getSource()), chosenMove);
            numMoves++;

            refreshOutput();

            Player winner = getWinner(currentPlayer, getPiece(chosenMove.getDestination()), chosenMove);
            if (winner != null) {
                return winner;
            }
        }

        Player[] players = configuration.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(currentPlayer)) {
                currentPlayer = players[(i + 1) % players.length];
                break;
            }
        }

        return null;
    }

    private Player getWinnerByScore() {
        Player[] players = configuration.getPlayers();
        int score0 = players[0].getScore();
        int score1 = players[1].getScore();

        if (score0 > score1) {
            return players[0];
        } else if (score1 > score0) {
            return players[1];
        } else {
            return currentPlayer;
        }
    }

    private boolean isValidMove(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();

        if (!new VacantRule().validate(this, move)) return false;
        if (!new NilMoveRule().validate(this, move)) return false;
        if (!new OutOfBoundaryRule().validate(this, move)) return false;
        if (!new OccupiedRule().validate(this, move)) return false;
        if (!new FirstNMovesProtectionRule(configuration.getNumMovesProtection()).validate(this, move)) return false;

        Piece piece = getPiece(source);
        if (piece instanceof Knight) {
            if (!new KnightMoveRule().validate(this, move)) return false;
            if (!new KnightBlockRule().validate(this, move)) return false;
        } else if (piece instanceof Archer) {
            if (!new ArcherMoveRule().validate(this, move)) return false;
        }

        return true;
    }

    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }

        if (lastPiece != null && lastPiece instanceof Knight) {
            Place source = lastMove.getSource();
            Place destination = lastMove.getDestination();
            if (source.equals(configuration.getCentralPlace()) && !destination.equals(configuration.getCentralPlace())) {
                return lastPlayer;
            }
        }

        int player0Count = 0;
        int player1Count = 0;

        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null) {
                    if (piece.getPlayer().equals(configuration.getPlayers()[0])) {
                        player0Count++;
                    } else if (piece.getPlayer().equals(configuration.getPlayers()[1])) {
                        player1Count++;
                    }
                }
            }
        }

        if (player0Count == 0 && player1Count > 0) {
            return configuration.getPlayers()[1];
        }
        if (player1Count == 0 && player0Count > 0) {
            return configuration.getPlayers()[0];
        }
        if (player0Count == 0 && player1Count == 0) {
            return getWinnerByScore();
        }

        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int manhattanDistance = Math.abs(destination.x() - source.x()) + Math.abs(destination.y() - source.y());
        player.setScore(player.getScore() + manhattanDistance);
    }

    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();

        Piece piece = getPiece(source);
        setPiece(source, null);
        setPiece(destination, piece);
    }

    public Move[] getAvailableMoves(Player player) {
        if (player == null) {
            return new Move[0];
        }

        ArrayList<Move> allAvailableMoves = new ArrayList<>();

        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    for (Move m : pieceMoves) {
                        allAvailableMoves.add(m);
                    }
                }
            }
        }

        return allAvailableMoves.toArray(new Move[0]);
    }

    @Override
    public JesonMor clone() throws CloneNotSupportedException {
        JesonMor cloned = (JesonMor) super.clone();
        return cloned;
    }
}
