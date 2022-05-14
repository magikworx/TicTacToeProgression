package starter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                    var player = board[i++];
                    if (player == 1) sb.append("X");
                    else  if (player == 2) sb.append("O");
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
                if (Game.Rules.isGameOver(board)) {
                    var winner = Game.Rules.getWinner(board);
                    if(winner == 0) {
                        Terminal.println("Draw");
                    } else if(winner == _player){
                        Terminal.println("You WIN!!!");
                    } else{
                        Terminal.println("You lose");
                    }
                    break;
                }
            }
        }
    }

    public static class GuiClient extends JPanel implements Game.Player {
        public int _player;
        public Game _game;
        public int _last = -1;

        JButton[][] buttons = new JButton[3][3];

        public GuiClient(Game game) {
            setLayout(new GridLayout(3,3));
            initializebuttons();
            _game = game;
            _player = _game.addPlayer(this);

            new Thread(() -> {
                while(true) {
                    var board = _game.getStatus();
                    int boardHash = Game.Rules.hashBoard(board);
                    if (boardHash != _last) {
                        for (int i = 0; i < 3; ++i) {
                            for (int j = 0; j < 3; ++j) {
                                var marker = board[i * 3 + j];
                                if (marker == 1) {
                                    buttons[i][j].setText("X");
                                } else if (marker == 2) {
                                    buttons[i][j].setText("O");
                                } else {
                                    buttons[i][j].setText("");
                                }
                            }
                        }
                        _last = boardHash;
                    }
                    if (Game.Rules.isGameOver(board)) {
                        var winner = Game.Rules.getWinner(board);
                        if(winner == 0) {
                            JOptionPane.showMessageDialog(null,
                                    "Draw",
                                    "Game Over",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else if(winner == _player){
                            JOptionPane.showMessageDialog(null,
                                    "You Win!!!",
                                    "Game Over",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else{
                            JOptionPane.showMessageDialog(null,
                                    "You Lose!!!",
                                    "Game Over",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    }
                }
            }).start();
        }

        public void initializebuttons()
        {
            for(int i = 0; i < 3; i++)
            {
                for(int j = 0; j < 3; j++) {
                    buttons[i][j] = new JButton();
                    buttons[i][j].setText("");
                    buttons[i][j].addActionListener(new buttonListener(this, i, j));

                    add(buttons[i][j]);
                }
            }
        }

        private class buttonListener implements ActionListener
        {
            public Game.Player _player;
            public int _row;
            public int _col;

            public buttonListener(Game.Player player, int row, int col){
                _player = player;
                _row = row;
                _col = col;
            }
            public void actionPerformed(ActionEvent e)
            {
                _game.makeMove(_player, _row, _col);
            }
        }

        public static void Launch(Game game){
            JFrame window = new JFrame("Tic-Tac-Toe");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            GuiClient client = new GuiClient(game);
            window.getContentPane().add(client);
            window.setBounds(300,200,300,300);
            window.setVisible(true);
        }
    }

    public static void main(String [] args) {
        Game game = new Game();
//        Local p1 = new Local(game);
//        new Thread(p1).start();
        GuiClient.Launch(game);
        Dumb p2 = new Dumb(game);
        new Thread(p2).start();
    }
}
