import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {

    private static Rule[] GLOBAL_RULES = new Rule[] {
            new VacantRule(),
            new NilMoveRule(),
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new KnightMoveRule(),
            new KnightBlockRule(),
            new ArcherMoveRule()
    };

    private Rule[] protectionRules;

    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        super(configuration);
        this.protectionRules = new Rule[] {
                new FirstNMovesProtectionRule(configuration.getNumMovesProtection())
        };
    }

    private boolean passesRules(Move move) {
        if (move == null || move.getSource() == null || move.getDestination() == null) {
            return false;
        }
        for (Rule r : GLOBAL_RULES) {
            if (!r.validate(this, move)) {
                return false;
            }
        }
        for (Rule r : protectionRules) {
            if (!r.validate(this, move)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Player start() {
        refreshOutput();
        if (this.configuration == null || this.configuration.getPlayers() == null
                || this.configuration.getPlayers().length < 2) {
            return null;
        }
        Player[] players = this.configuration.getPlayers();
        this.currentPlayer = players[0];
        Player winner = null;
        Piece lastPiece = null;
        Move lastMove = null;
        while (true) {
            Move[] available = getAvailableMoves(this.currentPlayer);
            if (available == null || available.length == 0) {
                winner = winnerByScore();
                break;
            }
            Move chosen = this.currentPlayer.nextMove(this, available);
            if (chosen == null) {
                winner = winnerByScore();
                break;
            }
            Piece movingPiece = getPiece(chosen.getSource());
            if (passesRules(chosen)) {
                movePiece(chosen);
                updateScore(this.currentPlayer, movingPiece, chosen);
                this.numMoves++;
                lastPiece = movingPiece;
                lastMove = chosen;
                refreshOutput();
                winner = getWinner(this.currentPlayer, lastPiece, lastMove);
                if (winner != null) {
                    break;
                }
            } else {
                lastPiece = movingPiece;
                lastMove = chosen;
                refreshOutput();
                winner = getWinner(this.currentPlayer, lastPiece, lastMove);
                if (winner != null) {
                    break;
                }
            }
            this.currentPlayer = (this.currentPlayer.equals(players[0])) ? players[1] : players[0];
        }
        if (winner != null) {
            System.out.println("Winner: " + winner.getName());
        } else {
            Player w = winnerByScore();
            if (w != null) {
                System.out.println("Winner: " + w.getName());
                winner = w;
            }
        }
        return winner;
    }

    private Player winnerByScore() {
        if (this.configuration == null || this.configuration.getPlayers() == null) {
            return null;
        }
        Player[] players = this.configuration.getPlayers();
        if (players.length < 2) {
            return players[0];
        }
        if (players[0].getScore() == players[1].getScore()) {
            return this.currentPlayer != null ? this.currentPlayer : players[0];
        }
        return players[0].getScore() > players[1].getScore() ? players[0] : players[1];
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.configuration == null) {
            return null;
        }
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        if (lastPiece instanceof Knight && lastMove != null
                && lastMove.getSource() != null && lastMove.getDestination() != null) {
            Place central = this.configuration.getCentralPlace();
            if (central != null
                    && lastMove.getSource().equals(central)
                    && !lastMove.getDestination().equals(central)) {
                return lastPlayer;
            }
        }
        Player[] players = this.configuration.getPlayers();
        if (players == null || players.length < 2) {
            return null;
        }
        int player0Pieces = countPieces(players[0]);
        int player1Pieces = countPieces(players[1]);
        if (player0Pieces == 0 && player1Pieces > 0) {
            return players[1];
        }
        if (player1Pieces == 0 && player0Pieces > 0) {
            return players[0];
        }
        return null;
    }

    private int countPieces(Player player) {
        if (this.board == null) {
            return 0;
        }
        int count = 0;
        for (int x = 0; x < this.board.length; x++) {
            for (int y = 0; y < this.board[x].length; y++) {
                Piece p = this.board[x][y];
                if (p != null && p.getPlayer() != null && p.getPlayer().equals(player)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        if (player == null || move == null
                || move.getSource() == null || move.getDestination() == null) {
            return;
        }
        int distance = Math.abs(move.getSource().x() - move.getDestination().x())
                + Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        if (move == null || move.getSource() == null || move.getDestination() == null) {
            return;
        }
        Piece piece = getPiece(move.getSource());
        setPiece(move.getSource(), null);
        setPiece(move.getDestination(), piece);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> result = new ArrayList<Move>();
        if (this.board == null || player == null) {
            return new Move[0];
        }
        for (int x = 0; x < this.board.length; x++) {
            for (int y = 0; y < this.board[x].length; y++) {
                Piece piece = this.board[x][y];
                if (piece == null || piece.getPlayer() == null
                        || !piece.getPlayer().equals(player)) {
                    continue;
                }
                Place source = new Place(x, y);
                Move[] candidates = piece.getAvailableMoves(this, source);
                if (candidates == null) {
                    continue;
                }
                for (Move m : candidates) {
                    if (passesRules(m)) {
                        result.add(m);
                    }
                }
            }
        }
        return result.toArray(new Move[0]);
    }
}
