package step0;

import util.Pair;
import util.Triplet;

import java.util.function.Function;

public class Game {
    public enum Markers {X, O, Empty}
    public enum MoveErrors{None, IncorrectPlayer, InvalidRow, InvalidColumn, AlreadyOccupied}

    public static class Status {
        public enum States {XMove, OMove, XWin, OWin, Draw}
        public States _state;
        public Markers[][] _board;
        public Status(States state, Markers[][] board) {
            _state = state;
            _board = board;
        }
    }

    public Markers[][] _board = new Markers[3][3];
    public int _boardMarkCount = 0;

    public Player _player1;
    public Player _player2;
    public Player _currentplayer;

    public Game(Player player1, Player player2) {
        _player1 = player1;
        _player2 = player2;
        _currentplayer = player1;
        _board = initBoard();
    }

    public Markers[][] initBoard() {
        Markers [][] output = new Markers[3][3];
        for(int i = 0; i < 3; ++i) {
            output[i] = new Markers[3];
            for(int j = 0; j < 3; ++j) {
                output[i][j] = Markers.Empty;
            }
        }
        return output;
    }

    public Markers[][] cloneBoard() {
        Markers [][] output = new Markers[3][3];
        for(int i = 0; i < _board.length; i++){
            output[i] = new Markers[3];
            System.arraycopy(_board[i], 0, output[i], 0, 3);
        }
        return output;
    }
    public void updateAndRender() {
        var board = cloneBoard();
        var status = Status.States.XMove;
        if (isGameOver()) {
            var row_pair = rowWin();
            var col_pair = colWin();
            var diag_pair = diagonalWin();
            if(row_pair.get_first() == Markers.X
                    || col_pair.get_first() == Markers.X
                    || diag_pair.get_first() == Markers.X) {
                status = Status.States.XWin;
            } else if(row_pair.get_first() == Markers.O
                    || col_pair.get_first() == Markers.O
                    || diag_pair.get_first() == Markers.O) {
                status = Status.States.OWin;
            } else {
                status = Status.States.Draw;
            }
        } else {
            if(_currentplayer._marker == Markers.X) {
                status = Status.States.XMove;
            }else{
                status = Status.States.OMove;
            }
        }
        var updateStatus = new Status(status, board);
        _player1.update(updateStatus, makeMoveLambda());
        _player2.update(updateStatus, makeMoveLambda());
    }

    public Function<Triplet<Player, Integer, Integer>, Game.MoveErrors> makeMoveLambda() {
        return (triplet) -> makeMove(triplet.get_first(), triplet.get_second(), triplet.get_third());
    }
    public MoveErrors makeMove(Player player, int row, int column) {
        if(_currentplayer != player) return MoveErrors.IncorrectPlayer;
        if(row < 0 || row >= 3) return MoveErrors.InvalidRow;
        if(column < 0 || column >= 3) return MoveErrors.InvalidColumn;
        if(_board[row][column] != Markers.Empty) return MoveErrors.AlreadyOccupied;
        _board[row][column] = _currentplayer._marker;
        _boardMarkCount++;
        if(_currentplayer == _player1) _currentplayer = _player2;
        else _currentplayer = _player1;
        return MoveErrors.None;
    }

    public boolean isGameOver() {
        return (_boardMarkCount == 9
                || rowWin().get_first() != Markers.Empty
                || colWin().get_first() != Markers.Empty
                || diagonalWin().get_first() != Markers.Empty);
    }

    public Pair<Markers, Integer> rowWin() {
        for(int row = 0; row < 3; ++row) {
            if (_board[row][0] == _board[row][1]
                    && _board[row][0] == _board[row][2]) {
                return new Pair<>(_board[row][0], row);
            }
        }
        return new Pair<>(Markers.Empty, -1);
    }
    public Pair<Markers, Integer> colWin() {
        for(int col = 0; col < 3; ++col) {
            if (_board[0][col] == _board[1][col]
                    && _board[0][col] == _board[2][col]) {
                return new Pair<>(_board[0][col], col);
            }
        }
        return new Pair<>(Markers.Empty, -1);
    }
    public Pair<Markers, Integer> diagonalWin() {
        if (_board[0][0] == _board[1][1]
                && _board[0][0] == _board[2][2]) {
            return new Pair<>(_board[0][0], 1);
        }
        if (_board[0][2] == _board[1][1]
                && _board[0][2] == _board[2][0]) {
            return new Pair<>(_board[0][2], -1);
        }
        return new Pair<>(Markers.Empty, 0);
    }
}
