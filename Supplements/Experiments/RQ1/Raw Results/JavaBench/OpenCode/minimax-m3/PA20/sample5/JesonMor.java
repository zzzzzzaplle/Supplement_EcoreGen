import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    private List<Rule> rules;

    public JesonMor() {
        super();
        this.rules = new ArrayList<Rule>();
        this.rules.add(new VacantRule());
        this.rules.add(new OccupiedRule());
        this.rules.add(new OutOfBoundaryRule());
        this.rules.add(new NilMoveRule());
        this.rules.add(new FirstNMovesProtectionRule());
        this.rules.add(new KnightMoveRule());
        this.rules.add(new KnightBlockRule());
        this.rules.add(new ArcherMoveRule());
    }

    public JesonMor(Configuration configuration) {
        this();
        this.configuration = configuration;
        int size = configuration.getSize();
        this.board = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.board[x] = new Piece[size];
            for (int y = 0; y < size; y++) {
                if (x < configuration.getInitialBoard().length && y < configuration.getInitialBoard()[x].length) {
                    this.board[x][y] = configuration.getInitialBoard()[x][y];
                } else {
                    this.board[x][y] = null;
                }
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
        if (configuration.getNumMovesProtection() > 0) {
            for (Rule rule : this.rules) {
                if (rule instanceof FirstNMovesProtectionRule) {
                    ((FirstNMovesProtectionRule) rule).setNumProtectedMoves(configuration.getNumMovesProtection());
                }
            }
        }
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public Player start() {
        while (true) {
            this.refreshOutput();
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                return this.decideWinnerByScore();
            }
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                return this.decideWinnerByScore();
            }
            if (!this.isValidMove(move)) {
                continue;
            }
            Piece movedPiece = this.getPiece(move.getSource());
            this.movePiece(move);
            this.updateScore(this.currentPlayer, movedPiece, move);
            this.numMoves++;
            Player winner = this.getWinner(this.currentPlayer, movedPiece, move);
            if (winner != null) {
                this.refreshOutput();
                return winner;
            }
            this.switchPlayer();
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

    private void switchPlayer() {
        Player[] players = this.configuration.getPlayers();
        if (this.currentPlayer.equals(players[0])) {
            this.currentPlayer = players[1];
        } else {
            this.currentPlayer = players[0];
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves <= this.configuration.getNumMovesProtection()) {
            return null;
        }
        if (lastMove != null && lastPiece instanceof Knight) {
            Place source = lastMove.getSource();
            Place destination = lastMove.getDestination();
            Place central = this.configuration.getCentralPlace();
            if (source != null && destination != null && central != null
                    && source.equals(central) && !destination.equals(central)) {
                return lastPlayer;
            }
        }
        Player[] players = this.configuration.getPlayers();
        int count0 = 0;
        int count1 = 0;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.board[x][y];
                if (p == null) continue;
                if (p.getPlayer().equals(players[0])) {
                    count0++;
                } else if (p.getPlayer().equals(players[1])) {
                    count1++;
                }
            }
        }
        if (count0 > 0 && count1 == 0) {
            return players[0];
        }
        if (count1 > 0 && count0 == 0) {
            return players[1];
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        if (player == null || move == null) {
            return;
        }
        Place source = move.getSource();
        Place destination = move.getDestination();
        if (source == null || destination == null) {
            return;
        }
        int distance = Math.abs(destination.x() - source.x()) + Math.abs(destination.y() - source.y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        if (move == null) return;
        Place source = move.getSource();
        Place destination = move.getDestination();
        if (source == null || destination == null) return;
        Piece piece = this.board[source.x()][source.y()];
        this.board[source.x()][source.y()] = null;
        this.board[destination.x()][destination.y()] = piece;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> result = new ArrayList<Move>();
        if (player == null) {
            return result.toArray(new Move[0]);
        }
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece == null) continue;
                if (!piece.getPlayer().equals(player)) continue;
                Place source = new Place(x, y);
                Move[] candidates = piece.getAvailableMoves(this, source);
                if (candidates == null) continue;
                for (Move move : candidates) {
                    if (this.isValidMove(move)) {
                        result.add(move);
                    }
                }
            }
        }
        return result.toArray(new Move[0]);
    }

    public boolean isValidMove(Move move) {
        if (move == null) return false;
        for (Rule rule : this.rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }
}
