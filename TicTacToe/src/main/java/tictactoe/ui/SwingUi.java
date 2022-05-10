package tictactoe.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingUi extends JPanel implements IUi {
    JButton[][] _buttons = new JButton[3][3];
    IUiMoveListener _listener;

    public SwingUi() {
        setLayout(new GridLayout(3,3));
        initializebuttons();
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

    public void addEventListener(IUiMoveListener listener) {
        _listener = listener;
    }

    public void update(States state, BoardMarkers[][] board) {
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
            if(_listener != null) {
                IUiMoveListener.ValidationErrors errors = _listener.madeMove(_row, _col);
            }
        }
    }
}
