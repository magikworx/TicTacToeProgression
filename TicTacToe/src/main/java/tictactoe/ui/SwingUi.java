package tictactoe.ui;

import tictactoe.BoardMarkers;
import tictactoe.GameStates;
import tictactoe.MoveValidationErrors;
import tictactoe.channels.DirectChannel;
import util.Terminal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingUi extends JPanel {
    DirectChannel _channel;
    JButton[][] _buttons = new JButton[3][3];

    public SwingUi(DirectChannel channel) {
        _channel = channel;
        setLayout(new GridLayout(3,3));
        initializebuttons();
        new Thread(()->{
            while(true) {
                var newState = _channel.getUpdate();
                var state = newState.get_first();
                var board = newState.get_second();
                if (board != null) {
                    update(state, board);
                    switch (state) {
                        case Won:
                        case Lost:
                        case Draw:
                            return;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        setVisible(true);
    }

    public void initializebuttons()
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++) {
                _buttons[i][j] = new JButton();
                _buttons[i][j].setText("");
                _buttons[i][j].addActionListener(new buttonListener(i, j));

                add(_buttons[i][j]);
            }
        }
    }

    public void updateBoard(BoardMarkers[][] board) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    var marker = board[i][j];
                    if (marker == BoardMarkers.Empty) {
                        _buttons[i][j].setText("");
                    }
                    else {
                        _buttons[i][j].setText(marker.toString());
                    }
                }
            }
        });
    }

    public void update(GameStates state, BoardMarkers[][] board) {
        updateBoard(board);
        switch (state) {
            case Won:
                JOptionPane.showMessageDialog(null,
                        "You Win!!!",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case Lost:
                JOptionPane.showMessageDialog(null,
                        "You Lose!!!",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case Draw:
                JOptionPane.showMessageDialog(null,
                    "Draw",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    private class buttonListener implements ActionListener
    {
        public int _row;
        public int _col;

        public buttonListener(int row, int col){
            _row = row;
            _col = col;
        }
        public void actionPerformed(ActionEvent e)
        {
            var result = _channel.madeMove(_row, _col);
        }
    }
}
