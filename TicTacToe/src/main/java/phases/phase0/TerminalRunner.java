package phases.phase0;

import tictactoe.ui.TerminalUi;

public class TerminalRunner {
    public static void main(String [] args) {
        var terminal = new TerminalUi();
        new Thread(terminal).start();
        Runner.Run(terminal);
    }
}
