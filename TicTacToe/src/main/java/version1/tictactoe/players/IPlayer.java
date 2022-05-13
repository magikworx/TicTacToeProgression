package version1.tictactoe.players;

import version1.tictactoe.Board;
import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import util.Pair;

public interface IPlayer {
    void setMarker(BoardMarkers marker);
    BoardMarkers getMarker();

    Pair<Integer, Integer> getMove(Board board);
    void updateStatus(GameStates state, Board board);
}