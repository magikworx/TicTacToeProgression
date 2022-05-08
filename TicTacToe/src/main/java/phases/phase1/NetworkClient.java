package phases.phase1;

import jdk.jfr.Frequency;
import tictactoe.Board;
import tictactoe.Game;
import tictactoe.Move;
import tictactoe.clients.IClient;
import util.Terminal;
import util.UDP;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkClient implements Runnable {
    public IClient _client;
    public Board _board = new Board(3);

    public NetworkClient(IClient client){
        _client = client;
    }

    @Override
    public void run() {
        try {
            /// create UDP socket
            DatagramSocket sock = new DatagramSocket();
            // create a socket that can listen in on localhost port 8889
            // construct the UDP packet using the buffer and fill in the IP headers for
            // localhost:8889
            InetAddress receiverIp = InetAddress.getByName("localhost");
            int receiverPort = 8889;

            boolean shouldExit = false;
            while (!shouldExit) { // make moves until the user quits
                // fetch the board to print to the screen
                sendGetStatus(sock, receiverIp, receiverPort);
                byte[] response = receiveResponse(sock);
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; ++j){
                        int offset = i * 3 + j + 2;
                        if (response[offset] == 1) _board.setMarker(Game.Marker.X, i, j);
                        if (response[offset] == 2) _board.setMarker(Game.Marker.O, i, j);
                    }
                }
                switch (response[1]) {
                    case 0:
                        break;
                    case 1:
                        int row;
                        int col;
                        do {
                            Move nextMove = _client.NextMove(_board);
                            if (nextMove.getType() == Move.Types.Moved) {
                                row  = nextMove.getRow();
                                col = nextMove.getColumn();
                                break;
                            }
                        } while(true);
                        sendMakeMove(sock, receiverIp, receiverPort, row, col);
                        receiveResponse(sock);
                        break;
                    case 2:
                        _client.Win(_board);
                        break;
                    case 3:
                        _client.Lose(_board);
                        break;
                    case 4:
                        _client.Draw(_board);
                        break;
                }
                shouldExit = response[0] == 0 && response[1] > 1;
            }

            // clean up connection
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Terminal.println("Client exited");
    }

    public void start(){
        new Thread(this).start();
    }

    /**
     * Listen on the connection and parse results
     * @param sock connection listen on
     * @return parsed response
     * @throws IOException
     */
    public static byte[] receiveResponse(DatagramSocket sock) throws IOException {
        return UDP.receive(sock);
    }

    /**
     * Send the command for starting a new game
     * @param sock connection to use
     * @param receiverIp where to send it
     * @param receiverPort what port to send it on
     * @throws IOException in case of failure sending
     */
    public static void sendGetStatus(DatagramSocket sock, InetAddress receiverIp, int receiverPort) throws IOException {

        byte[] buffer = { 0, 0, 0 };
        UDP.send(sock, receiverIp, receiverPort, buffer);
    }

    /**
     * Send the command for starting a new game
     * @param sock connection to use
     * @param receiverIp where to send it
     * @param receiverPort what port to send it on
     * @throws IOException in case of failure sending
     */
    public static void sendMakeMove(DatagramSocket sock, InetAddress receiverIp, int receiverPort, int row, int col) throws IOException {

        byte[] buffer = { 1, (byte)row, (byte)col };
        UDP.send(sock, receiverIp, receiverPort, buffer);
    }
}
