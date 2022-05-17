package udp;

import util.Optional;
import util.Triplet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Checksummed extends Repeated {
    public static final Checksummed Instance = new Checksummed();

    protected Checksummed() {
    }

    public byte[] checksum(byte[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i += 2) {
            int num = (int) array[i] << 8;
            if (i + 1 < array.length) {
                num += array[i];
            }
            sum = Math.floorMod(sum + num, 0xffff);
        }
        var high = (byte) (((sum & 0xff00) >> 8) ^ 0xff);
        var low = (byte) ((sum & 0xff) ^ 0xff);
        return new byte[]{high, low};
    }

    public Optional<byte[]> unpack(byte[] buffer) {
        try {
            byte[] output = new byte[buffer[0]];
            System.arraycopy(buffer, 1, output, 0, output.length);
            byte[] cs = checksum(output);
            if (cs[0] == buffer[output.length + 1] && cs[1] == buffer[output.length + 2]) {
                return Optional.of(output);
            }
        } catch (ArrayIndexOutOfBoundsException | NegativeArraySizeException ignored) {
        }
        return Optional.empty();
    }

    public byte[] pack(byte[] buffer) {
        byte[] cs = checksum(buffer);
        byte[] output = new byte[buffer.length + cs.length + 1];

        output[0] = (byte) buffer.length;
        System.arraycopy(buffer, 0, output, 1, buffer.length);
        System.arraycopy(cs, 0, output, buffer.length + 1, cs.length);

        return output;
    }

    public void send(DatagramSocket sock, InetAddress receiverIp, int receiverPort,
                     byte[] buffer) throws IOException {
        super.send(sock, receiverIp, receiverPort, pack(buffer), 0, 1);
    }

    public Triplet<InetAddress, Integer, byte[]> receive(DatagramSocket sock) throws IOException {
        while (true) {
            Triplet<InetAddress, Integer, byte[]> response = super.receive(sock);
            var unpacked = unpack(response.get_third());
            if (unpacked.isPresent()) {
                return new Triplet<>(response.get_first(), response.get_second(), unpacked.get());
            }
        }
    }
}
