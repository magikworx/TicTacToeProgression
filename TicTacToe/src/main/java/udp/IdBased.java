package udp;

import util.Triplet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class IdBased extends Checksummed {
    public static final IdBased Instance = new IdBased();

    private ThreadLocal<Byte> _id;

    protected IdBased() {
    }

    public Triplet<InetAddress, Integer, byte[]> sendAndReceive(DatagramSocket sock,
                                                                InetAddress receiverIp,
                                                                int receiverPort,
                                                                byte[] buffer) throws IOException {
        if (_id == null) {
            _id = new ThreadLocal<>();
            _id.set((byte) 0);
        }
        byte id = _id.get();
        _id.set((byte) (id + 1));

        byte[] toSend = new byte[buffer.length + 1];
        toSend[0] = id;
        System.arraycopy(buffer, 0, toSend, 1, buffer.length);

        while (true) {
            try {
                try {
                    sock.setSoTimeout(_timeout);

                    send(sock, receiverIp, receiverPort, toSend);
                    Triplet<InetAddress, Integer, byte[]> response = receive(sock);

                    int length = response.get_third().length - 1;
                    if (length < 1) continue;

                    if (id != response.get_third()[0]) continue;

                    byte[] output = new byte[length];
                    System.arraycopy(response.get_third(), 1, output, 0, length);
                    return new Triplet<>(response.get_first(), response.get_second(), output);
                } finally {
                    sock.setSoTimeout(0);
                }
            } catch (SocketTimeoutException ignored) {
            }
        }
    }
}
