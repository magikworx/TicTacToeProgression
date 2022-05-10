package phases.phase0;

import tictactoe.channels.DirectChannel;
import tictactoe.ui.TerminalUi;

public class TerminalRunner {
    public static void main(String [] args) {
        DirectChannel channel = new DirectChannel();
        var terminal = new TerminalUi(channel);
        new Thread(terminal).start();
        Runner.Run(channel);
    }
}
