package starter;

import base.Game;
import util.Pair;

public class SmartAi implements Game.Player, Runnable {
    public int _player;
    public Game _game;

    public SmartAi(Game game) {
        _game = game;
        _player = _game.addPlayer(this);
    }

    public int[][] convert(int[] board) {
        int[][] board2d = new int[3][3];
        for (int row = 0; row < 3; ++row) {
            board2d[row] = new int[3];
            System.arraycopy(board, row * 3, board2d[row], 0, 3);
        }
        return board2d;
    }

    public int[] convert(int[][] board2d) {
        int[] board = new int[9];
        for (int row = 0; row < 3; ++row) {
            System.arraycopy(board2d[row], 0, board, row*3, 3);
        }
        return board;
    }

    @Override
    public void run() {
        while (true) {
            var board = _game.getStatus();
            if (Game.Rules.getCurrentPlayer(board) == _player) {
                int[][]board2d = convert(board);
                var move = getMove(board2d);
                _game.makeMove(this, move.get_first(), move.get_second());
            }
            if (Game.Rules.isGameOver(board)) break;
        }
    }
    public Pair<Integer, Integer> getMove(int[][] board) {
        // set up storage
        Pair<Integer, Integer> result = new Pair<>(0, 0);
        int best = Integer.MIN_VALUE;

        // for each position
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                // find an empty
                if (board[i][j] == 0) {
                    // fill it
                    board[i][j] = _player;
                    // evaluate the position
                    var score = minimax(board, 1, false);
                    board[i][j] = 0;

                    // store if it's better than the previous best
                    if (score > best) {
                        best = score;
                        result.set_first(i);
                        result.set_second(j);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Performs a minimax analysis of a particular position and returns likelihood of winning from the position
     * @param board to analyze
     * @param depth the weight for analysis based on distance from root
     * @param maximizing false if their turn, true if mine
     * @return range from -10(losing) to 10(winning)
     */
    public int minimax(int[][] board, int depth, boolean maximizing) {
        // Determine which player is being evaluated
        int player = _player;
        int otherPlayer = player == 1? 2 : 1;
        int currentMarker = maximizing ? player : otherPlayer;

        if (Game.Rules.isGameOver(convert(board))) { // found a leaf node
            return score(board, depth); // give the score for the other player
        }

        // setup start values based on whose turn it is
        var best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // for each cell
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                // if it's available
                if (board[i][j] == 0) {

                    // go to the leaf and evaluate it
                    board[i][j] = currentMarker;
                    var score = minimax(board, depth + 1, !maximizing); // recurse to every node and evaluate
                    board[i][j] = 0;

                    // save it if this choice was better for who's turn it is
                    best = maximizing ? Integer.max(score, best) : Integer.min(score, best);
                }
            }
        }

        return best;
    }

    /**
     * Evaluates a position and assigns a score
     * @param board to analyze
     * @param depth to weight by
     * @return a score from -10 to 10 if game over, 0 if still playing
     */
    public int score(int[][] board, int depth) {
        int winner = Game.Rules.getWinner(convert(board));
        if (winner == 1) {
            if (_player == 1) {
                return 10 - depth; // I have won
            } else {
                return depth - 10; // They have won
            }
        } else if (winner == 2) {
            if (_player == 2) {
                return 10 - depth; // I have won
            } else {
                return depth - 10; // They have won
            }
        }
        return 0; // Either a push or still playing
    }

    public static void Launch(Game game) {
        SmartAi p1 = new SmartAi(game);
        new Thread(p1).start();
    }
}
