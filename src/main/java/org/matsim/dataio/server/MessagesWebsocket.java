package org.matsim.dataio.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author mrieser / Senozon AG
 */
@WebSocket
public class MessagesWebsocket {
	private static final Queue<Session> listeners = new ConcurrentLinkedQueue<>();
	private static final Queue<Session> binaryListeners = new ConcurrentLinkedQueue<>();

	private static MessagesProducer msgProducer;

	/**
	 * Workaround for initialization, as the constructor is not called by us, but by the framework.
	 * Should no longer be needed with Spark 2.6, hopefully.
	 */
	/*package*/ static void init(MessagesProducer msgProducer) {
		MessagesWebsocket.msgProducer = msgProducer;
	}

	public MessagesWebsocket() {
		// part of the workaround, msgProducer should be passed as Constructor-argument
		MessagesWebsocket.msgProducer.addListener(message -> broadcastMessage(message));
	}

	@OnWebSocketConnect
	public void connected(Session session) {
		// nothing to do
	}

	@OnWebSocketClose
	public void closed(Session session, int statusCode, String reason) {
		listeners.remove(session);
	}

	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		if ("start".equalsIgnoreCase(message)) {
			listeners.add(session);
			System.out.println("NEW SUBSCRIBER!");
		}
		if ("binstart".equalsIgnoreCase(message)) {
			binaryListeners.add(session);
			System.out.println("NEW BINARY SUBSCRIBER!");
		}
		if ("stop".equalsIgnoreCase(message)) {
			listeners.remove(session);
			binaryListeners.remove(session);
			System.out.println("bye subscriber");
		}
	}

	private static void broadcastMessage(String message) {
		for (Session session : listeners) {
			try {
				session.getRemote().sendString(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ByteBuffer bytes = ByteBuffer.wrap(message.getBytes());
		for (Session session : binaryListeners) {
			try {
				session.getRemote().sendBytes(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
