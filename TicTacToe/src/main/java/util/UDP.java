package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDP {
    public static byte[] receive(DatagramSocket sock) throws IOException {
        // allocate receive buffer
        byte[] buffer = new byte[1024];
        // create a packet object to receive UDP packets into
        DatagramPacket pack = new DatagramPacket(buffer, buffer.length);

        // blocking receive to wait for any communication.
        sock.receive(pack); // this will sit and wait indefinitely
        return pack.getData();
    }

    public static DatagramPacket receiveRawPacket(DatagramSocket sock) throws IOException {
        // allocate receive buffer
        byte[] buffer = new byte[1024];
        // create a packet object to receive UDP packets into
        DatagramPacket pack = new DatagramPacket(buffer, buffer.length);

        // blocking receive to wait for any communication.
        sock.receive(pack); // this will sit and wait indefinitely
        return pack;
    }

    public static void send(DatagramSocket sock, InetAddress receiverIp, int receiverPort, byte[] buffer)
            throws IOException {
        DatagramPacket pack = new DatagramPacket(buffer, buffer.length, receiverIp, receiverPort);
        sock.send(pack);
    }

    public static void bestEffortSend(DatagramSocket sock, InetAddress receiverIp, int receiverPort, byte[] buffer,
                                      int timeout, int retries) throws IOException {
        DatagramPacket pack = new DatagramPacket(buffer, buffer.length, receiverIp, receiverPort);
        for (int i = 0; i < retries; ++i) {
            sock.send(pack);
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ignored){}
        }
    }

    public static Optional<byte[]> sendAndReceiveWithTimeout(DatagramSocket sock, InetAddress receiverIp,
                                                           int receiverPort, byte[] buffer, int timeout,
                                                           int retries) throws IOException {
        Optional<byte[]> output = Optional.empty();
        for(int i = 0; i < retries || retries < 0; ++i) {
            try {
                sock.setSoTimeout(timeout);
                send(sock, receiverIp, receiverPort, buffer);
                byte[] reply = receive(sock);
                output = Optional.of(reply);
                break;
            } catch (SocketTimeoutException e) {
                // rethrow if no more retries
                if(retries > 0 && i == retries - 1) {
                    throw e;
                }
            } finally {
                sock.setSoTimeout(0);
            }
        }

        return output;
    }
}
