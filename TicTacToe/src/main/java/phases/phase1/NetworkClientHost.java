package phases.phase1;

import tictactoe.Board;
import tictactoe.Move;
import tictactoe.clients.IClient;
import util.Pair;
import util.Terminal;
import util.UDP;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkClientHost implements Runnable, IClient {
    public NetworkClientHost(){
    }

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
        response[1] = _gameStatus;
        System.arraycopy(_board, 0, response, 2, 9);
        return response;
    }

    public byte[] processMakeMove(byte [] request) {
        byte[] response = new byte[2];
        response[0] = 1;
        response[1] = (byte)makeMove(request[1], request[2]);
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

    private int _boardHash;
    private byte _gameStatus = 0;
    private byte _moveStatus = -1;
    private final byte[] _board = new byte[9];

    public int _rowRequest = -1;
    public int _colRequest = -1;

    public int makeMove(int row, int col){
        _moveStatus = -1;
        _rowRequest = row;
        _colRequest = col;
        try {
            while (_moveStatus < 0) Thread.sleep(100);
        } catch (InterruptedException e){}
        return _moveStatus;
    }

    @Override
    public Move NextMove(Board board) {
        if (board.hashCode() != _boardHash) {
            _rowRequest = -1;
            _colRequest = -1;
            updateBoard(board);
            _gameStatus = 1;
        } else if (_colRequest >= 0 && _rowRequest >= 0) {
            var result = board.validateMove(_rowRequest, _colRequest);

            var move = Move.makeMove(_rowRequest, _colRequest);
            _rowRequest = -1;
            _colRequest = -1;

            if (!result.get_first()){
                var error = result.get_second();
                switch (error) {
                    case "Invalid row":
                        _moveStatus = 1;
                        break;
                    case "Invalid column":
                        _moveStatus = 2;
                        break;
                    case "Cell already taken":
                        _moveStatus = 3;
                        break;
                    default:
                        _moveStatus = 4;
                        break;
                }
            } else {
                _gameStatus = 0;
                _moveStatus = 0;
                return move;
            }
        }
        return Move.stillThinking();
    }

    public void updateBoard(Board board) {
        if(_boardHash == board.hashCode()) return;

        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 3; ++j){
                switch (board.getMarker(i, j)){
                    case X:
                        _board[i * 3 + j] = 1;
                        break;
                    case O:
                        _board[i * 3 + j] = 2;
                        break;
                    case Empty:
                        _board[i * 3 + j] = 0;
                        break;
                }
            }
        }
        _boardHash = board.hashCode();
    }

    @Override
    public void Win(Board board) {
        _gameStatus = 2;
    }

    @Override
    public void Lose(Board board) {
        _gameStatus = 3;
    }

    @Override
    public void Draw(Board board) {
        _gameStatus = 4;
    }
}
