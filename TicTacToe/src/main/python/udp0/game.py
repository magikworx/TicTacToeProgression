class Rules:
    @staticmethod
    def hash_board(board):
        return sum(board)

    @staticmethod
    def get_current_player(board):
        if Rules.is_game_over(board):
            return 0
        zeros = 0
        for cell in board:
            if cell == 0:
                zeros = zeros + 1
        if zeros % 2 == 1:
            return 1
        else:
            return 2


    @staticmethod
    def winner(board):
        return max(max(Rules.row_win(board), Rules.col_win(board)), Rules.diag_win(board))

    @staticmethod
    def is_game_over(board):
        return Rules.board_full(board) or Rules.row_win(board) > 0 or Rules.col_win(board) > 0 or Rules.diag_win(board) > 0

    @staticmethod
    def board_full(board):
        for i in board:
            if i == 0:
                return False
        return True

    @staticmethod
    def row_win(board):
        for row in range(3):
            left = row * 3
            middle = left + 1
            right = middle + 1
            if board[left] > 0 and board[left] == board[middle] and board[left] == board[right]:
                return board[left]
        return 0

    @staticmethod
    def col_win(board):
        for col in range(3):
            top = col
            middle = 3 + col
            bottom = 6 + col
            if board[top] > 0 and board[top] == board[middle] and board[top] == board[bottom]:
                return board[top]
        return 0

    @staticmethod
    def diag_win(board):
        topleft = 0
        topright = 2
        middle = 4
        bottomleft = 6
        bottomright = 8
        if board[topleft] > 0 and board[topleft] == board[middle] and board[topleft] == board[bottomright]:
            return board[topleft]
        if board[topright] > 0 and board[topright] == board[middle] and board[topright] == board[bottomleft]:
            return board[topright]
        return 0
