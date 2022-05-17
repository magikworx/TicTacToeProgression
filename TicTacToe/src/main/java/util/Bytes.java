package util;

public class Bytes {
    public static byte[] convert(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        int i = 0;
        for (Byte b : oBytes) bytes[i++] = b;
        return bytes;
    }

    public static Byte[] convert(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b;
        return bytes;
    }
}
