package starter;

import base.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LocalGui extends JPanel implements Game.Player {
    public int _player;
    public Game _game;
    public int _last = -1;

    JButton[][] buttons = new JButton[3][3];

    public LocalGui(Game game) {
        setLayout(new GridLayout(3, 3));
        initializebuttons();
        _game = game;
        _player = _game.addPlayer(this);

        new Thread(() -> {
            while (true) {
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
                    if (winner == 0) {
                        JOptionPane.showMessageDialog(null,
                                "Draw",
                                "Game Over",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (winner == _player) {
                        JOptionPane.showMessageDialog(null,
                                "You Win!!!",
                                "Game Over",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
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

    public void initializebuttons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setText("");
                buttons[i][j].addActionListener(new buttonListener(this, i, j));

                add(buttons[i][j]);
            }
        }
    }

    private class buttonListener implements ActionListener {
        public Game.Player _player;
        public int _row;
        public int _col;

        public buttonListener(Game.Player player, int row, int col) {
            _player = player;
            _row = row;
            _col = col;
        }

        public void actionPerformed(ActionEvent e) {
            _game.makeMove(_player, _row, _col);
        }
    }

    public static void Launch(Game game) {
        JFrame window = new JFrame("Tic-Tac-Toe");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LocalGui client = new LocalGui(game);
        window.getContentPane().add(client);
        window.setBounds(300, 200, 300, 300);
        window.setVisible(true);
    }
}
