package pennygame.server.client;

import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;

public class Client extends QueuePair<ClientMainThread, ClientPushHandler> {

	@Override
	protected Socket createSocket() {
		return null;
	}

	@Override
	protected ClientMainThread createMainThread() {
		return null;
	}

	@Override
	protected ClientPushHandler createPushHandler(NetReceiver nr) {
		return null;
	}
}
