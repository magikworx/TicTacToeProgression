package phases.phase1;

import phases.phase0.Runner;
import tictactoe.clients.TerminalClient;

public class TerminalRunner {
    public static void main(String [] args) {
        NetworkClientHost nps = new NetworkClientHost();
        nps.start();
        NetworkClient npc = new NetworkClient(new TerminalClient());
        npc.start();
        Runner.Run(nps);
    }
}
