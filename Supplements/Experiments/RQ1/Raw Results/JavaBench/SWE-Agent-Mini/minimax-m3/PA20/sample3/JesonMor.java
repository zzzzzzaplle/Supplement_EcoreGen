import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    private List<Rule> rules;

    public JesonMor() {
        super();
        this.rules = new ArrayList<>();
        this.initializeRules();
    }

    public JesonMor(Configuration configuration) {
        super();
        this.configuration = configuration;
        int size = configuration.getSize();
        this.board = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.board[x] = new Piece[size];
            for (int y = 0; y < size; y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
        this.rules = new ArrayList<>();
        this.initializeRules();
    }

    private void initializeRules() {
        this.rules.add(new VacantRule());
        this.rules.add(new NilMoveRule());
        this.rules.add(new OutOfBoundaryRule());
        this.rules.add(new OccupiedRule());
        this.rules.add(new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()));
        this.rules.add(new KnightMoveRule());
        this.rules.add(new KnightBlockRule());
        this.rules.add(new ArcherMoveRule());
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public Player start() {
        this.refreshOutput();
        while (true) {
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves.length == 0) {
                // No available moves, decide winner by score
                Player p0 = this.configuration.getPlayers()[0];
                Player p1 = this.configuration.getPlayers()[1];
                if (p0.getScore() > p1.getScore()) {
                    return p0;
                } else if (p1.getScore() > p0.getScore()) {
                    return p1;
                } else {
                    return this.currentPlayer;
                }
            }
            Move chosen = this.currentPlayer.nextMove(this, availableMoves);
            if (chosen == null) {
                continue;
            }
            // Validate the move with all rules
            boolean valid = true;
            for (Rule rule : this.rules) {
                if (!rule.validate(this, chosen)) {
                    System.out.println("Invalid move: " + rule.getDescription());
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                continue;
            }
            Piece piece = this.getPiece(chosen.getSource());
            Piece captured = this.getPiece(chosen.getDestination());
            this.movePiece(chosen);
            this.updateScore(this.currentPlayer, piece, chosen);
            this.numMoves++;
            this.refreshOutput();
            // Check winner
            Player winner = this.getWinner(this.currentPlayer, piece, chosen);
            if (winner != null) {
                return winner;
            }
            // Switch player
            Player[] players = this.configuration.getPlayers();
            this.currentPlayer = (this.currentPlayer == players[0]) ? players[1] : players[0];
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves <= this.configuration.getNumMovesProtection()) {
            return null;
        }
        Place central = this.getCentralPlace();
        // Knight leaves central square
        if (lastMove != null && lastPiece instanceof Knight
                && lastMove.getSource().equals(central)
                && !lastMove.getDestination().equals(central)) {
            return lastPlayer;
        }
        // Count pieces of each player
        int p0Count = 0;
        int p1Count = 0;
        Player p0 = this.configuration.getPlayers()[0];
        Player p1 = this.configuration.getPlayers()[1];
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p == null) continue;
                if (p.getPlayer().equals(p0)) {
                    p0Count++;
                } else if (p.getPlayer().equals(p1)) {
                    p1Count++;
                }
            }
        }
        if (p0Count == 0 && p1Count > 0) {
            return p1;
        }
        if (p1Count == 0 && p0Count > 0) {
            return p0;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        if (player == null || move == null) return;
        Place s = move.getSource();
        Place d = move.getDestination();
        int dist = Math.abs(s.x() - d.x()) + Math.abs(s.y() - d.y());
        player.setScore(player.getScore() + dist);
    }

    @Override
    public void movePiece(Move move) {
        if (move == null) return;
        Place s = move.getSource();
        Place d = move.getDestination();
        Piece piece = this.getPiece(s);
        this.setPiece(s, null);
        this.setPiece(d, piece);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        if (player == null) return new Move[0];
        int size = this.configuration.getSize();
        List<Move> all = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece == null) continue;
                if (!piece.getPlayer().equals(player)) continue;
                Place src = new Place(x, y);
                Move[] candidates = piece.getAvailableMoves(this, src);
                for (Move m : candidates) {
                    if (this.isValidMove(player, piece, m)) {
                        all.add(m);
                    }
                }
            }
        }
        return all.toArray(new Move[0]);
    }

    private boolean isValidMove(Player player, Piece piece, Move move) {
        for (Rule rule : this.rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }
}
