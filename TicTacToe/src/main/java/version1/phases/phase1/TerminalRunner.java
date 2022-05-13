package version1.phases.phase1;

import version1.phases.phase0.Runner;
import version1.tictactoe.ui.TerminalUi;

public class TerminalRunner {
    public static void main(String [] args) {
        var terminal = new TerminalUi(new UdpChannelClient());
        new Thread(terminal).start();
        Runner.Run(new UdpChannelServer());
    }
}
