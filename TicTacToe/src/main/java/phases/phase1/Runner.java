package phases.phase1;

import tictactoe.Game;
import tictactoe.clients.IClient;
import tictactoe.clients.TerminalClient;
import tictactoe.players.ComputerPlayer;
import tictactoe.players.LocalPlayer;

public class Runner {
    public static void main(String [] args) {
        IClient io = new TerminalClient();
        LocalPlayer lp = new LocalPlayer(io);
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(lp, cp);
        game.newGame();
        while(!game.isGameOver()) {
            game.next();
        }
        game.gameOver();
    }
}
