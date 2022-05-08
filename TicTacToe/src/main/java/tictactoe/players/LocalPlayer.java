package tictactoe.players;

import tictactoe.Board;
import tictactoe.Game;
import tictactoe.Move;
import tictactoe.clients.IClient;
import util.Pair;

/**
 * Describes a local player(Human by default)
 */
public class LocalPlayer extends BasePlayer {
    public IClient _client;
    public LocalPlayer(IClient client){
        _client = client;
    }

    @Override
    public Pair<Integer, Integer> getMove(Board board) {
        do {
            var move = _client.NextMove(board);
            if (move.getType() == Move.Types.Moved) {
                return new Pair<>(move.getRow(), move.getColumn());
            }
        } while(true);
    }

    @Override
    public void lose(Board board) {
        _client.Lose(board);
    }

    @Override
    public void win(Board board) {
        _client.Win(board);
    }

    @Override
    public void draw(Board board) {
        _client.Draw(board);
    }
}
