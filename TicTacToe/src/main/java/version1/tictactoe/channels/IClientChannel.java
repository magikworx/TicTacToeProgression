package version1.tictactoe.channels;

import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import version1.tictactoe.MoveValidationErrors;
import util.Pair;

public interface IClientChannel {
    MoveValidationErrors madeMove(int row, int col);
    Pair<GameStates, BoardMarkers[][]> getUpdate();
}
