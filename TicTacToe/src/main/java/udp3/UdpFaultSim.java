package udp3;

import java.io.IOException;
import java.net.DatagramPacket;
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
                DatagramPacket requestPacket = UDP.receiveRawPacket(serverSock);
                var request_res = UDP.Checksummed.unpack(requestPacket.getData());
                if (request_res.isPresent() && request_res.get().get_second().length == 0) {
                    UDP.Checksummed.bestEffortSend(clientSock, requestPacket.getAddress(), _outgoingPort,
                            request_res.get().get_second(), 100, 10);
                    shouldExit = true;
                    continue;
                }

                // on successful receipt of packet, populate the receive packet object
                byte[] requestBuffer = requestPacket.getData();
                UDP.send(clientSock, outgoingIp, _outgoingPort, requestBuffer);
                DatagramPacket replyPacket = UDP.receiveRawPacket(clientSock);

                byte[] replyBuffer = replyPacket.getData();
                // shuffle packets randomly
                if (_r.nextBoolean()){
                   replyBuffer = shuffle(replyBuffer);
                }
                // drop packets
                if (_r.nextBoolean()){
                    continue;
                }

                UDP.send(serverSock, requestPacket.getAddress(), requestPacket.getPort(), replyBuffer);
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
