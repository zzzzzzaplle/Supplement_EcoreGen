import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    public JesonMor() {
        super();
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        
        int size = configuration.getSize();
        this.board = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.board[x] = new Piece[size];
            for (int y = 0; y < size; y++) {
                this.board[x][y] = null;
            }
        }
        
        // Place initial pieces
        configuration.getPlayers()[0].setScore(0);
        configuration.getPlayers()[1].setScore(0);
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = configuration.getInitialBoard()[x][y];
                if (piece != null) {
                    this.board[x][y] = piece;
                }
            }
        }
        
        // Set initial starting player
        this.currentPlayer = configuration.getPlayers()[0];
    }

    @Override
    public Player start() {
        System.out.println("The game of JesonMor has started!");
        
        while (true) {
            refreshOutput();
            
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                // No available moves for current player
                break;
            }
            
            Move chosenMove = currentPlayer.nextMove(this, availableMoves);
            if (chosenMove == null) {
                break;
            }
            
            // Check if valid move and is a move from the player's available moves
            boolean found = false;
            for (Move m : availableMoves) {
                if (m.equals(chosenMove)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Invalid move. Please try again.");
                continue;
            }
            
            movePiece(chosenMove);
            updateScore(currentPlayer, getPiece(chosenMove.getDestination()), chosenMove);
            
            // Check for win condition
            Player winner = getWinner(currentPlayer, getPiece(chosenMove.getDestination()), chosenMove);
            if (winner != null) {
                refreshOutput();
                System.out.println("Player " + winner.getName() + " (" + winner.getColor() + ") wins!");
                return winner;
            }
            
            // Switch to next player
            currentPlayer = (currentPlayer.equals(configuration.getPlayers()[0])) ?
                    configuration.getPlayers()[1] : configuration.getPlayers()[0];
        }
        
        // Game ended due to no available moves - decide by score
        refreshOutput();
        Player player1 = configuration.getPlayers()[0];
        Player player2 = configuration.getPlayers()[1];
        
        if (player1.getScore() > player2.getScore()) {
            System.out.println("Player " + player1.getName() + " (" + player1.getColor() + ") wins!");
            return player1;
        } else if (player2.getScore() > player1.getScore()) {
            System.out.println("Player " + player2.getName() + " (" + player2.getColor() + ") wins!");
            return player2;
        } else {
            System.out.println("Players are tied! Current player " + currentPlayer.getName() + " wins by default.");
            return currentPlayer;
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // Cannot declare winner during protection phase
        if (this.numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        
        // Check if a Knight left the central square
        if (lastPiece != null) {
            if (lastPiece instanceof Knight && 
                lastMove.getSource().equals(this.getCentralPlace()) && 
                !lastMove.getDestination().equals(this.getCentralPlace())) {
                return lastPlayer;
            }
        }
        
        // Check if only one player's pieces remain on the board
        int player1Count = 0;
        int player2Count = 0;
        int size = configuration.getSize();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null) {
                    if (piece.getPlayer().equals(configuration.getPlayers()[0])) {
                        player1Count++;
                    } else if (piece.getPlayer().equals(configuration.getPlayers()[1])) {
                        player2Count++;
                    }
                }
            }
        }
        
        if (player1Count == 0 && player2Count > 0) {
            return configuration.getPlayers()[1];
        } else if (player2Count == 0 && player1Count > 0) {
            return configuration.getPlayers()[0];
        }
        
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();
        
        int distance = Math.abs(destX - sourceX) + Math.abs(destY - sourceY);
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = this.board[move.getSource().x()][move.getSource().y()];
        this.board[move.getSource().x()][move.getSource().y()] = null;
        this.board[move.getDestination().x()][move.getDestination().y()] = piece;
        this.numMoves++;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allAvailableMoves = new ArrayList<>();
        int size = configuration.getSize();
        
        // Collect all pieces belonging to the player
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    for (Move move : pieceMoves) {
                        allAvailableMoves.add(move);
                    }
                }
            }
        }
        
        return allAvailableMoves.toArray(new Move[0]);
    }
}
