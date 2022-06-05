package tcp0;

import base.Game;
import tcp.Base;
import util.Terminal;
import util.Triplet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class TcpPlayerTerminal implements Runnable {
    public int _port;

    public TcpPlayerTerminal(int port) {
        _port = port;
    }

    public void printBoard(int[] board) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (int row = 0; row < 3; ++row) {
            sb.append("|");
            for (int col = 0; col < 3; ++col) {
                var player = board[i++];
                if (player == 1) sb.append("X");
                else if (player == 2) sb.append("O");
                else sb.append(" ");
                sb.append("|");
            }
            sb.append("\n");
        }
        Terminal.println(sb.toString());
    }

    @Override
    public void run() {
        try (var sock = new Socket("localhost",_port)) {
            /// create UDP socket

            // create a socket that can listen in on localhost port 8889
            // construct the UDP packet using the buffer and fill in the IP headers for
            // localhost:8889
            InetAddress receiverIp = InetAddress.getByName("localhost");

            int last = -1;
            while (true) {
                byte[] buffer = {0, 0, 0};
                byte[] response = Base.Instance.sendAndReceive(sock, buffer);
                var player = response[1];
                var board = new int[9];
                for (int i = 0; i < 9; ++i) {
                    board[i] = response[i + 2];
                }

                int boardHash = Game.Rules.hashBoard(board);
                if (boardHash != last) {
                    printBoard(board);
                    last = boardHash;
                }
                if (Game.Rules.getCurrentPlayer(board) == player) {
                    int row = Terminal.getIntFromChoice("Enter row[0,1,2]: ", 0, 1, 2);
                    int col = Terminal.getIntFromChoice("Enter column[0,1,2]: ", 0, 1, 2);
                    byte[] moveRequestBuffer = {1, (byte) row, (byte) col};
                    byte[] moveResponseBuffer = Base.Instance.sendAndReceive(sock, moveRequestBuffer);
                    int validation = moveResponseBuffer[1];
                    switch (validation) {
                        case 1:
                            Terminal.println("Invalid row");
                            break;
                        case 2:
                            Terminal.println("Invalid column");
                            break;
                        case 3:
                            Terminal.println("Already Occupied");
                            break;
                    }
                }
                if (Game.Rules.isGameOver(board)) {
                    var winner = Game.Rules.getWinner(board);
                    if (winner == 0) {
                        Terminal.println("Draw");
                    } else if (winner == player) {
                        Terminal.println("You WIN!!!");
                    } else {
                        Terminal.println("You lose");
                    }
                    Base.Instance.send(sock, new byte[0]);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Launch(int port) {
        TcpPlayerTerminal p1 = new TcpPlayerTerminal(port);
        new Thread(p1).start();
    }
}
