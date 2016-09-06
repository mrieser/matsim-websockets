package org.matsim.dataio.client;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author mrieser / Senozon AG
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class EventsSocket {
	private final CountDownLatch closeLatch;
	private volatile boolean slowDown = false;

	private final static Logger log = Logger.getLogger(EventsSocket.class);

	@SuppressWarnings("unused")
	private CompletableFuture<Session> session = new CompletableFuture<>();

	public EventsSocket() {
		this.closeLatch = new CountDownLatch(1);
	}

	public void beSlow(final boolean beSlow) {
		this.slowDown = beSlow;
	}

	public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
		return this.closeLatch.await(duration,unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) 	{
		System.out.printf("Connection closed: %d - %s%n",statusCode,reason);
		this.session = null;
		this.closeLatch.countDown(); // trigger latch
	}

	@OnWebSocketConnect
	public void onConnect(Session session) 	{
		System.out.printf("Got connect: %s%n",session);
		this.session.complete(session);
	}

	public void startListening() {
		try {
			Future<Void> fut;
			fut = session.get().getRemote().sendStringByFuture("start");
//		fut.get(2,TimeUnit.SECONDS); // wait for send to complete.
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void startBinaryListening() {
		try {
			session.get().getRemote().sendString("binstart");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void stopListening() {
		try {
		Future<Void> fut;
			fut = session.get().getRemote().sendStringByFuture("stop");
//		fut.get(2,TimeUnit.SECONDS); // wait for send to complete.
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			session.get().close(StatusCode.NORMAL,"I'm done");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
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
