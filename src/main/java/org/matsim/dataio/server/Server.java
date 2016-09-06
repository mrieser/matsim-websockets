package org.matsim.dataio.server;

import spark.Spark;

/**
 * @author mrieser / Senozon AG
 */
public class Server {

	public static void main(String[] args) {
		MessagesProducer msgProducer = new MessagesProducer();
		msgProducer.startMessages();
		MessagesWebsocket.init(msgProducer);

		Spark.port(9090);
		Spark.webSocket("/matsim/hello", HelloWebsocket.class);
		Spark.webSocket("/matsim/messages", MessagesWebsocket.class);
		Spark.init(); // Needed if you don't define any HTTP routes after your WebSocket routes
	}
}
