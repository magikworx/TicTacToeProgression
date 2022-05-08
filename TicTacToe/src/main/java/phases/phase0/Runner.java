package phases.phase0;

import tictactoe.Game;
import tictactoe.clients.IClient;
import tictactoe.players.ComputerPlayer;
import tictactoe.players.LocalPlayer;

public class Runner {
    public static void Run(IClient io) {
        var lp = new LocalPlayer(io);
        var cp = new ComputerPlayer(); //new SmartComputerPlayer();
        Game game = new Game(lp, cp);
        game.newGame();
        while(!game.isGameOver()) {
            game.next();
        }
        game.gameOver();
    }
}
