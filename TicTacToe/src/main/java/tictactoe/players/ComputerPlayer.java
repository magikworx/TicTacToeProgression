package tictactoe.players;

import tictactoe.Board;
import tictactoe.Game;
import util.Pair;

/**
 * A computer player that extends the default player with AI
 */
public class ComputerPlayer extends BasePlayer {
    /**
     * Gets the move of the AI
     *
     * @param board current board for any required checks
     * @return the pair of row,col that the AI wants to move to
     */
    public Pair<Integer, Integer> getMove(Board board) {
        for (int r = 0; r < board.rowSize(); ++r) { // for each row
            for (int c = 0; c < board.rowSize(); c++) { // for each column
                // pull the cell
                Game.Marker cell = board.getMarker(r, c);
                // if it's available
                if (cell == null || cell.equals(Game.Marker.Empty)) {
                    // return the cell
                    return new Pair<>(r, c);
                }
            }
        }
        // no cells available(should not be possible)
        return new Pair<>(0, 0);
    }
}
