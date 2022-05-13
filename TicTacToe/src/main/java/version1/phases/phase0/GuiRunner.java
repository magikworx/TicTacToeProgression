package version1.phases.phase0;

import version1.tictactoe.channels.DirectChannel;
import version1.tictactoe.ui.SwingUi;

import javax.swing.*;

public class GuiRunner {
    public static void main(String [] args) {
        DirectChannel channel = new DirectChannel();

        JFrame window = new JFrame("Tic-Tac-Toe");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUi gui = new SwingUi(channel);
        window.getContentPane().add(gui);
        window.setBounds(300,200,300,300);
        window.setVisible(true);

        Runner.Run(channel);
    }
}
