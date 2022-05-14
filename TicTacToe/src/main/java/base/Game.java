package base;

public class Game {
    public interface Player {

    }

    Player _xs;
    Player _os;
    int[] _board = new int[9];

    public int addPlayer(Player player) {
        if(_xs == null) {
            _xs = player;
            return 1;
        } else if(_os == null) {
            _os = player;
            return 2;
        }
        return 0;
    }

    public synchronized int[] getStatus() {
        int[] board = new int[9];
        System.arraycopy(_board, 0, board, 0, 9);
        return board;
    }

    public synchronized int makeMove(Player player, int row, int column) {
        var currentPlayer = Rules.getCurrentPlayer(_board);
        if((currentPlayer == 1 && player == _xs)
                || (currentPlayer == 2 && player == _os)){
            if (row < 0 || row > 2) return 1;
            if (column < 0 || column > 2) return 2;
            if (_board[row * 3 + column] != 0) return 3;
            _board[row * 3 + column] = currentPlayer;
        }
        return -1;
    }

    public static class Rules {
        public static int hashBoard(int[] board) {
            int sum = 0;
            for(var cell : board){
                sum += cell;
            }
            return sum;
        }
        public static boolean isGameOver(int[] board) {
            return boardFull(board)
                    || rowWin(board) > 0
                    || colWin(board) > 0
                    || diagWin(board) > 0;
        }
        public static int getWinner(int[] board) {
            return Math.max(Math.max(rowWin(board), colWin(board)), diagWin(board));
        }
        public static int getCurrentPlayer(int[] board) {
            if(isGameOver(board)) return 0;
            int zeros = 0;
            for(var cell : board){
                if(cell == 0) zeros++;
            }
            return (zeros % 2 == 1) ? 1 : 2;
        }
        public static boolean boardFull(int[] board) {
            for(var i : board) {
                if (i == 0) return false;
            }
            return true;
        }
        public static int rowWin(int[] board) {
            for(int row = 0; row < 3; ++row) {
                int left = row * 3;
                int middle = left + 1;
                int right = middle + 1;
                if (board[left] > 0
                        && board[left] == board[middle]
                        && board[left] == board[right]) {
                    return board[left];
                }
            }
            return 0;
        }
        public static int colWin(int[] board) {
            for(int col = 0; col < 3; ++col) {
                int top = col;
                int middle = 3 + col;
                int bottom = 6 + col;
                if (board[top] > 0
                        && board[top] == board[middle]
                        && board[top] == board[bottom]) {
                    return board[top];
                }
            }
            return 0;
        }
        public static int diagWin(int[] board) {
            int topleft = 0;
            int topright = 2;
            int middle = 4;
            int bottomleft = 6;
            int bottomright = 8;
            if (board[topleft] > 0
                    && board[topleft] == board[middle]
                    && board[topleft] == board[bottomright]) {
                return board[topleft];
            }
            if (board[topright] > 0
                    && board[topright] == board[middle]
                    && board[topright] == board[bottomleft]) {
                return board[topleft];
            }
            return 0;
        }
    }
}
