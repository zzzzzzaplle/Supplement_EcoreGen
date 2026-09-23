import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    public JesonMor() {
        super();
    }

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        Player winner = null;
        this.numMoves = 0;
        Player[] players = this.configuration.getPlayers();
        for (Player p : players) {
            p.setScore(0);
        }
        int size = this.configuration.getSize();
        Piece[][] initial = this.configuration.getInitialBoard();
        this.board = new Piece[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.board[x][y] = initial[x][y];
            }
        }
        this.currentPlayer = players[0];
        this.refreshOutput();
        int turnIndex = 0;
        while (true) {
            this.currentPlayer = players[turnIndex % players.length];
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                winner = decideWinnerByScore(this.currentPlayer);
                break;
            }
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                winner = decideWinnerByScore(this.currentPlayer);
                break;
            }
            Piece movingPiece = this.getPiece(move.getSource());
            this.movePiece(move);
            this.updateScore(this.currentPlayer, movingPiece, move);
            this.numMoves++;
            this.refreshOutput();
            winner = this.getWinner(this.currentPlayer, movingPiece, move);
            if (winner != null) {
                break;
            }
            turnIndex++;
        }
        if (winner != null) {
            System.out.printf("Congratulations! %s%s%s wins!\n",
                    winner.getColor(), winner.getName(), Color.DEFAULT);
        }
        return winner;
    }

    public Player decideWinnerByScore(Player current) {
        Player[] players = this.configuration.getPlayers();
        Player other = null;
        for (Player p : players) {
            if (!p.equals(current)) {
                other = p;
                break;
            }
        }
        if (other == null) {
            return current;
        }
        if (current.getScore() > other.getScore()) {
            return current;
        }
        if (other.getScore() > current.getScore()) {
            return other;
        }
        return current;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves <= this.configuration.getNumMovesProtection()) {
            return null;
        }
        Place central = this.configuration.getCentralPlace();
        if (lastPiece instanceof Knight
                && lastMove.getSource().equals(central)
                && !lastMove.getDestination().equals(central)) {
            return lastPlayer;
        }
        Player[] players = this.configuration.getPlayers();
        boolean[] hasPiece = new boolean[players.length];
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece == null) {
                    continue;
                }
                for (int i = 0; i < players.length; i++) {
                    if (piece.getPlayer().equals(players[i])) {
                        hasPiece[i] = true;
                        break;
                    }
                }
            }
        }
        int aliveCount = 0;
        Player aliveOne = null;
        for (int i = 0; i < players.length; i++) {
            if (hasPiece[i]) {
                aliveCount++;
                aliveOne = players[i];
            }
        }
        if (aliveCount == 1) {
            return aliveOne;
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
        Piece piece = this.getPiece(move.getSource());
        this.setPiece(move.getDestination(), piece);
        this.setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece == null || !piece.getPlayer().equals(player)) {
                    continue;
                }
                Place source = new Place(x, y);
                Move[] pieceMoves = piece.getAvailableMoves(this, source);
                if (pieceMoves != null) {
                    for (Move m : pieceMoves) {
                        moves.add(m);
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}
