package server;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by AndyRadulescu on 5/31/2017.
 * This class is used to encrypt or decrypt the message to be sent to the server.
 */
public class Encryption {
    public static final String DEFAULT_ENCODING = "UTF-8";
    static BASE64Encoder enc = new BASE64Encoder();
    static BASE64Decoder dec = new BASE64Decoder();

    public static String base64encode(String text) {
        try {
            return enc.encode(text.getBytes(DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }//base64encode

    public static String base64decode(String text) {
        try {
            return new String(dec.decodeBuffer(text), DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }
    }//base64decode

    /**
     * XORs the message , encrypting it , call it twice to decrypt .
     *
     * @param message
     * @param key
     * @returns the encrypted or decrypted string.
     */
    public static String xorMessage(String message, String key) {
        try {
            if (message == null || key == null) return null;
            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();
            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];
            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }
            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }
}