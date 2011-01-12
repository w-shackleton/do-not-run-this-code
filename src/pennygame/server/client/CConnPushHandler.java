package pennygame.server.client;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pennygame.lib.ext.Base64;
import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;

/**
 * Perhaps this should be made non-threaded, and pass its messages to the other main thread (too many...)
 * @author william
 *
 */

public class CConnPushHandler extends PushHandler {
	private final CConnMainThread.CConnMsgBacks cConnMsgBacks;
	
	public CConnPushHandler(NetReceiver producer, String threadID, CConnMainThread.CConnMsgBacks msgBacks) {
		super(producer, threadID);
		cConnMsgBacks = msgBacks;
	}

	@Override
	protected void processMessage(PennyMessage msg) {
		Class<? extends PennyMessage> cls = msg.getClass();
		if(cls.equals(MLoginRequest.class)) // Only once (contains RSA key to use)
		{
			MLoginRequest logReq = (MLoginRequest) msg;
			byte[] hashText = PasswordUtils.decryptPassword(cConnMsgBacks.getPrivateKey(), logReq.pass);
			String hashedPass = Base64.encodeBytes(hashText);
			// TODO: Check password, then call msgBacks.whatever()
		}
	}
}
