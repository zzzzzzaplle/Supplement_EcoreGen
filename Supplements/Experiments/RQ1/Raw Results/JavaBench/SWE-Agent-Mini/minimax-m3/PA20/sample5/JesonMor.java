import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    private List<Rule> rules;

    public JesonMor() {
        super();
        this.rules = new ArrayList<Rule>();
    }

    public JesonMor(Configuration configuration) {
        super();
        this.configuration = configuration;
        int size = configuration.getSize();
        this.board = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.board[x] = new Piece[size];
            for (int y = 0; y < size; y++) {
                Piece p = configuration.getInitialBoard()[x][y];
                this.board[x][y] = p;
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
        this.rules = new ArrayList<Rule>();
        // initialize default rules
        this.rules.add(new VacantRule());
        this.rules.add(new NilMoveRule());
        this.rules.add(new OutOfBoundaryRule());
        this.rules.add(new OccupiedRule());
        this.rules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));
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

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    private boolean validateMove(Move move) {
        for (Rule rule : this.rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Player start() {
        this.refreshOutput();
        Player winner = null;
        while (winner == null) {
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                winner = this.getWinner(this.currentPlayer, null, null);
                break;
            }
            Move chosen = this.currentPlayer.nextMove(this, availableMoves);
            if (chosen == null) {
                winner = this.getWinner(this.currentPlayer, null, null);
                break;
            }
            Piece movedPiece = this.getPiece(chosen.getSource());
            // check protection for win condition
            if (this.numMoves < this.configuration.getNumMovesProtection()) {
                // still need to make a valid move; rules will block captures
            }
            if (this.validateMove(chosen)) {
                this.movePiece(chosen);
                this.updateScore(this.currentPlayer, movedPiece, chosen);
                this.numMoves++;
                Piece captured = null; // capture info is in lastMove/destination
                winner = this.getWinner(this.currentPlayer, movedPiece, chosen);
                this.refreshOutput();
            } else {
                System.out.println("Invalid move attempted. Try again.");
            }
            if (winner == null) {
                // switch player
                Player[] ps = this.configuration.getPlayers();
                if (this.currentPlayer.equals(ps[0])) {
                    this.currentPlayer = ps[1];
                } else {
                    this.currentPlayer = ps[0];
                }
            }
        }
        this.refreshOutput();
        System.out.println("Winner: " + (winner == null ? "No winner" : winner.getName()));
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        if (lastMove != null && lastPiece != null) {
            // 1. Knight leaves central square
            if (lastPiece instanceof Knight) {
                Place central = this.configuration.getCentralPlace();
                if (lastMove.getSource().equals(central) && !lastMove.getDestination().equals(central)) {
                    return lastPlayer;
                }
            }
        }
        // 2. Only one player's pieces remain
        Player[] players = this.configuration.getPlayers();
        int count0 = 0;
        int count1 = 0;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p == null) continue;
                if (p.getPlayer().equals(players[0])) count0++;
                else if (p.getPlayer().equals(players[1])) count1++;
            }
        }
        if (count0 == 0) return players[1];
        if (count1 == 0) return players[0];
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        int manhattan = dx + dy;
        player.setScore(player.getScore() + manhattan);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = this.getPiece(move.getSource());
        this.setPiece(move.getDestination(), piece);
        this.setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> result = new ArrayList<Move>();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p == null) continue;
                if (!p.getPlayer().equals(player)) continue;
                Place source = new Place(x, y);
                Move[] candidates = p.getAvailableMoves(this, source);
                for (Move m : candidates) {
                    if (this.validateMove(m)) {
                        result.add(m);
                    }
                }
            }
        }
        return result.toArray(new Move[0]);
    }

    @Override
    public JesonMor clone() throws CloneNotSupportedException {
        return (JesonMor) super.clone();
    }
}
