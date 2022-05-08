package tictactoe;

public class Move {
    public enum Types {StillThinking, Moved};
    private final Types _type;
    private final int _row;
    private final int _column;

    private Move(Types type, int row, int column) {
        _type = type;
        _row = row;
        _column = column;
    }

    public static Move stillThinking(){
        return new Move(Types.StillThinking, 0,0);
    }

    public static Move makeMove(int row, int column) {
        return new Move(Types.Moved, row, column);
    }

    public Types getType() {
        return _type;
    }
    public int getColumn() {
        return _column;
    }
    public int getRow() {
        return _row;
    }
}
