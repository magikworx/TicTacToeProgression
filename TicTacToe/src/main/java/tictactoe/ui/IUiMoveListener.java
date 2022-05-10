package tictactoe.ui;

import tictactoe.MoveValidationErrors;

public interface IUiMoveListener {
    MoveValidationErrors madeMove(int row, int col);
}
