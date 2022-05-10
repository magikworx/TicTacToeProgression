package phases.phase0;

import tictactoe.channels.DirectChannel;
import tictactoe.ui.IUi;
import tictactoe.Game;
import tictactoe.players.ComputerPlayer;
import tictactoe.players.ChannelPlayer;

public class Runner {
    public static void Run(IUi ui) {
        DirectChannel channel = new DirectChannel(ui);
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
