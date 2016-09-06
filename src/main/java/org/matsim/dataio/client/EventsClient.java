package org.matsim.dataio.client;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author mrieser / Senozon AG
 */
public class EventsClient {

	public static void main(String[] args) {
		String uri = "ws://localhost:9090/matsim/events";

		WebSocketClient client = new WebSocketClient();
		EventsSocket socket = new EventsSocket();

		try {
			client.start();

			URI serviceUri = new URI(uri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, serviceUri, request);
			System.out.printf("Connecting to : %s%n", serviceUri);

			socket.startBinaryListening();
			Thread.sleep(5_000);
			socket.stopListening();

			socket.startListening();
			Thread.sleep(5_000);

			socket.beSlow(true);

			Thread.sleep(20_000);

			socket.stopListening();
			socket.close();

			// wait for closed socket connection.
			socket.awaitClose(65, TimeUnit.SECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
