package tictactoe.players;

import tictactoe.Board;
import tictactoe.channels.DirectChannel;
import tictactoe.ui.IUi;
import tictactoe.ui.IUiMoveListener;
import util.Pair;

/**
 * Describes a local player(Human by default)
 */
public class ChannelPlayer extends BasePlayer {
    public DirectChannel _channel;

    public ChannelPlayer(DirectChannel channel){
        _channel = channel;
    }

    @Override
    public Pair<Integer, Integer> getMove(Board board) {
        _channel.update(IUi.States.YourMove, toUi(board));
        while(true) {
            var move = _channel.getMove();
            var validate = board.validateMove(move.get_first(), move.get_second());
            var returnVal = toUi(validate);
            _channel.validated(returnVal);
            if (returnVal == IUiMoveListener.ValidationErrors.None) {
                return move;
            }
        }
    }

    @Override
    public void lose(Board board) {
        _channel.update(IUi.States.Lost, toUi(board));
    }

    @Override
    public void win(Board board) {
        _channel.update(IUi.States.Won, toUi(board));
    }

    @Override
    public void draw(Board board) {
        _channel.update(IUi.States.Draw, toUi(board));
    }

    IUiMoveListener.ValidationErrors toUi(Board.ValidationErrors error) {
        switch (error){
            case None:
                return IUiMoveListener.ValidationErrors.None;
            case InvalidRow:
                return IUiMoveListener.ValidationErrors.InvalidRow;
            case InvalidColumn:
                return IUiMoveListener.ValidationErrors.InvalidColumn;
            case AlreadyOccupied:
                return IUiMoveListener.ValidationErrors.AlreadyOccupied;
        }
        return IUiMoveListener.ValidationErrors.None;
    }

    IUi.BoardMarkers[][] toUi(Board board){
        IUi.BoardMarkers[][] output = new IUi.BoardMarkers[3][3];
        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 3; ++j){
                switch (board.getMarker(i, j)) {
                    case X:
                        output[i][j] = IUi.BoardMarkers.X;
                        break;
                    case O:
                        output[i][j] = IUi.BoardMarkers.O;
                        break;
                    case Empty:
                        output[i][j] = IUi.BoardMarkers.Empty;
                        break;
                }
            }
        }
        return output;
    }
}
