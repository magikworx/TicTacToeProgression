package udp;

import util.Triplet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Base {
    public static final Base Instance = new Base();

    protected Base() {
    }

    public void send(DatagramSocket sock, InetAddress receiverIp, int receiverPort, byte[] buffer)
            throws IOException {
        send(sock, receiverIp, receiverPort, buffer, 0, 1);
    }

    public void send(DatagramSocket sock, InetAddress receiverIp, int receiverPort, byte[] buffer,
                     int timeout, int retries) throws IOException {
        DatagramPacket pack = new DatagramPacket(buffer, buffer.length, receiverIp, receiverPort);
        for (int i = 0; i < retries; ++i) {
            sock.send(pack);
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public Triplet<InetAddress, Integer, byte[]> receive(DatagramSocket sock) throws IOException {
        // allocate receive buffer
        byte[] buffer = new byte[1024];
        // create a packet object to receive UDP packets into
        DatagramPacket pack = new DatagramPacket(buffer, buffer.length);

        // blocking receive to wait for any communication.
        sock.receive(pack); // this will sit and wait indefinitely
        byte[] output = new byte[pack.getLength()];
        System.arraycopy(pack.getData(), 0, output, 0, output.length);
        return new Triplet<>(pack.getAddress(), pack.getPort(), output);
    }

    public Triplet<InetAddress, Integer, byte[]> sendAndReceive(DatagramSocket sock,
                                                                InetAddress receiverIp,
                                                                int receiverPort,
                                                                byte[] buffer) throws IOException {
        send(sock, receiverIp, receiverPort, buffer);
        return receive(sock);
    }
}
