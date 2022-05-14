package starter;

import base.Game;
import util.Terminal;

public class Runner {
    public static class Dumb implements Game.Player, Runnable {
        public int _player;
        public Game _game;
        public Dumb(Game game) {
            _game = game;
            _player = _game.addPlayer(this);
        }

        @Override
        public void run() {
            while (true){
                var board = _game.getStatus();
                if (Game.Rules.getCurrentPlayer(board) == _player) {
                    int i = 0;
                    for(int row = 0; row < 3; ++row){
                        for(int col = 0; col < 3; ++col){
                            if (board[i++] == 0) _game.makeMove(this, row, col);
                        }
                    }
                }
                if (Game.Rules.isGameOver(board)) break;
            }
        }
    }
    public static class Local implements Game.Player, Runnable {
        public int _player;
        public Game _game;
        public Local(Game game) {
            _game = game;
            _player = _game.addPlayer(this);
        }

        public void printBoard(int[] board) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for(int row = 0; row < 3; ++row){
                sb.append("|");
                for(int col = 0; col < 3; ++col){
                    sb.append(board[i++]);
                    sb.append("|");
                }
                sb.append("\n");
            }
            Terminal.println(sb.toString());
        }
        @Override
        public void run() {
            int last = -1;
            while (true){
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
                        case 1: Terminal.println("Invalid row"); break;
                        case 2: Terminal.println("Invalid column"); break;
                        case 3: Terminal.println("Already Occupied"); break;
                    }
                }
                if (Game.Rules.isGameOver(board)) break;
            }
        }
    }

    public static void main(String [] args) {
        Game game = new Game();
        Local p1 = new Local(game);
        new Thread(p1).start();
        Dumb p2 = new Dumb(game);
        new Thread(p2).start();
    }
}
