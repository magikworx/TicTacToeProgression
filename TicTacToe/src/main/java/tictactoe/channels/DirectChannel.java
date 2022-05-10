package tictactoe.channels;

import tictactoe.ui.IUi;
import tictactoe.ui.IUiMoveListener;
import util.Pair;
import util.SimpleFuture;

public class DirectChannel implements IUiMoveListener{
    public SimpleFuture<Pair<Integer, Integer>> _moveFuture;
    public SimpleFuture<IUiMoveListener.ValidationErrors> _moveReturnFuture;

    public IUi _updateable;

    public DirectChannel(IUi updateable) {
        _updateable = updateable;
        _updateable.addEventListener(this);
    }

    @Override
    public ValidationErrors madeMove(int row, int col) {
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

    public void validated(IUiMoveListener.ValidationErrors validationResult) {
        _moveReturnFuture.put(validationResult);
    }

    public void update(IUi.States state, IUi.BoardMarkers[][] board) {
        _updateable.update(state, board);
    }
}
