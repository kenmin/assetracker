/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.util;

import java.math.BigInteger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * A simple Blowfish Encryption util <br/> All credit goes to:
 * http://tech.chitgoks.com/2009/07/15/encrypt-and-decrypt-using-blowfish-in-java/
 *
 * @author
 * http://tech.chitgoks.com/2009/07/15/encrypt-and-decrypt-using-blowfish-in-java/
 */
public class CryptoUtil {

    public static String encryptBlowfish(String to_encrypt, String strkey) {
        try {
            SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encoding = cipher.doFinal(to_encrypt.getBytes());
            BigInteger n = new BigInteger(encoding);
            return n.toString(16);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decryptBlowfish(String to_decrypt, String strkey) {
        try {
            byte[] kbytes = strkey.getBytes();
            SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");
            BigInteger n = new BigInteger(to_decrypt, 16);
            byte[] encoding = n.toByteArray();

            //SECURITY-344: fix leading zeros
            if (encoding.length % 8 != 0) {
                int length = encoding.length;
                int newLength = ((length / 8) + 1) * 8;
                int pad = newLength - length; //number of leading zeros
                byte[] old = encoding;
                encoding = new byte[newLength];
                for (int i = old.length - 1; i >= 0; i--) {
                    encoding[i + pad] = old[i];
                }
                //SECURITY-563: handle negative numbers
                if (n.signum() == -1) {
                    for (int i = 0; i < newLength - length; i++) {
                        encoding[i] = (byte) -1;
                    }
                }
            }
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decode = cipher.doFinal(encoding);
            return new String(decode);
        } catch (Exception e) {
            return null;
        }
    }
}
