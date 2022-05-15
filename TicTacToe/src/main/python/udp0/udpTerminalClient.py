import socket

import game


def print_board(board):
    out = ""
    for i in range(3):
        row = "|"
        for j in range(3):
            val = board[i*3 + j]
            if val == 1:
                row = row + "X|"
            elif val == 2:
                row = row + "O|"
            else:
                row = row + " |"
        out = out + row + "\n"
    print(out)


def getIntFromChoice(prompt, *args):
    while True:
        val_str = input(prompt)
        if val_str is not None:
            val = int(val_str)
            if val is not None and val in args:
                return val
            else:
                print("invalid choice")


serverAddressPort = ("127.0.0.1", 8889)
bufferSize = 1024

# Create a UDP socket at client side
with socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM) as sock:
    last = -1
    while True:
        sock.sendto(bytes([0, 0, 0]), serverAddressPort)
        (response_bytes, response_sock) = sock.recvfrom(bufferSize)
        player = int(response_bytes[1])
        board = response_bytes[2:]

        newHash = game.Rules.hash_board(board)
        if last != newHash:
            print_board(board)
            last = newHash

        if game.Rules.get_current_player(board) == player:
            row = getIntFromChoice("Enter row[0,1,2]: ", 0, 1, 2)
            col = getIntFromChoice("Enter column[0,1,2]: ", 0, 1, 2)
            sock.sendto( bytes([1, row, col]), serverAddressPort)
            (response_bytes, response_sock) = sock.recvfrom(bufferSize)
            validation = response_bytes[1]
            if validation == 1:
                print("Invalid row");
            if validation == 2:
                print("Invalid column")
            if validation == 3:
                print("Already Occupied")

        if game.Rules.is_game_over(board):
            winner = game.Rules.winner(board)
            if winner == 0:
                print("Draw")
            elif winner == player:
                print("You WIN!!!")
            else:
                print("You lose")
            sock.sendto(bytes([]), serverAddressPort)
            break
