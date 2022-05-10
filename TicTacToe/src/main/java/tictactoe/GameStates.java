package tictactoe;

public enum GameStates {
    Waiting,
    YourMove,
    Won,
    Lost,
    Draw;

    @Override
    public String toString(){
        switch (this) {
            case Waiting:
                return "Waiting for other player";
            case YourMove:
                return "Your move";
            case Won:
                return "You Win!!";
            case Lost:
                return "You Lose";
            case Draw:
                return "Draw";
        }
        return "";
    }
}
