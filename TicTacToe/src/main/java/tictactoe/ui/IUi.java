package tictactoe.ui;

public interface IUi {
    enum States {Waiting, YourMove, Won, Lost, Draw}
    enum BoardMarkers {X, O, Empty}
    void update(States state, BoardMarkers[][] board);
    void addEventListener(IUiMoveListener listener);
}
