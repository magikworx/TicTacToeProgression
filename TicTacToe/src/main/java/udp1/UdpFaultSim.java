package udp1;

import udp.Base;
import udp.Checksummed;
import util.Triplet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class UdpFaultSim implements Runnable {
    public Random _r = new Random();
    public int _incomingPort;
    public int _outgoingPort;

    public UdpFaultSim(int incomingPort, int outgoingPort) {
        _incomingPort = incomingPort;
        _outgoingPort = outgoingPort;
    }

    public byte[] shuffle(byte[] array) {
        // Starting from the last element and swapping one by one.
        array = array.clone();
        for (int i = 0; i < array.length - 1; i++) {
            // Pick a random index from 0 to i
            int j = _r.nextInt(array.length - i);
            // Swap array[i] with the element at random index
            byte temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return array;
    }

    @Override
    public void run() {
        try (DatagramSocket serverSock = new DatagramSocket(_incomingPort);
             DatagramSocket clientSock = new DatagramSocket()) {
            // create a socket that can listen in on localhost port
            InetAddress outgoingIp = InetAddress.getByName("localhost");

            boolean shouldExit = false;
            while (!shouldExit) {
                Triplet<InetAddress, Integer, byte[]> requestPacket = Checksummed.Instance.receive(serverSock);
                byte[] requestBuffer = requestPacket.get_third();

                if (requestBuffer.length == 0) {
                    Checksummed.Instance.send(clientSock, requestPacket.get_first(), _outgoingPort, requestBuffer);
                    shouldExit = true;
                    continue;
                }

                // drop packets
                if (_r.nextBoolean()) {
                    continue;
                }

                // on successful receipt of packet, populate the receive packet object
                Triplet<InetAddress, Integer, byte[]> replyPacket =
                        Checksummed.Instance.sendAndReceive(clientSock, outgoingIp, _outgoingPort, requestBuffer);

                // drop packets
                if (_r.nextBoolean()) {
                    continue;
                }

                Base.Instance.send(serverSock, requestPacket.get_first(), requestPacket.get_second(), replyPacket.get_third());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Launch(int incomingPort, int outgoingPort) {
        UdpFaultSim p1 = new UdpFaultSim(incomingPort, outgoingPort);
        new Thread(p1).start();
    }
}
