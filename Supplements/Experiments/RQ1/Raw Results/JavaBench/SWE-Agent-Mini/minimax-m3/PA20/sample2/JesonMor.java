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
        refreshOutput();
        Player winner = null;
        while (winner == null) {
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves == null || availableMoves.length == 0) {
                winner = getWinner(currentPlayer, null, null);
                break;
            }
            Move chosen = currentPlayer.nextMove(this, availableMoves);
            if (chosen == null) {
                winner = getWinner(currentPlayer, null, null);
                break;
            }
            Piece movingPiece = getPiece(chosen.getSource());
            movePiece(chosen);
            updateScore(currentPlayer, movingPiece, chosen);
            numMoves++;
            refreshOutput();
            winner = getWinner(currentPlayer, movingPiece, chosen);
            if (winner == null) {
                // switch player
                Player[] players = configuration.getPlayers();
                if (currentPlayer == players[0]) {
                    currentPlayer = players[1];
                } else {
                    currentPlayer = players[0];
                }
            }
        }
        System.out.println("Winner: " + winner.getColor() + winner.getName() + Color.DEFAULT);
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // Protection: no winner inside first numMovesProtection moves
        if (numMoves < configuration.getNumMovesProtection()) {
            return null;
        }
        // Win condition 1: A Knight leaves the central square
        if (lastPiece != null && lastMove != null
                && lastPiece instanceof Knight
                && lastMove.getSource().equals(configuration.getCentralPlace())
                && !lastMove.getDestination().equals(configuration.getCentralPlace())) {
            return lastPlayer;
        }
        // Win condition 2: Only one player's pieces remain
        Player[] players = configuration.getPlayers();
        boolean p0Alive = false;
        boolean p1Alive = false;
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = getPiece(x, y);
                if (p != null) {
                    if (p.getPlayer().equals(players[0])) {
                        p0Alive = true;
                    } else if (p.getPlayer().equals(players[1])) {
                        p1Alive = true;
                    }
                }
            }
        }
        if (p0Alive && !p1Alive) {
            return players[0];
        }
        if (p1Alive && !p0Alive) {
            return players[1];
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dist = Math.abs(move.getSource().x() - move.getDestination().x())
                + Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + dist);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        if (piece == null) {
            return;
        }
        setPiece(move.getDestination(), piece);
        setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<Move>();
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Place src = new Place(x, y);
                Piece p = getPiece(src);
                if (p == null || !p.getPlayer().equals(player)) {
                    continue;
                }
                Move[] candidates = p.getAvailableMoves(this, src);
                for (Move m : candidates) {
                    if (isValidMove(m)) {
                        allMoves.add(m);
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }

    private boolean isValidMove(Move move) {
        Rule[] globalRules = new Rule[]{
                new VacantRule(),
                new NilMoveRule(),
                new OutOfBoundaryRule(),
                new OccupiedRule(),
                new FirstNMovesProtectionRule(configuration.getNumMovesProtection())
        };
        for (Rule r : globalRules) {
            if (!r.validate(this, move)) {
                return false;
            }
        }
        Piece piece = getPiece(move.getSource());
        if (piece instanceof Knight) {
            Rule km = new KnightMoveRule();
            Rule kb = new KnightBlockRule();
            if (!km.validate(this, move)) return false;
            if (!kb.validate(this, move)) return false;
        } else if (piece instanceof Archer) {
            Rule am = new ArcherMoveRule();
            if (!am.validate(this, move)) return false;
        }
        return true;
    }
}
