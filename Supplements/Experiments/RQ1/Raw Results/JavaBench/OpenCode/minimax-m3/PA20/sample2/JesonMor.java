import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    private Rule[] rules;

    public JesonMor() {
        super();
        this.rules = new Rule[]{
                new VacantRule(),
                new OccupiedRule(),
                new OutOfBoundaryRule(),
                new NilMoveRule(),
                new KnightMoveRule(),
                new KnightBlockRule(),
                new ArcherMoveRule()
        };
    }

    public JesonMor(Configuration configuration) {
        super(configuration);
        this.numMoves = 0;
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.rules = new Rule[]{
                new VacantRule(),
                new OccupiedRule(),
                new OutOfBoundaryRule(),
                new NilMoveRule(),
                new FirstNMovesProtectionRule(configuration.getNumMovesProtection()),
                new KnightMoveRule(),
                new KnightBlockRule(),
                new ArcherMoveRule()
        };
    }

    public Rule[] getRules() {
        return rules;
    }

    public void setRules(Rule[] rules) {
        this.rules = rules;
    }

    @Override
    public Player start() {
        Player winner = null;
        this.refreshOutput();
        while (winner == null) {
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves.length == 0) {
                winner = this.getWinner(null, null, null);
                break;
            }
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            Piece movedPiece = this.getPiece(move.getSource());
            if (!this.validateAllRules(move)) {
                continue;
            }
            this.movePiece(move);
            this.updateScore(this.currentPlayer, movedPiece, move);
            this.numMoves++;
            this.refreshOutput();
            winner = this.getWinner(this.currentPlayer, movedPiece, move);
            if (winner == null) {
                Player[] players = this.configuration.getPlayers();
                this.currentPlayer = (this.currentPlayer == players[0]) ? players[1] : players[0];
            }
        }
        return winner;
    }

    private boolean validateAllRules(Move move) {
        for (Rule rule : this.rules) {
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
        if (lastMove != null && lastPiece != null && lastPiece instanceof Knight) {
            Place central = this.configuration.getCentralPlace();
            if (lastMove.getSource().equals(central) && !lastMove.getDestination().equals(central)) {
                return lastPlayer;
            }
        }
        Player[] players = this.configuration.getPlayers();
        int count0 = 0;
        int count1 = 0;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece == null) {
                    continue;
                }
                if (piece.getPlayer().equals(players[0])) {
                    count0++;
                } else if (piece.getPlayer().equals(players[1])) {
                    count1++;
                }
            }
        }
        if (count0 == 0) {
            return players[1];
        }
        if (count1 == 0) {
            return players[0];
        }
        Move[] available = this.getAvailableMoves(this.currentPlayer);
        if (available.length == 0) {
            if (players[0].getScore() > players[1].getScore()) {
                return players[0];
            } else if (players[1].getScore() > players[0].getScore()) {
                return players[1];
            } else {
                return this.currentPlayer;
            }
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
        this.setPiece(null, move.getSource());
        this.setPiece(piece, move.getDestination());
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<Move>();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece == null) {
                    continue;
                }
                if (!piece.getPlayer().equals(player)) {
                    continue;
                }
                Place source = new Place(x, y);
                Move[] pieceMoves = piece.getAvailableMoves(this, source);
                for (Move m : pieceMoves) {
                    if (this.validateAllRules(m)) {
                        allMoves.add(m);
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }
}
