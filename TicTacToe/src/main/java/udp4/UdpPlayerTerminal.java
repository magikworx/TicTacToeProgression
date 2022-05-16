package udp4;

import base.Game;
import udp.IdBased;
import util.Terminal;
import util.Triplet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpPlayerTerminal implements Runnable {
    public int _port;

    public UdpPlayerTerminal(int port) {
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
        try (DatagramSocket sock = new DatagramSocket()) {
            /// create UDP socket

            // create a socket that can listen in on localhost port 8889
            // construct the UDP packet using the buffer and fill in the IP headers for
            // localhost:8889
            InetAddress receiverIp = InetAddress.getByName("localhost");

            int last = -1;
            while (true) {
                byte[] buffer = {0, 0, 0};
                Triplet<InetAddress, Integer, byte[]> responsePack =
                        IdBased.Instance.sendAndReceive(sock, receiverIp, _port, buffer);
                byte[] response = responsePack.get_third();
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
                    Triplet<InetAddress, Integer, byte[]> moveResponse =
                            IdBased.Instance.sendAndReceive(sock, receiverIp, _port, moveRequestBuffer);
                    var moveResponseBuffer = moveResponse.get_third();
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
                    IdBased.Instance.send(sock, receiverIp, _port, new byte[0]);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Launch(int port) {
        UdpPlayerTerminal p1 = new UdpPlayerTerminal(port);
        new Thread(p1).start();
    }
}
