package nl.camilstaps.util;

import java.util.ArrayList;
import java.util.List;

public class Logger implements Thread.UncaughtExceptionHandler {
	private static Logger instance;

	public static Logger getInstance() {
		if (instance == null)
			instance = new Logger();

		return instance;
	}

	private final StringBuilder content = new StringBuilder();
	private final List<LoggerListener> listeners = new ArrayList<>();

	private Logger() {
	}

	public void setAsUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public void addListener(LoggerListener listener) {
		listeners.add(listener);
	}

	public synchronized void addEntry(String entry) {
		content
				.append(System.currentTimeMillis())
				.append("\t")
				.append(entry)
				.append("\r\n");

		onUpdatedContent();
	}

	public synchronized void addStackTrace(Throwable e) {
		content
				.append(System.currentTimeMillis())
				.append("\t")
				.append(e.getMessage())
				.append("\r\n");

		for (StackTraceElement el : e.getStackTrace())
			content
					.append("\t")
					.append(el.toString())
					.append("\r\n");

		onUpdatedContent();
	}

	public synchronized String getContent() {
		return content.toString();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		addEntry("Uncaught exception from thread " + t.getName() + " / " +  t.getId() + " / " + t.toString());
		addStackTrace(e);
	}

	private void onUpdatedContent() {
		for (LoggerListener listener : listeners)
			listener.onUpdatedContent();
	}

	public interface LoggerListener {
		void onUpdatedContent();
	}
}
