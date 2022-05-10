package tictactoe.players;

import tictactoe.Board;
import tictactoe.BoardMarkers;
import util.Pair;

public interface IPlayer {
    void setMarker(BoardMarkers marker);
    BoardMarkers getMarker();

    Pair<Integer, Integer> getMove(Board board);
    void lose(Board board);
    void win(Board board);
    void draw(Board board);
}