package tictactoe.clients;

import tictactoe.Board;
import tictactoe.Move;
import tictactoe.players.IPlayer;

public interface IClient {
    Move NextMove(Board board);

    void Win(Board board);
    void Lose(Board board);
    void Draw(Board board);
}
