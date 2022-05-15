package udp3;

import util.Optional;
import util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

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

        public static Optional<Pair<Byte, byte[]>> unpack(byte[] buffer) {
            byte[] message = new byte[buffer[0] + 4];
            System.arraycopy(buffer, 0, message, 0, message.length);
            buffer = message;
            byte cs1 = buffer[buffer.length - 2];
            byte cs0 = buffer[buffer.length - 1];
            buffer[buffer.length - 2] = 0;
            buffer[buffer.length - 1] = 0;
            byte[] cs = checksum(buffer);
            if (cs1 == cs[1] && cs0 == cs[0]){
                byte id = buffer[1];
                byte[] output = new byte[buffer[0]];
                System.arraycopy(buffer, 2, output, 0, output.length);
                return Optional.of(new Pair<>(id, output));
            }
            return Optional.empty();
        }

        public static byte _id = 0;
        public static byte[] pack(byte[] buffer){
            return pack(_id++, buffer);
        }
        public static byte[] pack(byte id, byte[] buffer) {
            byte[] output = new byte[buffer.length + 4];
            output[0] = (byte)buffer.length;
            output[1] = id;
            System.arraycopy(buffer, 0, output, 2, buffer.length);

            byte[] cs = checksum(output);
            output[output.length-2] = cs[1];
            output[output.length-1] = cs[0];

            return output;
        }

        public static Pair<Byte, byte[]> receive(DatagramSocket sock) throws IOException {
            while(true){
                byte[] packet = UDP.receive(sock);
                var packet_res = unpack(packet);
                if (packet_res.isPresent()) {
                    return packet_res.get();
                }
            }
        }

        public static byte send(DatagramSocket sock, InetAddress receiverIp, int receiverPort,
                                byte[] buffer) throws IOException {
            byte[] toSend = pack(buffer);
            DatagramPacket packet = new DatagramPacket(toSend, toSend.length, receiverIp, receiverPort);
            sock.send(packet);
            return toSend[1];
        }

        public static byte send(DatagramSocket sock, InetAddress receiverIp, int receiverPort,
                                byte id, byte[] buffer) throws IOException {
            byte[] toSend = pack(id, buffer);
            DatagramPacket packet = new DatagramPacket(toSend, toSend.length, receiverIp, receiverPort);
            sock.send(packet);
            return buffer[1];
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
            ArrayBlockingQueue<Byte> ids = new ArrayBlockingQueue<>(10);
            for (int i = 0; i < retries || retries < 0; ++i) {
                try {
                    sock.setSoTimeout(timeout);
                    byte id = send(sock, receiverIp, receiverPort, buffer);
                    if(ids.remainingCapacity() == 0) ids.remove();
                    ids.add(id);
                    Pair<Byte, byte[]> reply = receive(sock);
                    if(ids.contains(reply.get_first())) {
                        output = Optional.of(reply.get_second());
                        break;
                    }
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
