package nl.camilstaps.rbn;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import nl.camilstaps.rbn.filter.Filter;

public final class Client implements NewRecordListener {
	private final TelnetClient client;
	private InputStream inputStream;
	private BufferedReader bufferedReader;

	private final String host;
	private final int port;
	private final String call;

	private final Collection<NewRecordListener> listeners = new ArrayList<>();

	private Filter filter;

	private boolean alive = false;
	private final static int RECONNECT_INTERVAL = 1000;

	public Client(String call, String host, int port) throws IOException {
		this.call = call;
		this.host = host;
		this.port = port;

		client = new TelnetClient();
		client.setConnectTimeout(2000);

		connect();
	}

	public void connect() throws IllegalStateException, IOException {
		if (alive)
			throw new IllegalStateException("connect call while connected");

		client.connect(host, port);
		client.setKeepAlive(true);

		inputStream = client.getInputStream();
		final PrintStream printStream = new PrintStream(client.getOutputStream());

		readUntil("Please enter your call:");
		printStream.print(call + "\r\n");
		printStream.flush();
		readUntil(">\r\n\r\n");

		alive = true;

		bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		(new Thread(new ClientThread())).start();
	}

	public void register(NewRecordListener listener) {
		listeners.add(listener);
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	private void readUntil(String pattern) throws IOException {
		char lastChar = pattern.charAt(pattern.length() - 1);
		StringBuilder sb = new StringBuilder();
		int c;

		while((c = inputStream.read()) != -1) {
			char ch = (char) c;
			sb.append(ch);
			if(ch == lastChar && sb.toString().endsWith(pattern))
				return;
		}
	}

	@Override
	public boolean receivesAll() {
		return true;
	}

	@Override
	public void receive(Entry entry, boolean matchesFilter) {
		for (NewRecordListener listener : listeners)
			if (matchesFilter || listener.receivesAll())
				listener.receive(entry, matchesFilter);
	}

	@Override
	public void unparsable(String line, ParseException e) {
		for (NewRecordListener listener : listeners)
			listener.unparsable(line, e);
	}

	@Override
	public void onDisconnected() {
		alive = false;
		for (NewRecordListener listener : listeners)
			listener.onDisconnected();

		try {
			final Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						connect();
						onReconnected();
						timer.cancel();
					} catch (Exception e) {}
				}
			}, 0, RECONNECT_INTERVAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReconnected() {
		for (NewRecordListener listener : listeners)
			listener.onReconnected();
	}

	private class ClientThread implements Runnable {
		@Override
		public void run() {
			while (alive) {
				try {
					processInput();
				} catch (SocketException e) {
					onDisconnected();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private void processInput() throws IOException {
			String line = bufferedReader.readLine();

			if (line == null) {
				onDisconnected();
				return;
			}

			try {
				Entry entry = Entry.factory(line);
				receive(entry, filter.matches(entry));
			} catch (ParseException e) {
				unparsable(line, e);
				e.printStackTrace();
				System.err.println(line);
			}
		}
	}
}