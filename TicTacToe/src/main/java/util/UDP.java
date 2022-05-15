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

    public static class Checksummed {
        public static byte[] checksum(byte[] array) {
            int sum = 0;
            for(int i = 0; i < array.length; i += 2) {
                int num = (int)array[i] << 8;
                if (i+1 < array.length) {
                    num += array[i];
                }
                sum = Math.floorMod(sum + num, 0xffff);
            }
            var high = (byte)(((sum & 0xff00) >> 8)^0xff);
            var low = (byte)((sum & 0xff)^0xff);
            return new byte[] {high, low};
        }

        public static Optional<byte[]> unpack(byte[] buffer) {
            byte[] output = new byte[buffer[0]];
            System.arraycopy(buffer, 1, output, 0, output.length);
            byte[] cs = checksum(output);
            if (cs[0] == buffer[output.length + 1] && cs[1] == buffer[output.length + 2]) {
                return Optional.of(output);
            }
            return Optional.empty();
        }

        public static byte[] pack(byte[] buffer) {
            byte[] cs = checksum(buffer);
            byte[] output = new byte[buffer.length + cs.length + 1];

            output[0] = (byte)buffer.length;
            System.arraycopy(buffer, 0, output, 1, buffer.length);
            System.arraycopy(cs, 0, output, buffer.length + 1, cs.length);

            return output;
        }

        public static byte[] receive(DatagramSocket sock) throws IOException {
            while(true){
                byte[] packet = UDP.receive(sock);
                var packet_res = unpack(packet);
                if (packet_res.isPresent()) {
                    return packet_res.get();
                }
            }
        }

        public static void send(DatagramSocket sock, InetAddress receiverIp, int receiverPort,
                                byte[] buffer) throws IOException {
            byte[] toSend = pack(buffer);
            DatagramPacket packet = new DatagramPacket(toSend, toSend.length, receiverIp, receiverPort);
            sock.send(packet);
        }

        public static void bestEffortSend(DatagramSocket sock, InetAddress receiverIp, int receiverPort, byte[] buffer,
                                          int timeout, int retries) throws IOException {
            for (int i = 0; i < retries; ++i) {
                send(sock, receiverIp, receiverPort, buffer);
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ignored){}
            }
        }

        public static Optional<byte[]> sendAndReceive (DatagramSocket sock, InetAddress receiverIp,
                                                       int receiverPort, byte[] buffer, int timeout,
                                                       int retries) throws IOException {
            Optional<byte[]> output = Optional.empty();
            for (int i = 0; i < retries || retries < 0; ++i) {
                try {
                    sock.setSoTimeout(timeout);
                    send(sock, receiverIp, receiverPort, buffer);
                    byte[] reply = receive(sock);
                    output = Optional.of(reply);
                    break;
                } catch (SocketTimeoutException e) {
                    // rethrow if no more retries
                    if (retries > 0 && i == retries - 1) {
                        throw e;
                    }
                } finally {
                    sock.setSoTimeout(0);
                }
            }

            return output;
        }
    }
}
