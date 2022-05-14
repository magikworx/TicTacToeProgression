package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TCP {
    public static byte[] receive(DataInputStream in) throws IOException {
        byte[] bytes = new byte[0];
        int length = in.readInt(); // read length of incoming message
        if (length > 0) {
            bytes = new byte[length];
            in.readFully(bytes, 0, bytes.length); // read the message
        }
        return bytes;
    }

    public static void send(DataOutputStream out, byte[] buffer) throws IOException {
        out.writeInt(buffer.length);
        out.write(buffer);
    }
}
