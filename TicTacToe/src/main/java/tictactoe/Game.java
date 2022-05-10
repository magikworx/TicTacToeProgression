package tictactoe;

import tictactoe.players.IPlayer;

import java.util.Random;

public class Game {
  static Random rand = new Random();

  private final Board _board = new Board(3);
  private final IPlayer _player1;
  private final IPlayer _player2;
  private IPlayer _xs;
  private IPlayer _os;
  private IPlayer _currentPlayer;

  public Game(IPlayer player1, IPlayer player2){
    _player1 = player1;
    _player2 = player2;
  }

  /**
   * Resets the board and the markers
   */
  public void newGame() {
    _board.reset();

    if(rand.nextBoolean()) {
      _xs = _player1;
      _os = _player2;
    } else {
      _xs = _player2;
      _os = _player1;
    }

    _xs.setMarker(BoardMarkers.X);
    _os.setMarker(BoardMarkers.O);
    _currentPlayer = _xs;
  }
  
  /**
   * Get the current player
   * @return player who's turn it is
   */
  public IPlayer getCurrentPlayer(){
    return _currentPlayer;
  }

  /**
   * Check for game over conditions
   *
   * @return true if over, false if not
   */
  public boolean isGameOver() {
    return _board.isGameOver();
  }

  /**
   * Builds a string output of the current board
   *
   * @return byte string value of the current board (0x00 - unassigned, 0x01 - X,
   *         0x02- O)
   */
  public Board getBoard() {
    return _board;
  }

  public void next() {
    if (isGameOver()) return;
    var pair = _currentPlayer.getMove(_board);
    _board.setMarker(_currentPlayer.getMarker(), pair.get_first(), pair.get_second());
    if (_currentPlayer == _xs) {
      _currentPlayer = _os;
    } else{
      _currentPlayer = _xs;
    }
  }

  public void gameOver() {
    if (_board.hasXWon()) {
      _xs.win(_board);
      _os.lose(_board);
    } else if (_board.hasOWon()) {
      _xs.lose(_board);
      _os.win(_board);
    } else {
      _xs.draw(_board);
      _os.draw(_board);
    }
  }
}
