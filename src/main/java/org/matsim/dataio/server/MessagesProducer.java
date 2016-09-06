package org.matsim.dataio.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author mrieser / Senozon AG
 */
public class MessagesProducer {

	private MessageCreator creator = null;
	private final List<MessageListener> listeners = new CopyOnWriteArrayList<>();

	public MessagesProducer() {
	}

	public interface MessageListener {
		void listen(String message);
	}

	public void startMessages() {
		stopMessages();

		this.creator = new MessageCreator();
		Thread messageCreator = new Thread(this.creator);
		messageCreator.setDaemon(true);
		messageCreator.start();
	}

	public void stopMessages() {
		if (this.creator != null) {
			this.creator.stop();
		}
	}

	public void addListener(MessageListener listener) {
		this.listeners.add(listener);
	}

	private void broadcastMessage(String message) {
		for (MessageListener listener: this.listeners) {
			listener.listen(message);
		}
	}

	private class MessageCreator implements Runnable {
		private long id = 0;
		private volatile boolean stop = false;

		@Override
		public void run() {
			while (!stop) {
				String message = "message " + id;
				id++;
				broadcastMessage(message);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void stop() {
			this.stop = true;
		}
	}

}
