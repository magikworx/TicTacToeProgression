package step0;

public class Runner {
    public static void main(String [] args) {
        Player xs = new Player(Game.Markers.X);
        Player os = new Player(Game.Markers.O);
        Game game = new Game(xs, os);
        while(!game.isGameOver()) {
            game.updateAndRender();
        }
        game.updateAndRender();
    }
}
