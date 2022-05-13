package version1.phases.phase1;

import version1.tictactoe.BoardMarkers;
import version1.tictactoe.GameStates;
import version1.tictactoe.MoveValidationErrors;
import version1.tictactoe.channels.IServerChannel;
import util.Pair;

import java.io.IOException;
import java.net.DatagramSocket;

public class UdpChannelServer implements IServerChannel {
    public DatagramSocket _sock;
    public UdpChannelServer(){
        try {
            _sock = new DatagramSocket(8889);
        } catch (IOException ignored){}
    }
    @Override
    public Pair<Integer, Integer> getMove() {

        return null;
    }

    @Override
    public void validated(MoveValidationErrors validationResult) {

    }

    @Override
    public void updateStatus(GameStates state, BoardMarkers[][] board) {

    }
}
