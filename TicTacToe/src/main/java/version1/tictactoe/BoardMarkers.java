package version1.tictactoe;

public enum BoardMarkers {
    X,
    O,
    Empty;

    @Override
    public String toString() {
        switch (this){
            case X: return "X";
            case O: return "O";
            case Empty: return "";
        }
        return "";
    }
}
