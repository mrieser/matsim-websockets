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
public class EventsWebsocket {
	private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
	private static final Queue<Session> binarySessions = new ConcurrentLinkedQueue<>();

	public EventsWebsocket() {
		Thread eventsCreator = new Thread(new EventsCreator());
		eventsCreator.setDaemon(true);
		eventsCreator.start();
	}

	@OnWebSocketConnect
	public void connected(Session session) {
		// nothing to do
	}

	@OnWebSocketClose
	public void closed(Session session, int statusCode, String reason) {
		sessions.remove(session);
	}

	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		if ("start".equalsIgnoreCase(message)) {
			sessions.add(session);
			System.out.println("NEW SUBSCRIBER!");
		}
		if ("binstart".equalsIgnoreCase(message)) {
			binarySessions.add(session);
			System.out.println("NEW BINARY SUBSCRIBER!");
		}
		if ("stop".equalsIgnoreCase(message)) {
			sessions.remove(session);
			binarySessions.remove(session);
			System.out.println("bye subscriber");
		}
	}

	private static void broadcastEvent(String event) {
		for (Session session : sessions) {
			try {
				session.getRemote().sendString(event);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ByteBuffer bytes = ByteBuffer.wrap(event.getBytes());
		for (Session session : binarySessions) {
			try {
				session.getRemote().sendBytes(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class EventsCreator implements Runnable {
		long id = 0;
		@Override
		public void run() {
			while (true) {
				String event = "event " + id;
				id++;
				broadcastEvent(event);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
