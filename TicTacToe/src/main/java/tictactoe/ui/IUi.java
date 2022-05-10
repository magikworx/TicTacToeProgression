package tictactoe.ui;

import tictactoe.BoardMarkers;
import tictactoe.GameStates;

public interface IUi {
    void update(GameStates state, BoardMarkers[][] board);
    void addEventListener(IUiMoveListener listener);
}
