package version1.phases.phase1;
//
//import tictactoe.clients.AsyncClient;
//import tictactoe.clients.IClient;
import util.Terminal;
import util.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkClientHost implements Runnable {
//    private AsyncClient _client;
//
//    public NetworkClientHost(AsyncClient client){
//        _client = client;
//    }

    /**
     *
     * @param sock
     * @return
     * @throws IOException
     */
    public static DatagramPacket receiveRequest(DatagramSocket sock) throws IOException {
        return UDP.receiveRawPacket(sock);
    }

    /**
     *
     * @param sock
     * @param receiverIp
     * @param receiverPort
     * @param buffer
     * @throws IOException
     */
    public static void sendResponse(DatagramSocket sock, InetAddress receiverIp, int receiverPort, byte[] buffer)
            throws IOException {
        UDP.send(sock, receiverIp, receiverPort, buffer);
    }

    public byte[] processGetStatus() {
        byte[] response = new byte[11];
        response[0] = 0;
//        response[1] = _client.getGameStatus();
//        System.arraycopy(_client.getBoard(), 0, response, 2, 9);
        return response;
    }

    public byte[] processMakeMove(byte [] request) {
        byte[] response = new byte[2];
        response[0] = 1;
//        response[1] = (byte)_client.makeMove(request[1], request[2]);
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
                System.out.println("Waiting for packet:");
                DatagramPacket pack = receiveRequest(sock);

                if (pack.getLength() == 0) {
                    shouldExit = true;
                    continue;
                }

                // on successful receipt of packet, populate the receive packet object
                byte[] buffer = pack.getData();
                byte[] response = process(buffer);
                sendResponse(sock, pack.getAddress(), pack.getPort(), response);
                shouldExit = response[0] == 0 && response[1] > 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Terminal.println("Client Host Exited");
    }

    public void start(){
        new Thread(this).start();
    }
}
