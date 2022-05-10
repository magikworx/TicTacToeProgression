package tictactoe.ui;

import util.Terminal;

public class TerminalUi implements IUi, Runnable{
    public IUiMoveListener _listener;

    public States _state;
    public BoardMarkers[][] _board;

    @Override
    public void update(States state, BoardMarkers[][] board) {
        _state = state;
        _board = board;
    }

    @Override
    public void addEventListener(IUiMoveListener listener) {
        _listener = listener;
    }

    public void makeMove() {
        int row = Terminal.getIntFromChoice("Select row (0,1,2): ", 0, 1, 2);
        int col = Terminal.getIntFromChoice("Select column (0,1,2): ", 0, 1, 2);
        if (_listener != null) {
            var result = _listener.madeMove(row, col);
            if (result != IUiMoveListener.ValidationErrors.None) {
                Terminal.println(result.toString());
            }
        }
    }

    void printBoard(BoardMarkers[][] board){
        StringBuilder sb = new StringBuilder();
        for (var row : board) {
            sb.append("|");
            for (var cell : row) {
                if (cell == BoardMarkers.Empty) {
                    sb.append(" |");
                } else {
                    sb.append(cell).append("|");
                }
            }
            sb.append("\n");
        }
        Terminal.println(sb.toString());
    }

    @Override
    public void run() {
        while(true) {
            if (_board != null  && _state != States.Waiting) {
                printBoard(_board);
                switch (_state){
                    case YourMove:
                        makeMove();
                        break;
                    case Won:
                        Terminal.println("You WIN!!!!");
                        return;
                    case Lost:
                        Terminal.println("You Lose");
                        return;
                    case Draw:
                        Terminal.println("Draw");
                        return;
                }
            } else {
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

