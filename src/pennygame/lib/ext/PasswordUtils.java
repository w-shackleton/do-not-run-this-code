package pennygame.lib.ext;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pennygame.lib.GlobalPreferences;

public class PasswordUtils {
	
	/**
	 * Digests and encrypts the password
	 * @param rsaKey
	 * @param pass
	 * @return
	 */
	public static byte[] encryptPassword(PublicKey rsaKey, String pass) {
			System.out.println("Starting password processing");
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("SHA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
			byte[] passwd = pass.getBytes(Charset.forName("UTF-8"));
			md.update(GlobalPreferences.getBSalt());
			
			byte[] digest = md.digest(passwd);
			int iters = GlobalPreferences.getSaltiterations();
			for(int i = 0; i < iters; i++)
			{
				digest = md.digest(digest);
			}
			System.out.println("Digested passwd, got " + Base64.encodeBytes(digest));
			
			Cipher cipher = null;
			try {
				cipher = Cipher.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			}
			
			try {
				cipher.init(Cipher.ENCRYPT_MODE, rsaKey, new SecureRandom());
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
			
			byte[] cipherText = null;
			try {
				cipherText = cipher.doFinal(digest);
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
			System.out.println("Encrypted passwd");
			return cipherText;
	}
	
	/**
	 * Decrypts the password, into a digest
	 * @param cipherText
	 * @return
	 */
	public static final byte[] decryptPassword(PrivateKey key, byte[] cipherText) {
			System.out.println("Password received, processing");
			// Hard coded usrname + pass check. yep.
			
			Cipher cipher = null;
			try {
				cipher = Cipher.getInstance("RSA");
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				e1.printStackTrace();
			}
			
			try {
				cipher.init(Cipher.DECRYPT_MODE, key);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
			
			byte[] hashText = null;
			try {
				hashText = cipher.doFinal(cipherText);
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
			return hashText;
	}
}
