package version1.tictactoe.players;

import version1.tictactoe.Board;
import version1.tictactoe.BoardMarkers;
import util.Pair;

/**
 * A computer player that extends the default player with AI
 */
public class SmartComputerPlayer extends BasePlayer {
    /**
     * Gets the move of the AI
     *
     * @param board current board for any required checks
     * @return the pair of row,col that the AI wants to move to
     */
    public Pair<Integer, Integer> getMove(Board board) {
        // set up storage
        Pair<Integer, Integer> result = new Pair<>(0, 0);
        int best = Integer.MIN_VALUE;

        // for each position
        for (int i = 0; i < board.rowSize(); ++i) {
            for (int j = 0; j < board.rowSize(); ++j) {
                // find an empty
                if (board.isEmpty(i, j)) {
                    // fill it
                    board.setMarker(getMarker(), i, j);
                    // evaluate the position
                    var score = minimax(board, 1, false);
                    board.clearMarker(i, j);

                    // store if it's better than the previous best
                    if (score > best) {
                        best = score;
                        result.set_first(i);
                        result.set_second(j);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Performs a minimax analysis of a particular position and returns likelihood of winning from the position
     * @param board to analyze
     * @param depth the weight for analysis based on distance from root
     * @param maximizing false if their turn, true if mine
     * @return range from -10(losing) to 10(winning)
     */
    public int minimax(Board board, int depth, boolean maximizing) {
        // Determine which player is being evaluated
        BoardMarkers marker = getMarker();
        BoardMarkers otherMarker = marker.equals(BoardMarkers.X) ? BoardMarkers.O : BoardMarkers.X;
        BoardMarkers currentMarker = maximizing ? marker : otherMarker;

        if (board.isGameOver()) { // found a leaf node
            return score(board, depth); // give the score for the other player
        }

        // setup start values based on whose turn it is
        var best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // for each cell
        for (int i = 0; i < board.rowSize(); ++i) {
            for (int j = 0; j < board.rowSize(); ++j) {
                // if it's available
                if (board.isEmpty(i, j)) {

                    // go to the leaf and evaluate it
                    board.setMarker(currentMarker, i, j);
                    var score = minimax(board, depth + 1, !maximizing); // recurse to every node and evaluate
                    board.clearMarker(i, j);

                    // save it if this choice was better for who's turn it is
                    best = maximizing ? Integer.max(score, best) : Integer.min(score, best);
                }
            }
        }

        return best;
    }

    /**
     * Evaluates a position and assigns a score
     * @param board to analyze
     * @param depth to weight by
     * @return a score from -10 to 10 if game over, 0 if still playing
     */
    public int score(Board board, int depth) {
        if (board.hasXWon()) {
            if (getMarker().equals(BoardMarkers.X)) {
                return 10 - depth; // I have won
            } else {
                return depth - 10; // They have won
            }
        } else if (board.hasOWon()) {
            if (getMarker().equals(BoardMarkers.O)) {
                return 10 - depth; // I have won
            } else {
                return depth - 10; // They have won
            }
        }
        return 0; // Either a push or still playing
    }
}

