package tictactoe.players;

import tictactoe.Board;
import tictactoe.BoardMarkers;
import tictactoe.GameStates;
import util.Pair;

public interface IPlayer {
    void setMarker(BoardMarkers marker);
    BoardMarkers getMarker();

    Pair<Integer, Integer> getMove(Board board);
    void updateStatus(GameStates state, Board board);
}