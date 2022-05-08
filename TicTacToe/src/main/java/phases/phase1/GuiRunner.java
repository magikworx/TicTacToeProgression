package phases.phase1;

import phases.phase0.Runner;
import tictactoe.clients.GuiClient;
import tictactoe.clients.TerminalClient;

import javax.swing.*;

public class GuiRunner {
    public static void main(String [] args) {
        JFrame window = new JFrame("Tic-Tac-Toe");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GuiClient client = new GuiClient();
        window.getContentPane().add(client);
        window.setBounds(300,200,300,300);
        window.setVisible(true);

        NetworkClientHost nps = new NetworkClientHost();
        nps.start();
        NetworkClient npc = new NetworkClient(client);
        npc.start();

        Runner.Run(nps);
    }
}
