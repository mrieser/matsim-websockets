package org.matsim.dataio.client;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author mrieser / Senozon AG
 */
public class HelloClient {

	public static void main(String[] args) {
		String uri = "ws://localhost:9090/matsim/hello";

		WebSocketClient client = new WebSocketClient();
		HelloSocket socket = new HelloSocket();

		try {
			client.start();

			URI serviceUri = new URI(uri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, serviceUri, request);
			System.out.printf("Connecting to : %s%n", serviceUri);

			// wait for closed socket connection.
			socket.awaitClose(5, TimeUnit.SECONDS);
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
