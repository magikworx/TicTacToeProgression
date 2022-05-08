package tictactoe.clients;

import javax.swing.*;

import tictactoe.Board;
import tictactoe.Game;
import tictactoe.Move;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiClient extends JPanel implements IClient {
    JButton[][] buttons = new JButton[3][3];
    int alternate = 0;//if this number is a even, then put a X. If it's odd, then put an O

    public GuiClient() {
        setLayout(new GridLayout(3,3));
        initializebuttons();
        show();
    }
    public void initializebuttons()
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setText("");
                buttons[i][j].addActionListener(new buttonListener(i, j));

                add(buttons[i][j]);
            }
        }
    }

    public int _rowRequest = -1;
    public int _colRequest = -1;

    public int _boardHash;

    @Override
    public Move NextMove(Board board) {
        if (board.hashCode() != _boardHash) {
            _rowRequest = -1;
            _colRequest = -1;
            updateBoard(board);
            ((JFrame)SwingUtilities.getRoot(this)).setTitle("Your Move");
        } else if (_colRequest >= 0 && _rowRequest >= 0) {
            var result = board.validateMove(_rowRequest, _colRequest);

            var move = Move.makeMove(_rowRequest, _colRequest);
            _rowRequest = -1;
            _colRequest = -1;

            if (!result.get_first()){
                JOptionPane.showMessageDialog(null,
                        result.get_second(),
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                ((JFrame)SwingUtilities.getRoot(this)).setTitle("Waiting for other player");
                return move;
            }
        }
        return Move.stillThinking();
    }

    public void updateBoard(Board board) {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                var marker = board.getMarker(i, j);
                if (marker == Game.Marker.X) {
                    buttons[i][j].setText("X");
                } else if (marker == Game.Marker.O) {
                    buttons[i][j].setText("O");
                } else {
                    buttons[i][j].setText("");
                }
            }
        }
        _boardHash = board.hashCode();
    }

    @Override
    public void Win(Board board) {
        updateBoard(board);
        JOptionPane.showMessageDialog(null,
                "You Win!!!",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void Lose(Board board) {
        updateBoard(board);
        JOptionPane.showMessageDialog(null,
                "You Lose!!!",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void Draw(Board board) {
        updateBoard(board);
        JOptionPane.showMessageDialog(null,
                "Draw",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
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
            _rowRequest = _row;
            _colRequest = _col;
        }
    }
}
