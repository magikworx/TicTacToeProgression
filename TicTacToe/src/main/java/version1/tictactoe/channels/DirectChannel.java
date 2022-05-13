package version1.tictactoe.channels;

import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import version1.tictactoe.MoveValidationErrors;
import util.Pair;
import util.SimpleFuture;

public class DirectChannel implements IServerChannel, IClientChannel {
    Pair<GameStates, BoardMarkers[][]> _latest = new Pair<>(GameStates.Waiting, null);
    public SimpleFuture<Pair<Integer, Integer>> _moveFuture;
    public SimpleFuture<MoveValidationErrors> _moveReturnFuture;

    public MoveValidationErrors madeMove(int row, int col) {
        _moveReturnFuture = new SimpleFuture<>();
        while(true) {
            _moveFuture.put(new Pair<>(row, col));
            try {
                return _moveReturnFuture.get();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public Pair<Integer, Integer> getMove() {
        while(true) {
            _moveFuture = new SimpleFuture<>();
            try {
                return _moveFuture.get();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void validated(MoveValidationErrors validationResult) {
        _moveReturnFuture.put(validationResult);
    }

    public Pair<GameStates, BoardMarkers[][]> getUpdate() {
        return _latest;
    }

    public void updateStatus(GameStates state, BoardMarkers[][] board) {
        _latest = new Pair<>(state, board);
    }
}
