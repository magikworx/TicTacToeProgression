package tictactoe.clients;

import tictactoe.Board;
import tictactoe.Move;

public class GuiClient implements IClient{
    @Override
    public Move NextMove(Board board) {
        return Move.stillThinking();
    }

    @Override
    public void Win(Board board) {

    }

    @Override
    public void Lose(Board board) {

    }

    @Override
    public void Draw(Board board) {

    }

}
