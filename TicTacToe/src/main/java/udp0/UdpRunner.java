package udp0;

import base.Game;
import starter.DumbAi;

public class UdpRunner {
    public static void main(String[] args) {
        Game game = new Game();

//        DumbAi.Launch(game);
        UdpPlayerServer.Launch(8889, game);
        DumbAi.Launch(game);
        /*
        UdpPlayerGui.Launch(8889);
        /*
        UdpPlayerTerminal.Launch(8889);
        //*/
    }
}
