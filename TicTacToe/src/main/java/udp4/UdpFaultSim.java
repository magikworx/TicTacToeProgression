package udp4;

import udp.Base;
import udp.IdBased;
import util.Optional;
import util.Triplet;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
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

            Hashtable<Byte, byte[]> history = new Hashtable();
            boolean shouldExit = false;
            while (!shouldExit) {
                Triplet<InetAddress, Integer, byte[]> requestPacket = IdBased.Instance.receive(serverSock);
                byte[] requestBuffer = requestPacket.get_third();

                if (requestBuffer.length == 0) {
                    IdBased.Instance.send(clientSock, requestPacket.get_first(), _outgoingPort, requestBuffer);
                    shouldExit = true;
                    continue;
                }

                // drop packets
                if (_r.nextBoolean()) {
                    continue;
                }

                // on successful receipt of packet, populate the receive packet object
                IdBased.Instance.send(clientSock, outgoingIp, _outgoingPort, requestBuffer);
                Triplet<InetAddress, Integer, byte[]> replyPacket = IdBased.Instance.receive(clientSock);

                byte[] replyBuffer = IdBased.Instance.pack(replyPacket.get_third());
                // shuffle packets randomly
                if (_r.nextBoolean()) {
                    replyBuffer = shuffle(replyBuffer);
                }

                // drop packets
                if (_r.nextBoolean()) {
                    continue;
                }

                history.put(replyBuffer[0], replyBuffer);
                if(history.size() >= 3) {
                    for(var buff : history.values()){
                        Base.Instance.send(serverSock, requestPacket.get_first(), requestPacket.get_second(), buff);
                    }
                    history.clear();
                }
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
