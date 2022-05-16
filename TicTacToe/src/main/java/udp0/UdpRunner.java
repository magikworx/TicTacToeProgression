package udp0;

import base.Game;
import starter.DumbAi;
import udp2.UdpPlayerServer;
import udp2.UdpPlayerTerminal;

public class UdpRunner {
    public static void main(String[] args) {
        Game game = new Game();


        UdpPlayerServer.Launch(8889, game);

        /*
        UdpPlayerGui.Launch(8889);
        /*/
        UdpPlayerTerminal.Launch(8889);
        //*/

        DumbAi.Launch(game);
    }
}
