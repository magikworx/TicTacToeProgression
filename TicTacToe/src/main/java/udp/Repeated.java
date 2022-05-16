package udp;

import util.Triplet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Repeated extends Base {
    public static final Repeated Instance = new Repeated();

    protected Repeated() {
        super();
    }

    protected int _timeout = 100;

    public void setTimeout(int timeout) {
        _timeout = timeout;
    }

    public Triplet<InetAddress, Integer, byte[]> sendAndReceive(DatagramSocket sock,
                                                                InetAddress receiverIp,
                                                                int receiverPort,
                                                                byte[] buffer) throws IOException {
        while (true) {
            try {
                try {
                    sock.setSoTimeout(_timeout);
                    send(sock, receiverIp, receiverPort, buffer);
                    return receive(sock);
                } finally {
                    sock.setSoTimeout(0);
                }
            } catch (SocketTimeoutException ignored) {
            }
        }
    }
}
