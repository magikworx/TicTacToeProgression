package version1.phases.phase0;

import version1.tictactoe.channels.DirectChannel;
import version1.tictactoe.ui.TerminalUi;

public class TerminalRunner {
    public static void main(String [] args) {
        DirectChannel channel = new DirectChannel();
        var terminal = new TerminalUi(channel);
        new Thread(terminal).start();
        Runner.Run(channel);
    }
}
