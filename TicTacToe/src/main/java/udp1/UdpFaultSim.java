package udp1;

import util.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class UdpFaultSim implements Runnable {
    public int _mode = 0;
    public int _incomingPort;
    public int _outgoingPort;

    public UdpFaultSim(int incomingPort, int outgoingPort) {
        _incomingPort = incomingPort;
        _outgoingPort = outgoingPort;
    }

    @Override
    public void run() {
        try (DatagramSocket serverSock = new DatagramSocket(_incomingPort);
             DatagramSocket clientSock = new DatagramSocket()) {
            // create a socket that can listen in on localhost port
            InetAddress outgoingIp = InetAddress.getByName("localhost");

            Random r = new Random();
            boolean shouldExit = false;
            while (!shouldExit) {
                DatagramPacket requestPacket = UDP.receiveRawPacket(serverSock);

                // drop packets randomly
                if (r.nextBoolean()){
                    continue;
                }

                if (requestPacket.getLength() == 0) {
                    UDP.send(clientSock, requestPacket.getAddress(), _outgoingPort, new byte[0]);
                    shouldExit = true;
                    continue;
                }

                // on successful receipt of packet, populate the receive packet object
                byte[] requestBuffer = requestPacket.getData();
                UDP.send(clientSock, outgoingIp, _outgoingPort, requestBuffer);
                DatagramPacket replyPacket = UDP.receiveRawPacket(clientSock);

                byte[] replyBuffer = replyPacket.getData();
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
