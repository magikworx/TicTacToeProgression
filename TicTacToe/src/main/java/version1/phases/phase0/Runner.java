package version1.phases.phase0;

import version1.tictactoe.Game;
import version1.tictactoe.channels.IServerChannel;
import version1.tictactoe.players.ComputerPlayer;
import version1.tictactoe.players.ChannelPlayer;

public class Runner {
    public static void Run(IServerChannel channel) {
        var lp = new ChannelPlayer(channel);
        var cp = new ComputerPlayer(); //new SmartComputerPlayer();
        Game game = new Game(lp, cp);
        game.newGame();
        while(!game.isGameOver()) {
            game.next();
        }
        game.gameOver();
    }
}
