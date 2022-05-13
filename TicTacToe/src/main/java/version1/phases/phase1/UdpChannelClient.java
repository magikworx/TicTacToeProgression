package version1.phases.phase1;

import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import version1.tictactoe.MoveValidationErrors;
import version1.tictactoe.channels.IClientChannel;
import util.Pair;
import util.UDP;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpChannelClient implements IClientChannel {
    public DatagramSocket _sock;
    public InetAddress _receiverIp;
    public int _receiverPort = 8889;

    public UdpChannelClient(){
        try {
            _sock = new DatagramSocket();
            // create a socket that can listen in on localhost port 8889
            // construct the UDP packet using the buffer and fill in the IP headers for
            // localhost:8889
            _receiverIp = InetAddress.getByName("localhost");
        } catch (Exception e) {
            if (_sock != null) {
                _sock.close();
                _sock = null;
            }
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize()
    {
        if (_sock != null) {
            _sock.close();
            _sock = null;
        }
    }

    @Override
    public MoveValidationErrors madeMove(int row, int col) {
        if(_sock != null) {
            try {
                byte[] buffer = {1, (byte) row, (byte) col};
                UDP.send(_sock, _receiverIp, _receiverPort, buffer);
                buffer = UDP.receive(_sock);
                switch (buffer[1]) {
                    case 0: return MoveValidationErrors.None;
                    case 1: return MoveValidationErrors.InvalidRow;
                    case 2: return MoveValidationErrors.InvalidColumn;
                    case 3: return MoveValidationErrors.AlreadyOccupied;
                }
            } catch (IOException ignored){ }
        }
        return MoveValidationErrors.None;
    }

    @Override
    public Pair<GameStates, BoardMarkers[][]> getUpdate() {
        if(_sock != null) {
            try {
                byte[] buffer = {0, 0, 0};
                UDP.send(_sock, _receiverIp, _receiverPort, buffer);
                buffer = UDP.receive(_sock);
                var state = GameStates.Waiting;
                switch (buffer[1]){
                    case 0: state = GameStates.Waiting; break;
                    case 1: state = GameStates.YourMove; break;
                    case 2: state = GameStates.Won; break;
                    case 3: state = GameStates.Lost; break;
                    case 4: state = GameStates.Draw; break;
                }
                var board = new BoardMarkers[3][3];
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; ++j){
                        int offset = i * 3 + j + 2;
                        if (buffer[offset] == 0) board[i][j] = BoardMarkers.Empty;
                        if (buffer[offset] == 1) board[i][j] = BoardMarkers.X;
                        if (buffer[offset] == 2) board[i][j] = BoardMarkers.O;
                    }
                }
                return new Pair<>(state, board);
            } catch (IOException ignored){ }
        }
        return new Pair<>(GameStates.Waiting, null);
    }
}
