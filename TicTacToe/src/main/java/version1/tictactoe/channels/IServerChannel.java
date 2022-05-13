package version1.tictactoe.channels;

import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import version1.tictactoe.MoveValidationErrors;
import util.Pair;

public interface IServerChannel {
    Pair<Integer, Integer> getMove();
    void validated(MoveValidationErrors validationResult);
    void updateStatus(GameStates state, BoardMarkers[][] board);
}
