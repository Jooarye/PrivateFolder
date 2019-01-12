package de.jooarye.security.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

public class CryptUtils {

    public static boolean isKeyValid(String key) {
        return key.length() == 16 || key.length() == 32 || key.length() == 64 || key.length() == 128 || key.length() == 256;
    }

    public static void encryptFile(File file, SecretKey key) throws Exception {
        byte[] content = FileUtils.readFileToByteArray(file);
        content = FileUtils.readFileToByteArray(file);
        byte[] encrypted = CryptUtils.encrypt(key, content);
        File newFile = CryptUtils.generateEncryptedFile(key, file);
        FileUtils.writeByteArrayToFile(newFile, encrypted);
        file.delete();
    }

    public static void decryptFile(File file, SecretKey key) throws Exception {
        byte[] content = FileUtils.readFileToByteArray(file);
        content = FileUtils.readFileToByteArray(file);
        byte[] encrypted = CryptUtils.decrypt(key, content);
        File newFile = CryptUtils.generateDecryptedFile(key, file);
        FileUtils.writeByteArrayToFile(newFile, encrypted);
        file.delete();
    }

    private static byte[] encrypt(SecretKey key, byte[] content) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] encrypted = null;
        cipher.init(1, key);
        encrypted = Base64.encodeBase64(cipher.doFinal(content));
        return encrypted;
    }

    private static byte[] decrypt(SecretKey key, byte[] content) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] decrypted = null;
        cipher.init(2, key);
        decrypted = cipher.doFinal(Base64.decodeBase64(content));
        return decrypted;
    }

    public static String encryptStr(SecretKey key, String str) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        String encrypted = null;
        cipher.init(1, key);
        encrypted = Base58.encodeToString(cipher.doFinal(str.getBytes()));
        return encrypted;
    }

    public static String decryptStr(SecretKey key, String str) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        String decrypted = null;
        cipher.init(2, key);
        decrypted = new String(cipher.doFinal(Base58.decodeToBytes(str)));
        return decrypted;
    }

    public static SecretKey generateKey(String key) throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] keyBytes = key.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        return secretKey;
    }

    public static File generateEncryptedFile(SecretKey key, File file) throws GeneralSecurityException {
        String newName = encryptStr(key, file.getName());
        File newFile = new File(file.getParent() + "\\" + newName);
        return newFile;
    }

    public static File generateDecryptedFile(SecretKey key, File file) throws GeneralSecurityException {
        String newName = decryptStr(key, file.getName());
        File newFile = new File(file.getParent() + "\\" + newName);
        return newFile;
    }

}
