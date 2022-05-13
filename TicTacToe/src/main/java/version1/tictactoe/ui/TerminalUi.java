package version1.tictactoe.ui;

import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import version1.tictactoe.MoveValidationErrors;
import version1.tictactoe.channels.IClientChannel;
import util.Terminal;

public class TerminalUi implements Runnable {
    public IClientChannel _channel;
    public GameStates _state;
    public BoardMarkers[][] _board;

    public TerminalUi(IClientChannel channel){
        _channel = channel;
    }

    public void makeMove() {
        int row = Terminal.getIntFromChoice("Select row (0,1,2): ", 0, 1, 2);
        int col = Terminal.getIntFromChoice("Select column (0,1,2): ", 0, 1, 2);
        var result = _channel.madeMove(row, col);
        if (result != MoveValidationErrors.None) {
            Terminal.println(result.toString());
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
            var newState = _channel.getUpdate();
            _state = newState.get_first();
            _board = newState.get_second();
            if (_board != null  && _state != GameStates.Waiting) {
                printBoard(_board);
                switch (_state) {
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

