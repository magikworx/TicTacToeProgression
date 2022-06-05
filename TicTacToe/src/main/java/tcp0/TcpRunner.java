package tcp0;

import base.Game;
import starter.DumbAi;

public class TcpRunner {
    public static void main(String[] args) {
        Game game = new Game();

//        DumbAi.Launch(game);
        TcpPlayerServer.Launch(8889, game);
        DumbAi.Launch(game);
        /*
        TcpPlayerGui.Launch(8889);
        /*/
        TcpPlayerTerminal.Launch(8889);
        //*/
    }
}
