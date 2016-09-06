package org.matsim.dataio.client;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;

/**
 * @author mrieser / Senozon AG
 */
public final class WebsocketHelper {

	private final WebSocketClient client = new WebSocketClient();

	private WebsocketHelper() {
	}

	public static WebsocketHelper connect(MessagesSocket socket, String uri) {
		WebsocketHelper helper = new WebsocketHelper();

		try {
			helper.client.start();

			URI serviceUri = new URI(uri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			helper.client.connect(socket, serviceUri, request);

			return helper;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void close() {
		try {
			this.client.stop();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
