package tictactoe;

import java.util.Arrays;

/**
 * The game rules for Tic-Tac-Toe
 */
public class Board {
  public enum State {
    StillPlaying, XWins, OWins, Push, Error;

    @Override
    public String toString() {
      switch (this) {
      case StillPlaying:
        return "";
      case XWins:
        return "X Wins!";
      case OWins:
        return "O Wins!";
      case Push:
        return "Push!";

      default:
        return "Error! An unexpected error has occured.";
      }
    }
  }

  private Game.Marker[][] _board;

  public int rowSize() {
    return _board.length;
  }

  public Board(int rowSize) {
    _board = new Game.Marker[rowSize][rowSize];
    reset();
  }

  public void reset() {
    _board = new Game.Marker[rowSize()][rowSize()];
    for (Game.Marker[] markers : _board) {
      Arrays.fill(markers, Game.Marker.Empty);
    }
  }

  public void setMarker(Game.Marker marker, int row, int col) {
    _board[row][col] = marker;
  }

  public Game.Marker getMarker(int row, int col) {
    return _board[row][col];
  }

  public void clearMarker(int row, int col) {
    _board[row][col] = Game.Marker.Empty;
  }

  public boolean isEmpty(int row, int col) {
    return isEmpty(_board[row][col]);
  }

  public boolean isEmpty(Game.Marker value) {
    return value == Game.Marker.Empty;
  }

  /**
   * Check for game states
   *
   * @return GameState reflecting the games current state
   */
  public State state() {
    // game hasn't started
    if (_board == null)
      return State.Error;

    // either player won
    if (hasXWon()) {
      return State.XWins;
    }
    if (hasOWon()) {
      return State.OWins;
    }

    // is any move available?
    if (isBoardFull()) {
      return State.Push; // Push
    }
    return State.StillPlaying;
  }

  /**
   * Builds a string output of the current board
   *
   * @return string value of the current board
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Game.Marker[] row : _board) {
      sb.append("|");
      for (Game.Marker cell : row) {
        if (isEmpty(cell)) {
          sb.append(" |");
        } else {
          sb.append(cell).append("|");
        }
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  public enum ValidationErrors {
    None,
    InvalidRow,
    InvalidColumn,
    AlreadyOccupied;

    @Override
    public String toString() {
      switch (this){
        case None: return "";
        case InvalidRow: return "Invalid Row";
        case InvalidColumn: return "Invalid Column";
        case AlreadyOccupied: return "Cell already occupied";
      }
      return super.toString();
    }
  }

  public ValidationErrors validateMove(int row, int column) {
    if (row < 0 || row >= rowSize()) {
      return ValidationErrors.InvalidRow;
    }
    if (column < 0 || column >= rowSize()) {
      return ValidationErrors.InvalidColumn;
    }
    if (!isEmpty(row, column)) {
      return ValidationErrors.AlreadyOccupied;
    }
    return ValidationErrors.None;
  }

  /**
   * Check for game over conditions
   *
   * @return true if over, false if not
   */
  public boolean isGameOver() {
    return state() != State.StillPlaying;
  }

  /**
   * Is the board full
   *
   * @return true if no empty spots are available
   */
  public boolean isBoardFull() {
    for (Game.Marker[] row : _board) {
      for (Game.Marker cell : row) {
        // for each cell, is there an empty?
        if (isEmpty(cell)) {
          // if so, it's not full
          return false;
        }
      }
    }
    // if no cells are empty, board is full
    return true;
  }

  /**
   * Has the player with X won?
   *
   * @return true if X won
   */
  public boolean hasXWon() {
    return rowWin(Game.Marker.X) || colWin(Game.Marker.X) || diagonalWin(Game.Marker.X);
  }

  /**
   * Has the player with O won?
   *
   * @return true if O won
   */
  public boolean hasOWon() {
    return rowWin(Game.Marker.O) || colWin(Game.Marker.O) || diagonalWin(Game.Marker.O);
  }

  /**
   * Has a player with passed in mark won on any row?
   *
   * @param mark to check for
   * @return true if the player has won on any row
   */
  public boolean rowWin(Game.Marker mark) {
    boolean hasWon = true;
    for (Game.Marker[] row : _board) {
      // Assume the player has won
      hasWon = true;
      for (Game.Marker cell : row) {
        // Is there anything like an empty square or a different mark that
        // can disqualify the win?
        if (isEmpty(cell) || !cell.equals(mark)) {
          // If so, check the next row
          hasWon = false;
          break;
        }
      }
      // have they won?
      // if so, we should stop searching.
      if (hasWon)
        break;
    }
    return hasWon;
  }

  /**
   * Has a player with passed in mark won on any column?
   *
   * @param mark to check for
   * @return true if the player has won on any column
   */
  public boolean colWin(Game.Marker mark) {
    boolean hasWon = true;
    for (int column = 0; column < _board[0].length; column++) {
      // Assume the player has won
      hasWon = true;
      for (Game.Marker[] row : _board) {
        Game.Marker cell = row[column];
        // Is there anything like an empty square or a different mark that
        // can disqualify the win?
        if (isEmpty(cell) || !cell.equals(mark)) {
          // If so, check the next column
          hasWon = false;
          break;
        }
      }
      // have they won?
      // if so, we should stop searching.
      if (hasWon)
        break;
    }
    return hasWon;
  }

  /**
   * Has a player with passed in mark won on any diagonal?
   *
   * @param mark to check for
   * @return true if the player has won on any diagonal
   */
  public boolean diagonalWin(Game.Marker mark) {
    boolean hasWon = true;
    // Assume the player has won
    for (int i = 0; i < _board.length; i++) {
      Game.Marker cell = _board[i][i];
      // Is there anything like an empty square or a different mark that
      // can disqualify the win?
      if (isEmpty(cell) || !cell.equals(mark)) {
        // If so, check the next diagonal
        hasWon = false;
        break;
      }
    }
    // if they haven't won yet, try the other direction
    if (!hasWon) {
      // Assume the player has won
      hasWon = true;
      for (int i = 0; i < _board.length; i++) {
        // _board.length -i - 1 goes from lower left to upper right
        Game.Marker cell = _board[_board.length - i - 1][i];
        // Is there anything like an empty square or a different mark that
        // can disqualify the win?
        if (isEmpty(cell) || !cell.equals(mark)) {
          hasWon = false;
          break;
        }
      }
    }
    return hasWon;
  }

  @Override
  public int hashCode() {
    return java.util.Arrays.deepHashCode( _board );
  }
}
