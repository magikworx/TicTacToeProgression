package tictactoe.players;

import tictactoe.Board;
import tictactoe.Game;
import util.Pair;

public interface IPlayer {
    void setMarker(Game.Marker marker);
    Game.Marker getMarker();

    Pair<Integer, Integer> getMove(Board board);
    void lose(Board board);
    void win(Board board);
    void draw(Board board);
}