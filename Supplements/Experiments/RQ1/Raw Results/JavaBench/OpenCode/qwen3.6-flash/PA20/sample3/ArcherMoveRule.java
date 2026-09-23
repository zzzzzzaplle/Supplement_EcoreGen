@Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        Place source = move.getSource();
        Place dest = move.getDestination();

        // Archer must move orthogonally
        if (source.x() != dest.x() && source.y() != dest.y()) {
            return false;
        }

        Piece destPiece = game.getPiece(dest);
        boolean isCapturing = destPiece != null;

        // Count pieces between source and destination (exclusive)
        int pieceCount = 0;
        if (source.x() == dest.x()) {
            int minY = Math.min(source.y(), dest.y());
            int maxY = Math.max(source.y(), dest.y());
            for (int y = minY + 1; y < maxY; y++) {
                if (game.getPiece(source.x(), y) != null) {
                    pieceCount++;
                }
            }
        } else if (source.y() == dest.y()) {
            int minX = Math.min(source.x(), dest.x());
            int maxX = Math.max(source.x(), dest.x());
            for (int x = minX + 1; x < maxX; x++) {
                if (game.getPiece(x, source.y()) != null) {
                    pieceCount++;
                }
            }
        }

        if (isCapturing) {
            // Must have exactly one piece between source and destination (the screen)
            if (pieceCount != 1) {
                return false;
            }
        } else {
            // Non-capturing: path must be completely clear
            if (pieceCount != 0) {
                return false;
            }
        }

        return true;
    }
