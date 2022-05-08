package tictactoe.clients;

import tictactoe.Board;
import tictactoe.Move;
import tictactoe.players.IPlayer;
import util.Terminal;

public class TerminalClient implements IClient {
    @Override
    public Move NextMove(Board board) {
        do {
            Terminal.println(board.toString());
            int row = Terminal.getIntFromChoice("Select row (0,1,2): ", 0, 1, 2);
            int col = Terminal.getIntFromChoice("Select column (0,1,2): ", 0, 1, 2);
            var pair = board.validateMove(row, col);
            if (pair.get_first()){
                return Move.makeMove(row, col);
            } else {
                Terminal.println(pair.get_second());
            }
        } while(true);
    }

    @Override
    public void Win(Board board){
        Terminal.println(board.toString());
        Terminal.println("You WIN!!!!");
    }

    @Override
    public void Lose(Board board){
        Terminal.println(board.toString());
        Terminal.println("You Lose");
    }

    @Override
    public void Draw(Board board){
        Terminal.println(board.toString());
        Terminal.println("Draw");
    }
}
