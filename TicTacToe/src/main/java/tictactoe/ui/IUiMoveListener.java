package tictactoe.ui;

public interface IUiMoveListener {
    enum ValidationErrors {
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
    ValidationErrors madeMove(int row, int col);
}
