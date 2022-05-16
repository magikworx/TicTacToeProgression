package udp3;

import base.Game;
import udp.IdBased;
import util.Triplet;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Hashtable;
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

            // allows for least-recently used type purging
            ArrayBlockingQueue<Byte> last = new ArrayBlockingQueue<>(10);
            // to handle double move requests that are retries
            Hashtable<Byte, byte[]> lastResponses = new Hashtable<>();

            boolean shouldExit = false;
            while (!shouldExit) {
                Triplet<InetAddress, Integer, byte[]> request = IdBased.Instance.receive(sock);

                if (request.get_third().length == 0) {
                    shouldExit = true;
                    continue;
                }

                byte[] requestWithId = request.get_third();

                // filter duplicates with known answers
                var id = requestWithId[0];
                if (last.contains(id)) {
                    IdBased.Instance.send(sock, request.get_first(), request.get_second(), lastResponses.get(id));
                    continue;
                }
                // purge old responses
                if (last.remainingCapacity() == 0) {
                    lastResponses.remove(last.remove());
                }

                byte[] buffer = new byte[requestWithId.length - 1];
                System.arraycopy(requestWithId, 1, buffer, 0, buffer.length);

                // on successful receipt of packet, populate the receive packet object
                byte[] response = process(buffer);

                byte[] responseWithId = new byte[response.length + 1];
                System.arraycopy(response, 0, responseWithId, 1, response.length);
                responseWithId[0] = requestWithId[0];

                // add newest successful response
                last.add(id);
                lastResponses.put(id, responseWithId);
                IdBased.Instance.send(sock, request.get_first(), request.get_second(), responseWithId);
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