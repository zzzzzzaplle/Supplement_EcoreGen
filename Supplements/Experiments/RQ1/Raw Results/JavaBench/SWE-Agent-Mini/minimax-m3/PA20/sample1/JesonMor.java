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
        this.refreshOutput();
        Player lastPlayer = null;
        Piece lastPiece = null;
        Move lastMove = null;
        while (true) {
            Move[] available = this.getAvailableMoves(this.currentPlayer);
            if (available == null || available.length == 0) {
                return this.getWinner(lastPlayer, lastPiece, lastMove);
            }
            Move move = this.currentPlayer.nextMove(this, available);
            if (move == null) {
                return this.getWinner(lastPlayer, lastPiece, lastMove);
            }
            // Validate move via rules
            List<Rule> rules = new ArrayList<>();
            rules.add(new VacantRule());
            rules.add(new OccupiedRule());
            rules.add(new OutOfBoundaryRule());
            rules.add(new NilMoveRule());
            rules.add(new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()));
            Piece sourcePiece = this.getPiece(move.getSource());
            if (sourcePiece instanceof Knight) {
                rules.add(new KnightMoveRule());
                rules.add(new KnightBlockRule());
            } else if (sourcePiece instanceof Archer) {
                rules.add(new ArcherMoveRule());
            }
            boolean valid = true;
            for (Rule rule : rules) {
                if (!rule.validate(this, move)) {
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                System.out.println("Invalid move: " + ruleDescription(rules, move));
                continue;
            }
            this.movePiece(move);
            this.updateScore(this.currentPlayer, sourcePiece, move);
            this.numMoves++;
            lastPlayer = this.currentPlayer;
            lastPiece = sourcePiece;
            lastMove = move;
            Player winner = this.getWinner(lastPlayer, lastPiece, lastMove);
            if (winner != null) {
                this.refreshOutput();
                System.out.println("Winner: " + winner.getName());
                return winner;
            }
            // switch to the other player
            Player[] players = this.configuration.getPlayers();
            this.currentPlayer = (this.currentPlayer == players[0]) ? players[1] : players[0];
            this.refreshOutput();
        }
    }

    private String ruleDescription(List<Rule> rules, Move move) {
        for (Rule r : rules) {
            if (!r.validate(this, move)) {
                return r.getDescription();
            }
        }
        return "";
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (lastPlayer == null) {
            return null;
        }
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        // Knight leaves central square
        if (lastMove != null && lastPiece instanceof Knight
                && this.configuration.getCentralPlace().equals(lastMove.getSource())
                && !this.configuration.getCentralPlace().equals(lastMove.getDestination())) {
            return lastPlayer;
        }
        // Count pieces per player
        Player[] players = this.configuration.getPlayers();
        boolean p0Has = false;
        boolean p1Has = false;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p == null) {
                    continue;
                }
                if (p.getPlayer().equals(players[0])) {
                    p0Has = true;
                } else if (p.getPlayer().equals(players[1])) {
                    p1Has = true;
                }
            }
        }
        if (p0Has && !p1Has) {
            return players[0];
        }
        if (p1Has && !p0Has) {
            return players[1];
        }
        if (!p0Has && !p1Has) {
            return lastPlayer;
        }
        // No more moves: current player has no available moves
        Move[] avail = this.getAvailableMoves(this.currentPlayer);
        if (avail == null || avail.length == 0) {
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
        if (player == null || move == null) {
            return;
        }
        int dist = Math.abs(move.getSource().x() - move.getDestination().x())
                + Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + dist);
    }

    @Override
    public void movePiece(Move move) {
        if (move == null) {
            return;
        }
        Piece piece = this.getPiece(move.getSource());
        this.setPiece(move.getDestination(), piece);
        this.setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        if (player == null) {
            return new Move[0];
        }
        List<Move> all = new ArrayList<>();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p == null) {
                    continue;
                }
                if (!p.getPlayer().equals(player)) {
                    continue;
                }
                Place src = new Place(x, y);
                Move[] candidates = p.getAvailableMoves(this, src);
                if (candidates == null) {
                    continue;
                }
                for (Move m : candidates) {
                    if (!m.getSource().equals(m.getDestination())) {
                        all.add(m);
                    }
                }
            }
        }
        return all.toArray(new Move[0]);
    }
}
