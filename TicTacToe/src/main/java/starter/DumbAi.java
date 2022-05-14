package starter;

import base.Game;

public class DumbAi implements Game.Player, Runnable {
    public int _player;
    public Game _game;

    public DumbAi(Game game) {
        _game = game;
        _player = _game.addPlayer(this);
    }

    @Override
    public void run() {
        while (true) {
            var board = _game.getStatus();
            if (Game.Rules.getCurrentPlayer(board) == _player) {
                int i = 0;
                for (int row = 0; row < 3; ++row) {
                    for (int col = 0; col < 3; ++col) {
                        if (board[i++] == 0) _game.makeMove(this, row, col);
                    }
                }
            }
            if (Game.Rules.isGameOver(board)) break;
        }
    }

    public static void Launch(Game game) {
        DumbAi p1 = new DumbAi(game);
        new Thread(p1).start();
    }
}