package nl.camilstaps.rbn;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class RecordCounter implements NewRecordListener {
	private final int fullCounters[] = {0,0,0,0,0};
	private final int matchedCounters[] = {0,0,0,0,0};
	
	private NewCountListener listener;

	public RecordCounter() {
		TimerTask moveCountsTask = new TimerTask() {
			@Override
			public void run() {
				fullCounters[0] = fullCounters[1];
				fullCounters[1] = fullCounters[2];
				fullCounters[2] = fullCounters[3];
				fullCounters[3] = fullCounters[4];
				fullCounters[4] = 0;
				
				matchedCounters[0] = matchedCounters[1];
				matchedCounters[1] = matchedCounters[2];
				matchedCounters[2] = matchedCounters[3];
				matchedCounters[3] = matchedCounters[4];
				matchedCounters[4] = 0;
				
				if (listener != null)
					listener.onNewMinuteAverage(getMinuteAverage(true), getMinuteAverage(false));
			}
		};

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(moveCountsTask, 15000, 15000);
	}
	
	public int getMinuteAverage(boolean onlyMatched) {
		if (onlyMatched)
			return Math.max(matchedCounters[0], matchedCounters[4]) +
					matchedCounters[1] + matchedCounters[2] + matchedCounters[3];
		else
			return Math.max(fullCounters[0], fullCounters[4]) +
					fullCounters[1] + fullCounters[2] + fullCounters[3];
	}
	
	public void setNewCountListener(NewCountListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean receivesAll() {
		return true;
	}

	@Override
	public void receive(Entry entry, boolean matchesFilter) {
		fullCounters[4]++;
		if (matchesFilter)
			matchedCounters[4]++;
	}

	@Override
	public void unparsable(String line, ParseException e) {
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onReconnected() {
	}
	
	public interface NewCountListener {
		void onNewMinuteAverage(int all, int matched);
	}
}
