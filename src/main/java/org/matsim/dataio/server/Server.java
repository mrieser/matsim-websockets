package org.matsim.dataio.server;

import spark.Spark;

/**
 * @author mrieser / Senozon AG
 */
public class Server {

	public static void main(String[] args) {
		Spark.port(9090);
		Spark.webSocket("/matsim/hello", HelloWebsocket.class);
		Spark.webSocket("/matsim/events", EventsWebsocket.class);
		Spark.init(); // Needed if you don't define any HTTP routes after your WebSocket routes
	}
}
