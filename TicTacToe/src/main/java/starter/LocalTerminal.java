package starter;

import base.Game;
import util.Terminal;

public class LocalTerminal implements Game.Player, Runnable {
    public int _player;
    public Game _game;

    public LocalTerminal(Game game) {
        _game = game;
        _player = _game.addPlayer(this);
    }

    public void printBoard(int[] board) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (int row = 0; row < 3; ++row) {
            sb.append("|");
            for (int col = 0; col < 3; ++col) {
                var player = board[i++];
                if (player == 1) sb.append("X");
                else if (player == 2) sb.append("O");
                else sb.append(" ");
                sb.append("|");
            }
            sb.append("\n");
        }
        Terminal.println(sb.toString());
    }

    @Override
    public void run() {
        int last = -1;
        while (true) {
            var board = _game.getStatus();
            int boardHash = Game.Rules.hashBoard(board);
            if (boardHash != last) {
                printBoard(board);
                last = boardHash;
            }
            if (Game.Rules.getCurrentPlayer(board) == _player) {
                int row = Terminal.getIntFromChoice("Enter row[0,1,2]: ", 0, 1, 2);
                int col = Terminal.getIntFromChoice("Enter column[0,1,2]: ", 0, 1, 2);
                int validation = _game.makeMove(this, row, col);
                switch (validation) {
                    case 1:
                        Terminal.println("Invalid row");
                        break;
                    case 2:
                        Terminal.println("Invalid column");
                        break;
                    case 3:
                        Terminal.println("Already Occupied");
                        break;
                }
            }
            if (Game.Rules.isGameOver(board)) {
                var winner = Game.Rules.getWinner(board);
                if (winner == 0) {
                    Terminal.println("Draw");
                } else if (winner == _player) {
                    Terminal.println("You WIN!!!");
                } else {
                    Terminal.println("You lose");
                }
                break;
            }
        }
    }

    public static void Launch(Game game) {
        LocalTerminal p1 = new LocalTerminal(game);
        new Thread(p1).start();
    }
}
