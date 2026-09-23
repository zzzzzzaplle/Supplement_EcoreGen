import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    private List<Rule> rules;

    public JesonMor() {
        super();
        this.rules = new ArrayList<>();
        initializeRules();
    }

    public JesonMor(Configuration configuration) {
        super();
        this.configuration = configuration;
        int size = configuration.getSize();
        this.board = new Piece[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
        this.rules = new ArrayList<>();
        initializeRules();
    }

    private void initializeRules() {
        this.rules.add(new VacantRule());
        this.rules.add(new OccupiedRule());
        this.rules.add(new OutOfBoundaryRule());
        this.rules.add(new NilMoveRule());
        this.rules.add(new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()));
        this.rules.add(new KnightMoveRule());
        this.rules.add(new KnightBlockRule());
        this.rules.add(new ArcherMoveRule());
    }

    @Override
    public Player start() {
        this.currentPlayer = this.configuration.getPlayers()[0];
        this.refreshOutput();
        while (true) {
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                return decideWinnerByScore();
            }
            Move chosenMove = this.currentPlayer.nextMove(this, availableMoves);
            if (chosenMove == null) {
                continue;
            }
            Piece piece = this.getPiece(chosenMove.getSource());
            this.movePiece(chosenMove);
            this.updateScore(this.currentPlayer, piece, chosenMove);
            this.numMoves++;
            this.refreshOutput();
            Player winner = this.getWinner(this.currentPlayer, piece, chosenMove);
            if (winner != null) {
                return winner;
            }
            // Switch to next player
            Player[] players = this.configuration.getPlayers();
            this.currentPlayer = (this.currentPlayer.equals(players[0])) ? players[1] : players[0];
        }
    }

    private Player decideWinnerByScore() {
        Player[] players = this.configuration.getPlayers();
        if (players[0].getScore() > players[1].getScore()) {
            return players[0];
        } else if (players[1].getScore() > players[0].getScore()) {
            return players[1];
        } else {
            return this.currentPlayer;
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves <= this.configuration.getNumMovesProtection()) {
            return null;
        }
        // Check if a Knight leaves the central square
        if (lastPiece instanceof Knight
                && this.configuration.getCentralPlace().equals(lastMove.getSource())
                && !this.configuration.getCentralPlace().equals(lastMove.getDestination())) {
            return lastPlayer;
        }
        // Check if only one player's pieces remain
        Player[] players = this.configuration.getPlayers();
        int count0 = 0;
        int count1 = 0;
        for (int x = 0; x < this.configuration.getSize(); x++) {
            for (int y = 0; y < this.configuration.getSize(); y++) {
                Piece p = this.getPiece(x, y);
                if (p != null) {
                    if (p.getPlayer().equals(players[0])) {
                        count0++;
                    } else if (p.getPlayer().equals(players[1])) {
                        count1++;
                    }
                }
            }
        }
        if (count0 == 0) {
            return players[1];
        }
        if (count1 == 0) {
            return players[0];
        }
        return null;
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
        List<Move> allMoves = new ArrayList<>();
        for (int x = 0; x < this.configuration.getSize(); x++) {
            for (int y = 0; y < this.configuration.getSize(); y++) {
                Place p = new Place(x, y);
                Piece piece = this.getPiece(p);
                if (piece == null || !piece.getPlayer().equals(player)) {
                    continue;
                }
                Move[] candidates = piece.getAvailableMoves(this, p);
                for (Move m : candidates) {
                    if (validateMove(m, piece)) {
                        allMoves.add(m);
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }

    private boolean validateMove(Move move, Piece piece) {
        for (Rule rule : this.rules) {
            // Apply piece-specific rules only for their respective piece types
            if (rule instanceof KnightMoveRule || rule instanceof KnightBlockRule) {
                if (!(piece instanceof Knight)) {
                    continue;
                }
            }
            if (rule instanceof ArcherMoveRule) {
                if (!(piece instanceof Archer)) {
                    continue;
                }
            }
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }
}
