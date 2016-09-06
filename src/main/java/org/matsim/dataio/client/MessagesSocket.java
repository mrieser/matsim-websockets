package org.matsim.dataio.client;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author mrieser / Senozon AG
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class MessagesSocket {
	private final static Logger log = Logger.getLogger(MessagesSocket.class);

	private volatile boolean slowDown = false;

	private CompletableFuture<Boolean> connected = new CompletableFuture<>();
	private Session session = null;

	public MessagesSocket() {
	}

	public void beSlow(final boolean beSlow) {
		this.slowDown = beSlow;
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) 	{
		this.session = null;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) 	{
		this.session = session;
		this.connected.complete(true);
	}

	public void awaitConnect() {
		try {
			this.connected.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public void startListening() {
		try {
			session.getRemote().sendString("start");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void startBinaryListening() {
		try {
			session.getRemote().sendString("binstart");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void stopListening() {
		session.getRemote().sendStringByFuture("stop");
	}

	public void close() {
		session.close();
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		log.info("Got msg: " + msg);

		if (this.slowDown) {
			try {
				Thread.sleep(2_000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.info("-------: " + msg);
		}
	}

	@OnWebSocketMessage
	public void onMessage(byte[] bytes, int offset, int length) {
		log.info("Got binary msg: " + new String(bytes, offset, length));
	}
}
