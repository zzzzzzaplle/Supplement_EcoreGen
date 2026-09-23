import java.util.ArrayList;

public class JesonMor extends Game {

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        this.board = new Piece[configuration.getSize()][];
        for (int x = 0; x < configuration.getSize(); x++) {
            this.board[x] = new Piece[configuration.getSize()];
            for (int y = 0; y < configuration.getSize(); y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
    }

    public Player start() {
        refreshOutput();
        while (true) {
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                // No moves available, end game by score
                Player winner = decideWinnerByScore();
                refreshOutput();
                System.out.println("Game Over! Winner: " + winner.getName());
                return winner;
            }
            Move chosenMove = currentPlayer.nextMove(this, availableMoves);
            if (chosenMove == null) {
                break;
            }
            movePiece(chosenMove);
            updateScore(currentPlayer, getPiece(chosenMove.getSource()), chosenMove);
            numMoves++;
            refreshOutput();

            if (numMoves > configuration.getNumMovesProtection()) {
                Player winner = getWinner(currentPlayer, getPiece(chosenMove.getSource()), chosenMove);
                if (winner != null) {
                    System.out.println("Game Over! Winner: " + winner.getName());
                    return winner;
                }
            }

            // Switch player
            Player[] players = configuration.getPlayers();
            if (currentPlayer.equals(players[0])) {
                currentPlayer = players[1];
            } else {
                currentPlayer = players[0];
            }
            refreshOutput();
        }
        return null;
    }

    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // Check if a Knight left the central square
        if (lastPiece != null && lastPiece instanceof Knight) {
            Place centralPlace = configuration.getCentralPlace();
            if (lastMove.getSource().equals(centralPlace) && !lastMove.getDestination().equals(centralPlace)) {
                return lastPlayer;
            }
        }

        // Check if only one player's pieces remain
        Player[] players = configuration.getPlayers();
        int player0Pieces = 0;
        int player1Pieces = 0;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null) {
                    if (piece.getPlayer().equals(players[0])) {
                        player0Pieces++;
                    } else {
                        player1Pieces++;
                    }
                }
            }
        }
        if (player0Pieces == 0 && player1Pieces > 0) {
            return players[1];
        }
        if (player1Pieces == 0 && player0Pieces > 0) {
            return players[0];
        }
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        player.setScore(player.getScore() + dx + dy);
    }

    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        setPiece(move.getSource(), null);
        setPiece(move.getDestination(), piece);
    }

    private void setPiece(Place place, Piece piece) {
        board[place.x()][place.y()] = piece;
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> allMoves = new ArrayList<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] moves = piece.getAvailableMoves(this, new Place(x, y));
                    for (Move m : moves) {
                        allMoves.add(m);
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }

    private Player decideWinnerByScore() {
        Player[] players = configuration.getPlayers();
        Player p1 = players[0];
        Player p2 = players[1];
        if (p1.getScore() > p2.getScore()) {
            return p1;
        } else if (p2.getScore() > p1.getScore()) {
            return p2;
        } else {
            // Equal scores, the other player wins
            return currentPlayer.equals(p1) ? p2 : p1;
        }
    }
}
