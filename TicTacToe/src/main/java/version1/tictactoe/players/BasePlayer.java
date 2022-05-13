package version1.tictactoe.players;

import version1.tictactoe.Board;
import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import util.Pair;

public abstract class BasePlayer implements IPlayer {
    private BoardMarkers _marker;

    /**
     * Sets the players marker on the board
     *
     * @param marker for player
     */
    public void setMarker(BoardMarkers marker) {
        _marker = marker;
    }

    /**
     * Gets the player's marker for the board
     *
     * @return player's marker
     */
    public BoardMarkers getMarker() {
        return _marker;
    }

    /**
     * Asks the player for their move
     *
     * @param board current board for any required checks
     * @return a pair of row,col values for the player's move
     */
    public abstract Pair<Integer, Integer> getMove(Board board);

    @Override
    public void updateStatus(GameStates state, Board board){}
}
