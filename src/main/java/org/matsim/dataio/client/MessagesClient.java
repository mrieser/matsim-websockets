package org.matsim.dataio.client;

/**
 * @author mrieser / Senozon AG
 */
public class MessagesClient {

	public static void main(String[] args) {
		String uri = "ws://localhost:9090/matsim/messages";

		MessagesSocket socket = new MessagesSocket();
		WebsocketHelper helper = null;

		try {
			helper = WebsocketHelper.connect(socket, uri);
			socket.awaitConnect();

			socket.startBinaryListening();
			Thread.sleep(5_000);
			socket.stopListening();

			socket.startListening();
			Thread.sleep(5_000);

			socket.beSlow(true);

			Thread.sleep(20_000);

			socket.stopListening();

			socket.close();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				if (helper != null) {
					helper.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
