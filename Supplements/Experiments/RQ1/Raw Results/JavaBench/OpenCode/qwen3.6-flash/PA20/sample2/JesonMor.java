import java.util.ArrayList;
import java.util.List;
import java.lang.CloneNotSupportedException;

public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        List<Rule> globalRules = new ArrayList<>();
        globalRules.add(new VacantRule());
        globalRules.add(new NilMoveRule());
        globalRules.add(new OutOfBoundaryRule());
        globalRules.add(new OccupiedRule());
        globalRules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));
        this.rules = globalRules;
        this.board = new Piece[configuration.getSize()][];
        for (int x = 0; x < configuration.getSize(); x++) {
            this.board[x] = new Piece[configuration.getSize()];
            for (int y = 0; y < configuration.getSize(); y++) {
                this.board[x][y] = configuration.getInitialBoard().get(x).get(y);
            }
        }
        this.currentPlayer = configuration.getPlayers().get(0);
    }

    @Override
    public void start() {
        refreshOutput();
        while (true) {
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (currentPlayer == null) {
                break;
            }
            if (availableMoves.length == 0) {
                Player p1 = configuration.getPlayers().get(0);
                Player p2 = configuration.getPlayers().get(1);
                if (p1.getScore() > p2.getScore()) {
                    System.out.println("Game over! Winner: " + p1.getName());
                } else if (p2.getScore() > p1.getScore()) {
                    System.out.println("Game over! Winner: " + p2.getName());
                } else {
                    if (currentPlayer.equals(p1)) {
                        System.out.println("Game over! No available moves. Winner: " + p1.getName());
                    } else {
                        System.out.println("Game over! No available moves. Winner: " + p2.getName());
                    }
                }
                break;
            }
            Move chosenMove = currentPlayer.nextMove(this, availableMoves);
            movePiece(chosenMove);
            numMoves++;
            updateScore(currentPlayer, getPiece(chosenMove.getSource()), chosenMove);
            Player[] currentPlayers = configuration.getPlayers().toArray(new Player[0]);
            int currentIndex = -1;
            for (int i = 0; i < currentPlayers.length; i++) {
                if (currentPlayers[i].equals(getPiece(chosenMove.getSource()).getPlayer())) {
                    currentIndex = i;
                    break;
                }
            }
            int nextIndex = (currentIndex + 1) % currentPlayers.length;
            this.currentPlayer = currentPlayers[nextIndex];
            refreshOutput();
            Player winner = getWinner(currentPlayer, getPiece(chosenMove.getSource()), chosenMove);
            if (winner != null) {
                System.out.println("Game over! Winner: " + winner.getName());
                break;
            }
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        if (lastPiece instanceof Knight) {
            if (lastMove.getSource().equals(configuration.getCentralPlace()) &&
                !lastMove.getDestination().equals(configuration.getCentralPlace())) {
                return lastPlayer;
            }
        }
        int player1PieceCount = 0;
        int player2PieceCount = 0;
        Player p1 = configuration.getPlayers().get(0);
        Player p2 = configuration.getPlayers().get(1);
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null) {
                    if (piece.getPlayer().equals(p1)) {
                        player1PieceCount++;
                    } else if (piece.getPlayer().equals(p2)) {
                        player2PieceCount++;
                    }
                }
            }
        }
        if (player1PieceCount == 0) {
            return p2;
        }
        if (player2PieceCount == 0) {
            return p1;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();
        int manhattanDist = Math.abs(destX - sourceX) + Math.abs(destY - sourceY);
        player.setScore(player.getScore() + manhattanDist);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        if (piece == null) {
            return;
        }
        this.board[move.getSource().x()][move.getSource().y()] = null;
        this.board[move.getDestination().x()][move.getDestination().y()] = piece;
        refreshOutput();
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        int size = configuration.getSize();
        ArrayList<Move> allMoves = new ArrayList<>();
        List<Rule> allRules = new ArrayList<>(rules);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    List<Rule> pieceRules = new ArrayList<>(allRules);
                    if (piece instanceof Knight) {
                        pieceRules.add(new KnightMoveRule());
                        pieceRules.add(new KnightBlockRule());
                    } else if (piece instanceof Archer) {
                        pieceRules.add(new ArcherMoveRule());
                    }
                    Move[] available = piece.getAvailableMoves(this, new Place(x, y));
                    ArrayList<Move> filteredMoves = new ArrayList<>();
                    for (Move m : available) {
                        if (pieceRules.stream().allMatch(r -> r.validate(this, m))) {
                            filteredMoves.add(m);
                        }
                    }
                    allMoves.addAll(filteredMoves);
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }
}
