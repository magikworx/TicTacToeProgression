package starter;

import base.Game;

public class Runner {
    public static void main(String[] args) {
        Game game = new Game();
        LocalTerminal.Launch(game);
        //LocalGui.Launch(game);
        DumbAi.Launch(game);
    }
}
