package udp3;

import base.Game;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class UdpPlayerServer implements Game.Player, Runnable {
    public int _port;
    public int _player;
    public Game _game;

    public UdpPlayerServer(int port, Game game) {
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
        try (DatagramSocket sock = new DatagramSocket(_port)) {
            // create a socket that can listen in on localhost port 8889

            ArrayBlockingQueue<Byte> last = new ArrayBlockingQueue<>(10);
            boolean shouldExit = false;
            while (!shouldExit) {
//                    System.out.println("Waiting for packet:");
                DatagramPacket pack = UDP.receiveRawPacket(sock);
                var pack_res = UDP.Checksummed.unpack(pack.getData());
                if(!pack_res.isPresent()) {
                    continue;
                }

                byte id = pack_res.get().get_first();
                byte[] buffer = pack_res.get().get_second();

                if (buffer.length == 0) {
                    shouldExit = true;
                    continue;
                }

                if(last.contains(id)){ continue; }
                if (last.remainingCapacity() == 0) last.remove();
                last.add(id);

                // on successful receipt of packet, populate the receive packet object
                byte[] response = process(buffer);
                UDP.Checksummed.send(sock, pack.getAddress(), pack.getPort(), id, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Launch(int port, Game game) {
        UdpPlayerServer p1 = new UdpPlayerServer(port, game);
        new Thread(p1).start();
    }
}