import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    private Rule[] rules;

    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        int size = this.configuration.getSize();
        this.board = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.board[x] = this.configuration.getInitialBoard()[x].clone();
        }
        this.numMoves = 0;
        this.currentPlayer = this.configuration.getPlayers()[0];
        FirstNMovesProtectionRule protectionRule = new FirstNMovesProtectionRule();
        protectionRule.setNumProtectedMoves(configuration.getNumMovesProtection());
        this.rules = new Rule[]{
                new VacantRule(),
                new OccupiedRule(),
                new OutOfBoundaryRule(),
                new NilMoveRule(),
                protectionRule,
                new KnightMoveRule(),
                new KnightBlockRule(),
                new ArcherMoveRule()
        };
    }

    public Rule[] getRules() {
        return this.rules;
    }

    public void setRules(Rule[] rules) {
        this.rules = rules;
    }

    @Override
    public Player start() {
        this.refreshOutput();
        while (true) {
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                Player p0 = this.configuration.getPlayers()[0];
                Player p1 = this.configuration.getPlayers()[1];
                if (p0.getScore() > p1.getScore()) {
                    return p0;
                }
                if (p1.getScore() > p0.getScore()) {
                    return p1;
                }
                return this.currentPlayer;
            }
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                Player p0 = this.configuration.getPlayers()[0];
                Player p1 = this.configuration.getPlayers()[1];
                if (p0.getScore() > p1.getScore()) {
                    return p0;
                }
                if (p1.getScore() > p0.getScore()) {
                    return p1;
                }
                return this.currentPlayer;
            }
            Piece piece = this.getPiece(move.getSource());
            boolean valid = true;
            for (Rule rule : this.rules) {
                if (!rule.validate(this, move)) {
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                continue;
            }
            this.movePiece(move);
            this.updateScore(this.currentPlayer, piece, move);
            this.numMoves++;
            this.refreshOutput();
            Player winner = this.getWinner(this.currentPlayer, piece, move);
            if (winner != null) {
                return winner;
            }
            Player[] players = this.configuration.getPlayers();
            this.currentPlayer = (this.currentPlayer == players[0]) ? players[1] : players[0];
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves <= this.configuration.getNumMovesProtection()) {
            return null;
        }
        if (lastPiece != null && lastPiece instanceof Knight && lastMove != null) {
            Place central = this.configuration.getCentralPlace();
            if (lastMove.getSource().equals(central) && !lastMove.getDestination().equals(central)) {
                return lastPlayer;
            }
        }
        Player[] players = this.configuration.getPlayers();
        int p0Count = this.countPieces(players[0]);
        int p1Count = this.countPieces(players[1]);
        if (p0Count > 0 && p1Count == 0) {
            return players[0];
        }
        if (p1Count > 0 && p0Count == 0) {
            return players[1];
        }
        return null;
    }

    private int countPieces(Player player) {
        int count = 0;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p != null && p.getPlayer().equals(player)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().x() - move.getDestination().x())
                + Math.abs(move.getSource().y() - move.getDestination().y());
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
                Piece p = this.getPiece(x, y);
                if (p != null && p.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = p.getAvailableMoves(this, source);
                    if (pieceMoves == null) {
                        continue;
                    }
                    for (Move m : pieceMoves) {
                        boolean valid = true;
                        for (Rule rule : this.rules) {
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
