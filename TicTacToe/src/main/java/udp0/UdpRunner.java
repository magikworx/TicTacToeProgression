package udp0;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

import base.Game;
import starter.Runner;
import util.Optional;
import util.Pair;
import util.Terminal;
import util.UDP;

public class UdpRunner {
    public static class Dumb implements Game.Player, Runnable {
        public int _player;
        public Game _game;
        public Dumb(Game game) {
            _game = game;
            _player = _game.addPlayer(this);
        }

        @Override
        public void run() {
            while (true){
                var board = _game.getStatus();
                if (Game.Rules.getCurrentPlayer(board) == _player) {
                    int i = 0;
                    for(int row = 0; row < 3; ++row){
                        for(int col = 0; col < 3; ++col){
                            if (board[i++] == 0) _game.makeMove(this, row, col);
                        }
                    }
                }
                if (Game.Rules.isGameOver(board)) break;
            }
        }
    }
    public static class UdpLocal implements Game.Player, Runnable {
        public int _player;
        public Game _game;
        public UdpLocal(Game game) {
            _game = game;
            _player = _game.addPlayer(this);
        }


        public byte[] processGetStatus() {
            byte[] response = new byte[11];
            response[0] = 0;
            response[1] = (byte)_player;
            var board = _game.getStatus();
            for(int i = 0; i < 9; ++i) {
                response[i+2] = (byte) board[i];
            }
            return response;
        }

        public byte[] processMakeMove(byte [] request) {
            byte[] response = new byte[2];
            response[0] = 1;
            response[1] = (byte)_game.makeMove(this, request[1], request[2]);
            return response;
        }

        public byte[] process(byte[] request) {
            switch(request[0]){
                case 0:
                    return processGetStatus();
                case 1:
                    return processMakeMove(request);
                default:
                    return new byte[]{6};
            }
        }

        @Override
        public void run() {
            try (DatagramSocket sock = new DatagramSocket(8889)) {
                // create a socket that can listen in on localhost port 8889

                boolean shouldExit = false;
                while (!shouldExit) {
//                    System.out.println("Waiting for packet:");
                    DatagramPacket pack = UDP.receiveRawPacket(sock);

                    if (pack.getLength() == 0) {
                        shouldExit = true;
                        continue;
                    }

                    // on successful receipt of packet, populate the receive packet object
                    byte[] buffer = pack.getData();
                    byte[] response = process(buffer);
                    UDP.send(sock, pack.getAddress(), pack.getPort(), response);
                    shouldExit = response[0] == 0 && response[1] > 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static class Local implements Runnable {
        public void printBoard(int[] board) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for(int row = 0; row < 3; ++row){
                sb.append("|");
                for(int col = 0; col < 3; ++col){
                    var player = board[i++];
                    if (player == 1) sb.append("X");
                    else  if (player == 2) sb.append("O");
                    else sb.append(" ");
                    sb.append("|");
                }
                sb.append("\n");
            }
            Terminal.println(sb.toString());
        }

        @Override
        public void run() {
            try(DatagramSocket sock = new DatagramSocket()) {
                /// create UDP socket

                // create a socket that can listen in on localhost port 8889
                // construct the UDP packet using the buffer and fill in the IP headers for
                // localhost:8889
                InetAddress receiverIp = InetAddress.getByName("localhost");
                int receiverPort = 8889;

                int last = -1;
                while (true){
                    byte[] buffer = { 0, 0, 0 };
                    UDP.send(sock, receiverIp, receiverPort, buffer);
                    byte[] response = UDP.receive(sock);
                    var player = response[1];
                    var board = new int[9];
                    for (int i = 0; i < 9; ++i){
                        board[i] = response[i+2];
                    }

                    int boardHash = Game.Rules.hashBoard(board);
                    if (boardHash != last) {
                        printBoard(board);
                        last = boardHash;
                    }
                    if (Game.Rules.getCurrentPlayer(board) == player) {
                        int row = Terminal.getIntFromChoice("Enter row[0,1,2]: ", 0, 1, 2);
                        int col = Terminal.getIntFromChoice("Enter column[0,1,2]: ", 0, 1, 2);
                        byte[] move_buffer = { 1, (byte)row, (byte)col };
                        UDP.send(sock, receiverIp, receiverPort, move_buffer);
                        byte[] move_resp = UDP.receive(sock);
                        int validation = move_resp[1];
                        switch (validation) {
                            case 1: Terminal.println("Invalid row"); break;
                            case 2: Terminal.println("Invalid column"); break;
                            case 3: Terminal.println("Already Occupied"); break;
                        }
                    }
                    if (Game.Rules.isGameOver(board)) {
                        var winner = Game.Rules.getWinner(board);
                        if(winner == 0) {
                            Terminal.println("Draw");
                        } else if(winner == player){
                            Terminal.println("You WIN!!!");
                        } else{
                            Terminal.println("You lose");
                        }
                        UDP.send(sock, receiverIp, receiverPort, new byte[0]);
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class UdpGuiClient extends JPanel {
        public int _last = -1;

        JButton[][] buttons = new JButton[3][3];

        public UdpGuiClient() {
            setLayout(new GridLayout(3, 3));
            initializebuttons();

            new Thread(() -> {
                try (DatagramSocket sock = new DatagramSocket()) {
                    /// create UDP socket

                    // create a socket that can listen in on localhost port 8889
                    // construct the UDP packet using the buffer and fill in the IP headers for
                    // localhost:8889
                    InetAddress receiverIp = InetAddress.getByName("localhost");
                    int receiverPort = 8889;

                    while (true) {
                        byte[] buffer = {0, 0, 0};
                        UDP.send(sock, receiverIp, receiverPort, buffer);
                        byte[] response = UDP.receive(sock);
                        var player = response[1];
                        var board = new int[9];
                        for (int i = 0; i < 9; ++i) {
                            board[i] = response[i + 2];
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
                            byte[] move_buffer = {1, (byte) row, (byte) col};
                            UDP.send(sock, receiverIp, receiverPort, move_buffer);
                            byte[] move_resp = UDP.receive(sock);
                            int validation = move_resp[1];
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
                            UDP.send(sock, receiverIp, receiverPort, new byte[0]);
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
        public static void Launch(){
            JFrame window = new JFrame("Tic-Tac-Toe");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            UdpGuiClient client = new UdpGuiClient();
            window.getContentPane().add(client);
            window.setBounds(300,200,300,300);
            window.setVisible(true);
        }
    }

    public static void main(String [] args) {
        Game game = new Game();
        UdpLocal p1s = new UdpLocal(game);
        new Thread(p1s).start();
//        Local p1 = new Local();
//        new Thread(p1).start();
        UdpGuiClient.Launch();
        starter.Runner.Dumb p2 = new starter.Runner.Dumb(game);
        new Thread(p2).start();
    }
}
