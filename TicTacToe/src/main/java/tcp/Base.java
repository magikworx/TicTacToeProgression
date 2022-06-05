package tcp;

import java.io.*;
import java.net.Socket;

public class Base {
    public static final Base Instance = new Base();

    protected Base() {
    }

    public void send(Socket sock, byte[] buffer) throws IOException {
        OutputStream out = sock.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(buffer.length);
        if (buffer.length > 0) {
            dos.write(buffer, 0, buffer.length);
        }
    }

    public byte[] receive(Socket sock) throws IOException {
        // Again, probably better to store these objects references in the support class
        InputStream in = sock.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        int len = dis.readInt();
        byte[] output = new byte[len];
        if (len > 0) {
            dis.readFully(output);
        }
        return output;
    }

    public byte[] sendAndReceive(Socket sock, byte[] buffer) throws IOException {
        send(sock, buffer);
        return receive(sock);
    }
}
