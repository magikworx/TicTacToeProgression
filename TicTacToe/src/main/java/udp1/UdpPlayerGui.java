package udp1;

import base.Game;
import udp.Repeated;
import util.Optional;
import util.Pair;
import util.Triplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpPlayerGui extends JPanel {
    public int _port;
    public int _last = -1;

    JButton[][] buttons = new JButton[3][3];

    public UdpPlayerGui(int port) {
        _port = port;
        setLayout(new GridLayout(3, 3));
        initializebuttons();

        new Thread(() -> {
            try (DatagramSocket sock = new DatagramSocket()) {
                /// create UDP socket

                // create a socket that can listen in on localhost port 8889
                // construct the UDP packet using the buffer and fill in the IP headers for
                // localhost:8889
                InetAddress receiverIp = InetAddress.getByName("localhost");

                while (true) {
                    byte[] statusRequestBuffer = {0, 0, 0};
                    Triplet<InetAddress, Integer, byte[]> statusResponse =
                            Repeated.Instance.sendAndReceive(sock, receiverIp, _port, statusRequestBuffer);
                    var statusResponseBuffer = statusResponse.get_third();
                    var player = statusResponseBuffer[1];
                    var board = new int[9];
                    for (int i = 0; i < 9; ++i) {
                        board[i] = statusResponseBuffer[i + 2];
                    }

                    int boardHash = Game.Rules.hashBoard(board);
                    if (boardHash != _last) {
                        for (int i = 0; i < 3; ++i) {
                            for (int j = 0; j < 3; ++j) {
                                var marker = board[i * 3 + j];
                                if (marker == 1) {
                                    buttons[i][j].setText("X");
                                } else if (marker == 2) {
                                    buttons[i][j].setText("O");
                                } else {
                                    buttons[i][j].setText("");
                                }
                            }
                        }
                        _last = boardHash;
                    }
                    if (_moveRequest.isPresent()) {
                        int row = _moveRequest.get().get_first();
                        int col = _moveRequest.get().get_second();
                        byte[] moveRequestBuffer = {1, (byte) row, (byte) col};
                        Triplet<InetAddress, Integer, byte[]> moveResponse =
                                Repeated.Instance.sendAndReceive(sock, receiverIp, _port, moveRequestBuffer);
                        var moveResponseBuffer = moveResponse.get_third();
                        int validation = moveResponseBuffer[1];
                        _moveRequest = Optional.empty();
                    }
                    if (Game.Rules.isGameOver(board)) {
                        var winner = Game.Rules.getWinner(board);
                        if (winner == 0) {
                            JOptionPane.showMessageDialog(null,
                                    "Draw",
                                    "Game Over",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else if (winner == player) {
                            JOptionPane.showMessageDialog(null,
                                    "You Win!!!",
                                    "Game Over",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "You Lose!!!",
                                    "Game Over",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        Repeated.Instance.send(sock, receiverIp, _port, new byte[0]);
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void initializebuttons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setText("");
                buttons[i][j].addActionListener(new buttonListener(i, j));

                add(buttons[i][j]);
            }
        }
    }

    public Optional<Pair<Integer, Integer>> _moveRequest = Optional.empty();

    private class buttonListener implements ActionListener {
        public int _row;
        public int _col;

        public buttonListener(int row, int col) {
            _row = row;
            _col = col;
        }

        public void actionPerformed(ActionEvent e) {
            _moveRequest = Optional.of(new Pair<>(_row, _col));
        }
    }

    public static void Launch(int port) {
        JFrame window = new JFrame("Tic-Tac-Toe");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UdpPlayerGui client = new UdpPlayerGui(port);
        window.getContentPane().add(client);
        window.setBounds(300, 200, 300, 300);
        window.setVisible(true);
    }
}
