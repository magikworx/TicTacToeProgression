package tictactoe.players;

import tictactoe.Board;
import tictactoe.Game;
import util.Pair;

public abstract class BasePlayer implements IPlayer {
    private Game.Marker _marker;

    /**
     * Sets the players marker on the board
     *
     * @param marker for player
     */
    public void setMarker(Game.Marker marker) {
        _marker = marker;
    }

    /**
     * Gets the player's marker for the board
     *
     * @return player's marker
     */
    public Game.Marker getMarker() {
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
    public void lose(Board board) { }
    @Override
    public void win(Board board) { }
    @Override
    public void draw(Board board) { }
}
