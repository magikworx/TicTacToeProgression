package tcp0;

import base.Game;
import tcp.Base;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;

public class TcpPlayerServer implements Game.Player, Runnable {
    public int _port;
    public int _player;
    public Game _game;

    public TcpPlayerServer(int port, Game game) {
        _port = port;
        _game = game;
        _player = _game.addPlayer(this);
    }

    public byte[] processGetStatus() {
        byte[] response = new byte[11];
        response[0] = 0;
        response[1] = (byte) _player;
        var board = _game.getStatus();
        for (int i = 0; i < 9; ++i) {
            response[i + 2] = (byte) board[i];
        }
        return response;
    }

    public byte[] processMakeMove(byte[] request) {
        byte[] response = new byte[2];
        response[0] = 1;
        response[1] = (byte) _game.makeMove(this, request[1], request[2]);
        return response;
    }

    public byte[] process(byte[] request) {
        switch (request[0]) {
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
        // create a socket that can listen in on localhost port 8889
        try (var sock = new ServerSocket(_port)){
            boolean shouldExit = false;
            while (!shouldExit){
                try (var conn = sock.accept()) {

                    while (!shouldExit) {
                        System.out.println("Waiting for packet:");
                        byte[] buffer = Base.Instance.receive(conn);

                        if (buffer.length == 0) {
                            shouldExit = true;
                            continue;
                        }

                        // on successful receipt of packet, populate the receive packet object
                        byte[] response = process(buffer);
                        Base.Instance.send(conn, response);
                    }
                } catch (EOFException e) {
                    System.out.println("Client Disconnect");
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            // clean up connection
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Launch(int port, Game game) {
        TcpPlayerServer p1 = new TcpPlayerServer(port, game);
        new Thread(p1).start();
    }
}