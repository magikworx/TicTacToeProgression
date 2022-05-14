package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
}
