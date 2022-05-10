package tictactoe.players;

import tictactoe.Board;
import tictactoe.BoardMarkers;
import tictactoe.GameStates;
import tictactoe.channels.DirectChannel;
import tictactoe.MoveValidationErrors;
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
        while(true) {
            var move = _channel.getMove();
            var validate = board.validateMove(move.get_first(), move.get_second());
            var returnVal = toUi(validate);
            _channel.validated(returnVal);
            if (returnVal == MoveValidationErrors.None) {
                return move;
            }
        }
    }

    @Override
    public void updateStatus(GameStates state, Board board) {
        _channel.updateStatus(state, toUi(board));
    }

    MoveValidationErrors toUi(MoveValidationErrors error) {
        switch (error){
            case None:
                return MoveValidationErrors.None;
            case InvalidRow:
                return MoveValidationErrors.InvalidRow;
            case InvalidColumn:
                return MoveValidationErrors.InvalidColumn;
            case AlreadyOccupied:
                return MoveValidationErrors.AlreadyOccupied;
        }
        return MoveValidationErrors.None;
    }

    BoardMarkers[][] toUi(Board board){
        BoardMarkers[][] output = new BoardMarkers[3][3];
        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 3; ++j){
                switch (board.getMarker(i, j)) {
                    case X:
                        output[i][j] = BoardMarkers.X;
                        break;
                    case O:
                        output[i][j] = BoardMarkers.O;
                        break;
                    case Empty:
                        output[i][j] = BoardMarkers.Empty;
                        break;
                }
            }
        }
        return output;
    }
}
