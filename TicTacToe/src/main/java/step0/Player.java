package step0;

import util.Optional;
import util.Pair;
import util.Terminal;
import util.Triplet;

import java.util.function.Function;

public class Player {
    public Game.Markers _marker;
    public boolean _myTurn;
    public Game.Markers[][] _board;
    public boolean _isGameOver;
    public Game.Status.States _gameOverState;

    public Player(Game.Markers marker) {
        _marker = marker;
    }

    public void getMoveFromPlayer(Function<Triplet<Player, Integer, Integer>, Game.MoveErrors> makeMove) {
        for(var row : _board) {
            Terminal.print("|");
            for(var cell : row) {
                switch (cell) {
                    case X:
                        Terminal.print("X|");
                        break;
                    case O:
                        Terminal.print("O|");
                        break;
                    case Empty:
                        Terminal.print(" |");
                        break;
                }
            }
            Terminal.println();
        }
        int row = Terminal.getIntFromChoice("Select row (0,1,2): ", 0, 1, 2);
        int col = Terminal.getIntFromChoice("Select column (0,1,2): ", 0, 1, 2);
        var result = makeMove.apply(new Triplet<>(this, row, col));
        if (result != Game.MoveErrors.None) {
            Terminal.println(result.toString());
        }
    }

    public void update(Game.Status status, Function<Triplet<Player, Integer, Integer>, Game.MoveErrors> makeMove) {
        _isGameOver =
                status._state == Game.Status.States.XWin
                ||status._state == Game.Status.States.OWin
                || status._state == Game.Status.States.Draw;
        _gameOverState = status._state;
        _myTurn =
                (status._state == Game.Status.States.XMove && _marker == Game.Markers.X)
                || (status._state == Game.Status.States.OMove && _marker == Game.Markers.O);
        _board = status._board;

        if (_myTurn) {
            getMoveFromPlayer(makeMove);
        }
    }
}
