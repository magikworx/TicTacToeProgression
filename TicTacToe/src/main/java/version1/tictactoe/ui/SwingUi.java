package version1.tictactoe.ui;

import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import version1.tictactoe.channels.IClientChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingUi extends JPanel {
    IClientChannel _channel;
    JButton[][] _buttons = new JButton[3][3];

    public SwingUi(IClientChannel channel) {
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
                        case GameStates.Won:
                        case GameStates.Lost:
                        case GameStates.Draw:
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
                        "step0.Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case Lost:
                JOptionPane.showMessageDialog(null,
                        "You Lose!!!",
                        "step0.Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case Draw:
                JOptionPane.showMessageDialog(null,
                    "Draw",
                    "step0.Game Over",
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
