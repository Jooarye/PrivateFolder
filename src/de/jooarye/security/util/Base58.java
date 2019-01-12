package de.jooarye.security.util;

import java.math.BigInteger;

public class Base58 {
    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(58L);

    public static byte[] encodeToBytes(byte[] input) {
        return encodeToString(input).getBytes();
    }

    public static byte[] encodeToBytes(String input) {
        return encodeToString(input.getBytes()).getBytes();
    }

    public static String encodeToString(String input) {
        return encodeToString(input.getBytes());
    }

    public static String encodeToString(byte[] input) {
        BigInteger bi = new BigInteger(1, input);
        StringBuffer s = new StringBuffer();
        while (bi.compareTo(BASE) >= 0) {
            BigInteger mod = bi.mod(BASE);
            s.insert(0, "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".charAt(mod.intValue()));
            bi = bi.subtract(mod).divide(BASE);
        }
        s.insert(0, "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".charAt(bi.intValue()));

        byte[] arrayOfByte = input;int j = input.length;
        for (int i = 0; i < j; i++) {
            byte anInput = arrayOfByte[i];
            if (anInput != 0) {
                break;
            }
            s.insert(0, "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".charAt(0));
        }
        return s.toString();
    }

    public static String decodeToString(byte[] input) {
        return String.valueOf(decodeToBytes(String.valueOf(input)));
    }

    public static String decodeToString(String input) {
        return String.valueOf(decodeToBytes(input));
    }

    public static byte[] decodeToBytes(byte[] input) {
        return decodeToBytes(String.valueOf(input));
    }

    public static byte[] decodeToBytes(String input) {
        byte[] bytes = decodeToBigInteger(input).toByteArray();
        boolean stripSignByte = (bytes.length > 1) && (bytes[0] == 0) && (bytes[1] < 0);
        int leadingZeros = 0;
        for (int i = 0; input.charAt(i) == "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".charAt(0); i++) {
            leadingZeros++;
        }
        byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0) + leadingZeros];
        System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros, tmp.length - leadingZeros);
        return tmp;
    }

    private static BigInteger decodeToBigInteger(String input) {
        BigInteger bi = BigInteger.valueOf(0L);
        for (int i = input.length() - 1; i >= 0; i--) {
            int alphaIndex = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".indexOf(input.charAt(i));
            if (alphaIndex == -1) {
                throw new IllegalArgumentException("In Base58.decodeToBigInteger(), Illegal character " +
                        input.charAt(i) + " at index " + i + ". Throwing new IlleglArgumentException.");
            }
            bi = bi.add(BigInteger.valueOf(alphaIndex).multiply(BASE.pow(input.length() - 1 - i)));
        }
        return bi;
    }
}
