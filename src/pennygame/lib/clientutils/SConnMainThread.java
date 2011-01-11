package pennygame.lib.clientutils;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.queues.MainThread;

public class SConnMainThread extends MainThread {
	
	final String pass, username;
	
	public SConnMainThread(String username, String pass, String threadID) {
		super(threadID);
		this.pass = pass;
		this.username = username;
	}
	
	@Override
	protected void loop() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void setup() {
		// TODO: Add login processing (Wait until request received from PushHandler)
	}
	
	public MsgBacks msgBacks = new MsgBacks();
	
	public class MsgBacks // This is a container of functions called by the PushHandler
	{
		public synchronized void onLoginKey(PublicKey rsaKey) {
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
				digest = md.digest(passwd);
			}
			System.out.println("Digested passwd");
			
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
			
			MLoginRequest req = new MLoginRequest(username, cipherText);
			putMessage(req);
		}
	};
}
